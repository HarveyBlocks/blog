# Application

每当应用程序启动的时候，系统就会自动将Application类进行初始化

## 自定义Application

可以用application的上下文作为全局上下文, 这样就可以做各种通知了

```kotlin
class MyApplication : Application() {
    companion object {
        lateinit var app: MyApplication

        val applicationContext: Context
            get() = app.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}
```

### 注册

manifest.xml

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="org.harvey.android.first">
    <application
            android:name=".view.application.MyApplication"
            ...>
        <!--...-->
    </application>

</manifest>
```

