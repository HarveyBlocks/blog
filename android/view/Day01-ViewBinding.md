# ViewBinding

[视图绑定](https://developer.android.google.cn/topic/libraries/view-binding?hl=zh-cn)

## 获取View组件的传统方式

```kotlin
val button1: Button = super.findViewById(R.id.button1)
```

## ViewBinding 项目集成

在Model的build.gradle中启用

```kotlin
android {
    // ...
    viewBinding {
    	enabled = true
	}
}
```

在生成绑定类时忽略某个布局文件，则将`tools:viewBindingIgnore="true"`属性添加到相应布局文件的根视图中

```xml
<LinearLayout ... xmlns:tools="http://schemas.android.com/tools"
        tools:viewBindingIgnore="true" ...>
    <!--...-->
</LinearLayout>
```

## 有关委托

为了延迟加载有关资源, 以下是工具用委托, 保证运行时不报错

```kotlin

class NonWritablePropertyException(
    message: String? = null, cause: Throwable? = null
) : Exception(message, cause)

interface AnyDelegate<P> {
    operator fun getValue(ref: Any?, properties: KProperty<*>): P;

    operator fun setValue(ref: Any?, properties: KProperty<*>, value: P);
}

/**
 * 只可以写一次, 延迟初始化
 * 没有初始化, 多次赋值, 都会在在运行时异常, 但同时保证在编译时不会错误
 */
class LazyConstant<P> : AnyDelegate<P> {
    var filed: P? = null
    override operator fun getValue(ref: Any?, properties: KProperty<*>): P {
        if (filed == null) {
            throw ExceptionInInitializerError("do not initialize ${properties.name}");
        } else {
            return this.filed!!
        }
    }

    override operator fun setValue(ref: Any?, properties: KProperty<*>, value: P) {
        if (filed == null) {
            filed = value
        } else {
            throw NonWritablePropertyException("the ${properties.name} can not be written")
        }
    }
}

```

## Activity中使用

ViewBinding为xml文件生成Binding, 例如`main_activity.xml`就生成成`ActivityMainBinding`

```kotlin
import org.harvey.android.first.databinding.ActivityMainBinding
```

在`Activity`中的`onCreate`中改用`inflate`静态方法生成Binding实例

```kotlin
import org.harvey.android.first.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding by LazyConstant()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(super.layoutInflater);
        val view: View = binding.getRoot();
        setContentView(view); // 替换传统的setContentView
    }
}
```

可以直接获取有关成员了

```kotlin
binding.button1.setOnClickListener {
    // ...
}
```

## Fragment中使用

```kotlin
private lateinit var binding: ActivityMainBinding

override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    binding = ActivityMainBinding.inflate(inflater, container, false)
    val view: View = binding.getRoot()
    return view;
}
```

## Adapter中使用

```kotlin
private class MainAdapter(private val mList: MutableList<String?>) : RecyclerView.Adapter<MainAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 之前的写法
        // val view: View = LayoutInflater.from(parent.getContext())
        //     .inflate(R.layout.layout_comment, parent, false);
        // val holder: ViewHolder = ViewHolder(view);

        // 使用ViewBinding的写法
        val commentBinding: LayoutCommentBinding =
            LayoutCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        val holder = ViewHolder(commentBinding)
        return holder
    }

    override fun onBindViewHolder( holder: ViewHolder, position: Int) {
        holder.mTextView.setText(mList.get(position))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    // 之前的写法
    // ViewHolder(itemView:View) {
    //     super(itemView);
    //    mTextView = itemView.findViewById(R.id.tv_include);
    // }
    class ViewHolder( commentBinding: LayoutCommentBinding) :
        RecyclerView.ViewHolder(commentBinding.getRoot()) {
        var mTextView: TextView

        //使用ViewBinding的写法
        init {
            mTextView = commentBinding.tvInclude
        }
    }
}
```