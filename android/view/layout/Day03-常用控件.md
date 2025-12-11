# 常用控件

传统使用XML开发UI界面, 也有`androidx.constraintlayout.widget.ConstraintLayout`, 使用拖拽和GUI来进行开发

XML开发能更好地理解原理和进行细调, 故此处介绍这种方法

layout.xml框架

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
	<!--一些控件-->
</LinearLayout>
```

## TextView 文本显示

用于在界面上显示一段文本信息

```xml
<TextView
        android:id="@+id/textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="This is TextView" />
```

-   `android:id` 给当前控件定义了一个唯一标识符

-   `android:layout_width`和`android:layout_height`  指定控件的宽度和高度

    可选值三

    -   `match_parent` 让当前控件的**大小和父布局的大小一样**，也就是由父布局来决定当前控件的大小

        此处表示和父布局一样宽

    -   `wrap_content` 让当前控件的**大小能够刚好包含**住里面的内容，也就是由控件内容决定当前控件的大小

    -   固定值 表示给控件指定一个固定的尺寸，单位一般用`dp`

        `dp`是一种屏幕密度无关的尺寸单位，可以**保证在不同分辨率的手机上显示效果尽可能地一致**，如`50dp`

可选属性

-   ` android:gravity="center" ` 设置文本对齐方式
    -   可选值 `top`、`bottom`、`start`、 `end`、`center`
    -   用`|` 来同时指定多个值，例如`"center"`，等价于`"center_vertical|center_horizontal"`
-   `android:textColor="#00ff00"` 设置文本颜色
-   `android:textSize="24sp"` 设置文本大小,单位sp有助于用户修改系统文本大小时同时修改

## Button

```xml
<Button
        android:id="@+id/button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Button" />
```

-   `android:textAllCaps="false"` Android系统**默认会将按钮上的英文字母全部转换成大写**，使用false禁止这种情况

注册事件

API1

```kotlin
class MainActivity : AppCompatActivity(), View.OnClickListener { 
 
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState) 
        setContentView(R.layout.activity_main) 
        button.setOnClickListener(this) 
    } 
 
    override fun onClick(v: View?) { 
        when (v?.id) { 
            R.id.button -> { 
                // 在此处添加逻辑 
            } 
        } 
    } 
 
}
```

API2

```kotlin
class MainActivity : AppCompatActivity(), View.OnClickListener { 
 
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState) 
        setContentView(R.layout.activity_main) 
        button.setOnClickListener({
            // 添加逻辑
        }) 
    } 

}
```



## EditText 文本输入

允许用户在控件里输入和编辑内容，并可以在程序中对这些内容进行处理

```xml
<EditText android:id="@+id/editText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Type something here"/>
```

-   `android:hint` 提示文本

获取输入文本的方式

```kotlin
val inputText = editText.text.toString()
```



## ImageView

```kotlin
<ImageView
        android:id="@+id/imageView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/img_1"
        />
```

![image-20250909155402717](../../assetss/Day03-常用控件/image-20250909155402717.png)

在代码中修改文件

```kotlin
binding.imageView.setImageResource(R.drawable.img_2)
```

## ProgressBar 进度条

进度条, 虽然叫进度条, 其实默认是转圈圈

```kotlin
<ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        />
```

![image-20250909161504680](../../assetss/Day03-常用控件/image-20250909161504680.png)

可选属性`android:visibility`, 用于设置是否可见, 其值有三

-   `visible` 默认可见
-   `invisible` 不可见, 但是占据屏幕空间
-   `gone` 不可见, 不占用屏幕

也可以在代码中修改

```kotlin
binding.progressBar.visibility = View.VISIBLE
binding.progressBar.visibility = View.INVISIBLE
binding.progressBar.visibility = View.GONE
```

设置进度条样式, 添加属性`style="?android:attr/progressBarStyleHorizontal"`设置成一条

```xml
<ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="match_parent"
        android:layout_marginTop="40pt"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_height="wrap_content" />
```

在代码中调整进度

```kotlin
binding.button.setOnClickListener {
    binding.progressBar.progress += if(binding.progressBar.progress == 100) -100 else 10
}
```

## AlertDialog 弹窗

在当前界面弹出一个对话框，这个对话框是置顶于所有界面元素之上的，能够 屏蔽其他控件的交互能力

<img src="../../assetss/Day03-常用控件/image-20250909165521698.png" alt="image-20250909165521698" style="zoom:50%;" />





创建Alert

```kotlin
AlertDialog.Builder(this).run {
    setTitle("This is Dialog")
    setMessage("Something important.")
    setCancelable(false) // 不能通过BACK返回
    setPositiveButton("OK") { dialog, which ->
        toastShow(this@MainActivity, "OK")
    }
    setNegativeButton("Cancel"/*自动大写*/) { dialog, which ->
        toastShow(this@MainActivity, "cancel")
    }
    show()
}
```

