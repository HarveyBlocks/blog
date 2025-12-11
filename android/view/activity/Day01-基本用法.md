# 基本用法

Activity 是一种可以包含用户界面的组件

用于和用户进行交互

一个应用程序中可以包含零个或多个Activity，但不包含任何Activity的应用程序很少见

## 添加

### 添加Activity类

![image-20250907153442377](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907153442377.png)

选择Empty Views Activity

![image-20250907155850868](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907155850868.png)

```kotlin
package org.harvey.android.first

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
```

manifest自动添加

```xml
<activity
    android:name=".FirstActivity"
    android:exported="false" />
<!--...-->
```

### 创建加载布局

选择Layout Resource File

![image-20250907155559108](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907155559108.png)

设置文件名和根元素

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907155519895.png" alt="image-20250907155519895" style="zoom:50%;" />

创建完成

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907155251682.png" alt="image-20250907155251682" style="zoom:50%;" />

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

</LinearLayout>
```

添加一个按钮

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    <Button
            android:id="@+id/button1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Button Hello"/>
</LinearLayout>
```

-   `android:id`
    -   给当前的元素定义一个唯一的标识符
    -   `@+id/id_name` 在XML中定义一个id
-   `android:layout_width`
    -   指定当前元素的宽度
    -   `match_parent `让当前元素和父元素一样宽
-   `android:layout_height`
    -   指定当前元素的高度
    -   `wrap_content `当前元素的高度刚好包含里面的内容
-   `android:text`指定了元素中显示的文字内容

![image-20250907163055625](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907163055625.png)

### 加载布局

`setContentView()`方法来给当前的Activity加载布局, 参数传入一个布局文件的id

```kotlin
class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // R.layout.activity_first
        // res/layout/activity_first
        super.setContentView(R.layout.activity_first)
    }
}
```

### AndroidManifest 注册

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="org.harvey.android.first"> <!--很奇妙, package不配置, 也能使用".FirstActivity"-->
    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.FirstAndroid" >
        <activity
            android:name=".FirstActivity"
            android:exported="false" />
        <activity
            android:name=".MainActivity"
            android:label="My First Android App"
            android:theme="@style/Theme.AppCompat.Light.DarkActionBar"
            android:exported="true" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

`<activity>` 对Activity进行注册的, Activity的注册声明要放在`<application>`标签内

`android:name`  指定具体注册哪一个Activity

`.FirstActivity`是`org.harvey.android.first.FirstActivity`的缩写

最外层的`<manifest>`标签中已经通过`package`属性指定了程序的包名

修改theme, 当前版本的Android Studio会生成一个不带Bar的style

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!--parent的NoActionBar就是不带Bar-->
    <style name="Base.Theme.FirstAndroid" parent="Theme.Material3.DayNight.NoActionBar">
    </style>

    <style name="Theme.FirstAndroid" parent="Base.Theme.FirstAndroid" />
</resources>
```

修改成下面的

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!--parent选择DarkActionBar-->
    <style name="Base.Theme.FirstAndroid" parent="Theme.AppCompat.Light.DarkActionBar">
    </style>
    <style name="Theme.FirstAndroid" parent="Base.Theme.FirstAndroid" />
</resources>
```

使用`android:label`指定Activity中标题栏(Bar)的内容，标题栏是显示在Activity最顶部的

![image-20250907231636447](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907231636447.png)

给**主Activity**指定的label不仅会成为标题栏中的内容，还会成为启动器（Launcher）中应用程序显示的名称

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907191259972.png" alt="image-20250907191259972" style="zoom:50%;" />

## Toast

将一些短小的信息通 知给用户，这些信息会在一段时间后自动消失，并且不会占用任何屏幕空间

```kotlin
val textToast: Toast = Toast.makeText(
    this, "You clicked Button 1", Toast.LENGTH_SHORT
)
textToast.show()
```

静态方法makeText创建一个Toast, 其声明如下

```java
public static Toast makeText(Context context, CharSequence text, @Duration int duration)
```

-   context, 而Activity(this)是Context类的子类
-   text 通知文本
-   duration 存在时长, `LENGTH_SHORT `是一个可选的常量, 还有`LENGTH_LONG`可选

将触发同时消息的行为注册到Button的点击事件上

