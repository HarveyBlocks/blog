# 使用

## 简单使用

### 创建布局

创建左侧的Fragment布局

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    <Button
            android:id="@+id/button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:text="Button"
            />
</LinearLayout>
```

创建右侧的Fragment布局

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical"
        android:background="#00ff00"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:textSize="24sp"
            android:text="This is right fragment"
            />
</LinearLayout>
```

### 创建Fragment类

```kotlin
import androidx.fragment.app.Fragment // 注意是 androidx.fragment.app 包下的

class LeftFragment : Fragment()
```

创建两个Fragment对应的view

```kotlin
class LeftFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_left, container, false)
    }
}
```

```kotlin
class RightFragment: Fragment(R.layout.fragment_right)
```

依据源码, 两种写法的效果是一致的

使用ViewBindings的抽取父类

```kotlin
typealias BindingInflater0<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

open class BaseFragment<T : ViewBinding>(val bindingInflater: BindingInflater0<T>) : Fragment() {
    lateinit var binding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = bindingInflater(inflater, container, false)
        return binding.root;
    }
}
```

子类实现

```kotlin
class LeftFragment : BaseFragment<FragmentLeftBinding>(FragmentLeftBinding::inflate)
```

```kotlin
class RightFragment: BaseFragment<FragmentRightBinding>(FragmentRightBinding::inflate)
```



### 注册到Activity

在Activity布局两个Fragment, 使其左右两边

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="horizontal"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

    <androidx.fragment.app.FragmentContainerView
            android:id="@+id/leftFragment"
            android:name="org.harvey.android.first.fragment.LeftFragment"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1" />

    <androidx.fragment.app.FragmentContainerView
            android:id="@+id/rightFragment"
            android:name="org.harvey.android.first.fragment.RightFragment"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1" />
</LinearLayout>
```

### 结果展示

![image-20250914233619324](../../assetss/Day04-使用/image-20250914233619324.png)

## 动态添加

目标是将右侧的fragment换成另一个fragment

创建另一个fragment的布局

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical"
        android:background="#FFEB3B"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:textSize="24sp"
            android:text="This is other right fragment"
            />
</LinearLayout>
```

创建对应的Fragment类

```kotlin
class OtherRightFragment: Fragment(R.layout.fragment_right_other)
```

修改activity_main.xml

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="horizontal"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

    <androidx.fragment.app.FragmentContainerView
            android:id="@+id/leftLayout"
            android:name="org.harvey.android.first.fragment.LeftFragment"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1" />

    <androidx.fragment.app.FragmentContainerView
            android:id="@+id/rightLayout"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1" />
</LinearLayout>
```

在代码里修改`name`

为了在leftFragment完成加载View之后再进行rightFragment的加载, 在LeftFragment里添加一个钩子字段

```kotlin
class LeftFragment : BaseFragment<FragmentLeftBinding>(FragmentLeftBinding::inflate) {
    var onViewCreatedListener: LeftFragment.() -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.onViewCreatedListener()
    }
}
```

在MainActivity中设置按钮按下, 更换RightFragment的逻辑

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val leftFragment = supportFragmentManager.findFragmentById(R.id.leftLayout)
        if (leftFragment !is LeftFragment) {
            return
        }
        leftFragment.onViewCreatedListener = {
            var other = false
            this.binding.button.setOnClickListener {
                other = !other
                val rightFragment = if (other) OtherRightFragment() else RightFragment()
                replaceFragment(R.id.rightLayout, rightFragment)
            }
        }
        replaceFragment(R.id.rightLayout, RightFragment()) //初始化
    }

    private fun replaceFragment(
        layoutId: Int,
        fragment: Fragment,
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(layoutId, fragment)
        transaction.commit()
    }
}
```

由此, `replaceFragment`其实使用了一个事务, supportFragmentManager是父类的属性



## 返回栈

按下back, 返回上一个Fragment

```kotlin
private fun replaceFragment(
    layoutId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(layoutId, fragment)
    if (addToBackStack) {
        transaction.addToBackStack(/* name = */ null)
    }
    transaction.commit()
}
```

测试发现, 按下Back, 会以类似Activity**standard的启动模式**的方式不断pop fragment

不断Back, 甚至能将Right的 所有Fragment全部pop, 包括用于初始化的Fragment

那么在OnCreate里, 在back时对Stack进行检查, 如果到了初始化的Fragment, 则Back全部

```kotlin
replaceFragment(R.id.rightLayout, RightFragment(), addToBackStack = false) //初始化
```

如果要保证两个Fragment交替, Back能返回, 而不会形成很深的栈

```kotlin
private fun replaceFragment(
    layoutId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
) {
    val transaction = supportFragmentManager.beginTransaction()
    if (addToBackStack && supportFragmentManager.backStackEntryCount > 0) {
        supportFragmentManager.popBackStack() // 删除上一个
    }
    transaction.replace(layoutId, fragment)
    if (addToBackStack) {
        transaction.addToBackStack(/* name = */ null)
    }
    transaction.commit()
}
```



## 和Activity的交互

### 从Activity中获取Fragment

```kotlin
val fragment = supportFragmentManager.findFragmentById(R.id.leftFrag) as LeftFragment 
```

使用KTX增强这一操作, KTX有对android增加了一些extension的操作, 是Java的android在Kotlin上的增强与扩展

先导入依赖

```kotlin
implementation("androidx.fragment:fragment-ktx:1.6.2")
```

然后结合viewBinding

```kotlin
val leftFragment = binding.leftLayout.findFragment<LeftFragment>()
```

findFragment实质上是对view这一类的extension, 取消了`as Fragment`的操作, 保证类型安全

### 从Fragment中获取Activity

直接用`activity`属性(`getActivity`)获取

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    // 1. 调用父类逻辑
    super.onViewCreated(view, savedInstanceState)

    // 2. 本 fragment 内部的逻辑
    // 2.1 获取本Fragment对象所在的Activity
    val mainActivity = activity as MainActivity
    Log.i("FragmentLeft","${mainActivity.binding.root.id}")
}
```



或者使用Fragment的方法`requireActivity()`

`requireActivity()` 的好处在于, 没有获取到Activity的时候抛出异常IllegalStateException而不是返回null

下面的例子是将注册button点击事件的工作从MainActivity转移到LeftFragment上

```kotlin
class LeftFragment : BaseFragment<FragmentLeftBinding>(FragmentLeftBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 1. 调用父类逻辑
        super.onViewCreated(view, savedInstanceState)

        // 2. 本 fragment 内部的逻辑
        // 2.1 获取本Fragment对象所在的Activity
        val mainActivity = requireActivity() as MainActivity
        var other = false
        this.binding.button.setOnClickListener {
            other = !other
            val rightFragment = if (other) OtherRightFragment() else RightFragment()
            mainActivity.replaceFragment(R.id.rightLayout, rightFragment)
        }
    }
}
```

### Fragment之间的通信

通过Activity间接实现

