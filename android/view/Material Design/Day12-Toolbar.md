# Toolbar

- 继承了ActionBar的所有功能
- 灵活性高
- 可以配合其他控件完成一些Material Design的效果



## 覆盖ActionBar

使用Toolbar之前像取消ActionBar

```xml
<resources>
    <!-- Base application theme. -->
    <style name="Base.Theme.FirstAndroid" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your light theme here. -->
        <!-- <item name="colorPrimary">@color/my_light_primary</item> -->
    </style>

    <style name="Theme.FirstAndroid" parent="Base.Theme.FirstAndroid" />
</resources>
```

将Parent设置为NoActionBar

theme中的主题颜色属性

```xml
<resources>
    <!-- Base application theme. -->
    <style name="Base.Theme.FirstAndroid" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your light theme here. -->
        <item name="colorPrimary">@color/my_primary_light</item>
        <item name="colorPrimaryDark">@color/my_primary_dark</item>
        <item name="colorSecondary">@color/accent</item>
    </style>

    <style name="Theme.FirstAndroid" parent="Base.Theme.FirstAndroid" />
</resources>
```

其中, 不同标签表示的部分是

<img src="../../assetss/Day12-Material Design/image-20250922114958312.png" alt="image-20250922114958312" style="zoom:50%;" />

- 此处的colorAccent修改为colorSecondary

## 基本效果

`activity_main.xml`

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

    <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="@color/my_light_primary"
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
</LinearLayout>
```

- `xmlns:app="http://schemas.android.com/apk/res-auto"`  声明命名空间, 专门负责Material相关组件
- `androidx.appcompat.widget.Toolbar ` 组件, 由appcompat库提供
- `ThemeOverlay.AppCompat.Dark.ActionBar`主题指定成了深色主题, 那么label的颜色就是白色
- ` android:background="@color/my_light_primary"` 背景颜色和原来的ActionBar背景一致
- `app:popupTheme="@style/ThemeOverlay.AppCompat.Light" ` 保证弹出的框也保持在Light样式, 而不是Dark, 很丑

MainActiviy

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val logger: Logger = this.logger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar) // 注册tool bar
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // 注册Menu
        super.menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 注册Menu上的事件
        when (item.itemId) {
            R.id.register -> toast("register is not ready")
            R.id.login -> toast("login is not ready")
        }
        return true
    }
}
```

样式如下

![image-20250922162524115](../../assetss/Day12-Toolbar/image-20250922162524115.png)

## label

manifest.xml

```xml
<application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.FirstAndroid">

    <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="My App"
            android:launchMode="standard"
            android:theme="@style/Theme.FirstAndroid">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

- `android:label`上的文本会成为Bar上的文本
- 如果是`<action android:name="android.intent.action.MAIN" />`上标记的`label`, 则会成为App名

## menu 图标

### menu布局



```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
            android:id="@+id/backup"
            android:icon="@drawable/ic_backup"
            android:title="Backup"
            app:showAsAction="always" />
    <item
            android:id="@+id/delete"
            android:icon="@drawable/ic_delete"
            android:title="Delete"
            app:showAsAction="ifRoom" />
    <item
            android:id="@+id/settings"
            android:icon="@drawable/ic_settings"
            android:title="Settings"
            app:showAsAction="never" />
</menu>
```

`app:showAsAction`如何在Bar上显示

- `always`
- `never`
- `ifRoom` 如果Bar上还有空间空余

`android:icon`设置图标



### 注册

```kotlin
class MainActivity : AppCompatActivity() { 
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState) 
        // ...
        setSupportActionBar(binding.toolbar) 
    } 
}
```





### 效果



<img src="../../assetss/Day12-Toolbar/image-20250922165045594.png" alt="image-20250922165045594" style="zoom:50%;" />

显示效果

![image-20250922165114031](../../assetss/Day12-Toolbar/image-20250922165114031.png)

有一个item被隐藏了

![image-20250922165255220](../../assetss/Day12-Toolbar/image-20250922165255220.png)