```kotlin
private var binding: ActivityMainBinding by LazyConstant()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(super.layoutInflater)
    val view: View = binding.getRoot()
    setContentView(view)
    binding.button1.setOnClickListener {
        Toast.makeText(this, getString(R.string.toast_message), Toast.LENGTH_SHORT).show()
    }
}
```

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907205525044.png" alt="image-20250907205525044" style="zoom:33%;" />

## menu

### 创建资源文件

在res目录下新建一个menu文件夹

![image-20250907223926629](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907223926629.png)

menu文件夹下新建一个名叫“main”的菜单文件(右键->NEW->Menu Resource File)

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907224017008.png" alt="image-20250907224017008" style="zoom:50%;" />

添加代码

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/add_item"
            android:title="@string/menu_add"/>
    <item android:id="@+id/remove_item"
            android:title="@string/menu_remove"/>
</menu>
```

-   title 指定名称

效果

![image-20250907224235603](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907224235603.png)

### 注册资源

在MainActivity中重写方法`onCreateOptionsMenu`注册Menu

```kotlin
override fun onCreateOptionsMenu(menu: Menu): Boolean {
    super.menuInflater.inflate(R.menu.main, menu)
    return true
}
```

-   `nemuInflater.inflate`用于创建menu
    -   参数1表示依据哪个资源文件创建菜单
    -   参数2表示创建的菜单项将加入到哪个Menu对象中
-   返回值true 表示需要渲染菜单

### 响应事件

定义菜单的响应事件

重载Activity的`onOptionsItemSelected`方法

```kotlin
override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
        R.id.add_item -> toastShow(R.string.menu_add_clicked)
        R.id.remove_item -> toastShow(R.string.menu_remove_clicked)
    }
    return true
}

private fun toastShow(resId:Int) {
    Toast.makeText(
        this, getString(resId), Toast.LENGTH_SHORT
    ).show()
}
```

## 销毁

Android的Back键有自动销毁的功能

![image-20250907230452370](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/activity/Day01-基本用法/image-20250907230452370.png)

在代码中使用Activity的方法`finish()`, 实现在代码中销毁activity, 效果与Back键一致

```kotlin
binding.button1.setOnClickListener {
    super.finish()
    //toastShow(R.string.button_clicked)
}
```

## 最佳实践

### 创建时打印Activity

方便在调试页面的时候跟踪当前使用显示的Activity是哪个

可以规定在一个项目中使用`BaseActivity`作为一个父类, 所有真实渲染的Activity继承BaseActivity

而后在`BaseActivity`规范所有Activity应有的行为, 例如在开始时打印所有当前Activity的名字

```kotlin
open class BaseActivity<T : ViewBinding>(val bindingConstructor: (LayoutInflater) -> T) : AppCompatActivity() {
    protected var binding: T by LazyConstant()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("BaseActivity", "${this.javaClass}")

        binding = bindingConstructor(super.layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view) // 替换传统的setContentView

    }

}
```

子类继承

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>({ ActivityMainBinding.inflate(it) }) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.button1.setOnClickListener {
            Log.d(TAG, "clicked")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }

}
```

### 允许随时退出全部

创建一个单例, 允许随时退出

```kotlin
object ActivityCollector {
    private val list = ArrayList<Activity>()
    fun register(activity: Activity) = list.add(activity)

    fun unregister(activity: Activity) = list.remove(activity)

    fun finishAll() {
        for (activity in list) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        list.clear()
    }
}
```

在BaseActivity里统一规范

```kotlin
open class BaseActivity<T : ViewBinding>(val bindingConstructor: (LayoutInflater) -> T) : AppCompatActivity() {
    protected var binding: T by LazyConstant()
    protected var tag: String by LazyConstant()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        ActivityCollector.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.unregister(this)
    }
}
```

虽然不建议用, 但是本处给出杀死当前进程的API, 此API只能杀死当前进程

```kotlin
ndroid.os.Process.killProcess(android.os.Process.myPid()) 
```

### 启动Activity

将Intent传输的过程抽成函数, 设置成静态(object component), 方便其他Activity的开发者快速查看阅读

```kotlin
class SecondActivity : BaseActivity() { 
    // ... 
    companion object { 
        fun actionStart(context: Context, data1: String, data2: String) { 
            val intent = Intent(context, SecondActivity::class.java) 
            intent.putExtra("param1", data1) 
            intent.putExtra("param2", data2) 
            context.startActivity(intent) 
        } 
    } 
} 
```

