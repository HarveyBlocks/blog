# 接受系统广播

> BroadcastReceiver

为Android中的每个应用程序都可以对自己感兴趣的广播进行注册

样该程序只会收到自己所关心的广播内容

在应用程序中通过监听**系统广播**来得到各种**系统的状态信息**, 比如

- 手机开机完成后
- 电池的电量发生变化
- 系统时间发生改变
- .....

注册方式有二

- 动态注册 在代码中注册
- 静态注册 在AndroidManifest.xml中注册

## 动态注册

以监听时间变化为例

当有广播到来时， onReceive()方法就会得到执行，具体的逻辑就可以在这个方法中处理

### Receiver

```kotlin
class TimeChangeReceiver(val contextWrapper: ContextWrapper) : BroadcastReceiver() {
    init {
        register()
    }

    private fun register() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.TIME_TICK")
        contextWrapper.registerReceiver(this, intentFilter)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(logTag,"time change")
    }

    fun unregister() {
        contextWrapper.unregisterReceiver(this)
    }
}

```

Action的选择可以在本地某一版本AndroidSDK的[broadcast_actions](D:\IT_study\android\platforms\android-36\data\broadcast_actions.txt)文件中查看

重写onReceive方法

**不要在onReceive()方法中添加过多的逻辑或者进行任何的耗时操作**，因为 BroadcastReceiver中是不允许开启线程的，当onReceive()方法运行了较长时间而没有结束时，程序就会出现错误

### Activity

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    lateinit var timeChangeReceiver: TimeChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeChangeReceiver = TimeChangeReceiver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeChangeReceiver.unregister()
    }
}
```

动态注册的BroadcastReceiver**一定要取消注册**

### 日志

一分钟变化一次

![image-20250916090744618](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/broudcast/Day06-接收系统广播/image-20250916090744618.png)

### 父类抽象

Action的枚举类

```kotlin
interface ReceiverAction {
    val action: String
}
```

```kotlin
enum class SystemReceiverAction(override val action: String) : ReceiverAction {
    TIME_TICK("android.intent.action.TIME_TICK")
    // ...
}

```

父类抽象和使用

```kotlin
abstract class BaseBroadcastReceiver(
    val contextWrapper: ContextWrapper, vararg action: ReceiverAction
) : BroadcastReceiver() {
    init {
        register(*action)
    }

    protected open fun register(vararg action: ReceiverAction) {
        val intentFilter = IntentFilter()
        action.asSequence().map { it.action }.forEach(intentFilter::addAction)
        contextWrapper.registerReceiver(this, intentFilter)
    }

    open fun unregister() {
        contextWrapper.unregisterReceiver(this)
    }
}

// 使用
class TimeChangeReceiver(contextWrapper: ContextWrapper) :
    BaseBroadcastReceiver(contextWrapper, ReceiverAction.TIME_TICK) {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(logTag, "time change")
    }
}
```

## 静态注册

让程序在未启动的情况下也能接收广播

由于大量恶意的应用程序利用这个机制在程序未启动的情况下监听系统广播，从而使任何应用都可以频繁地从后台被唤醒，严重影响了用户手机的电量和性能，因此Android系统几乎每个版本都在削减静态注册BroadcastReceiver的功能

Android 8.0系统之后，所有隐式广播都不允许使用静态注册的方式来接收了。隐式广播指的是那些没有具体指定发送给哪个应用程序的广播，大多数系统广播属于隐式广播

少数特殊的[系统广播](https://developer.android.google.cn/develop/background-work/background-tasks/broadcasts/broadcast-exceptions?hl=zh-cn)目前仍然允许使用静态注册的方式来接收

开机依然可以使用静态注册监听

### 创建BroadcastReceiver

![image-20250916095216392](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/broudcast/Day06-接收系统广播/image-20250916095216392.png)

创建页面

![image-20250916095157092](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/broudcast/Day06-接收系统广播/image-20250916095157092.png)

以这种方式创建的Receiver, 将会**自动注册到manifest**

```xml
<receiver
        android:name=".broadcast.receiver.BootCompleteReceiver"
        android:enabled="true"
        android:exported="true"></receiver>
```

- Exported 允许接收本程序以外的广播
- Enabled   启用本 BroadcastReceiver

添加需要监听的目标事件

```xml
<receiver
        android:name=".broadcast.receiver.BootCompleteReceiver"
        android:enabled="true"
        android:exported="true" >
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

为了保护用户设备的安全和隐私，Android 系统规定：

如果程序需要进行一些对用户来说比较敏感的操作，**必须在AndroidManifest.xml文件中进行权限声明**，否则程序将会直接崩溃

接收系统的开机广播就是需要进行权限声明的

在上述manifest中使用标签声明了android.permission.RECEIVE_BOOT_COMPLETED权限。

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="org.harvey.android.first">
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <!--...-->

</manifest>
```

Receiver的回调逻辑

```kotlin
class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Boot Complete", Toast.LENGTH_LONG).show()
    }
}
```

