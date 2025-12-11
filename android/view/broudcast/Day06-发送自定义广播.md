# 发送自定义广播



## 广播类型



### 标准广播

> normal broadcasts

异步广播, 广播发出后, 所有的BroadcastReceiver几乎在同一时刻收到广播消息

效率高, 但是无法被阶段



### 有序广播

> ordered broadcasts

同步执行的广播, 广播发出后, 同一时刻只有一个BroadcastReceiver能接受到消息

前一个BroadcastReceiver的逻辑执行完毕后, 才会传递给后一个执行

优先级高的BroadcastReceiver可以先收到广播, 也可以阶段信息

![image-20250916000423453](../../assetss/Day06-发送自定义广播/image-20250916000423453.png)







## 准备Receiver

以MainActivity Created为例

先创建Receiver

```kotlin
class MainActivityCreatedReceiver() : BroadcastReceiver() {
    override fun onReceive(parent: Context?, intent: Intent?) {
        toastShow(parent!!, "main activity created")
    }
}
```

```kotlin
class MainActivityCreatedReceiver(contextWrapper: ContextWrapper) : BaseBroadcastReceiver(
    contextWrapper, CustomerReceiverAction.MAIN_ACTIVITY_CREATED
) {
    override fun onReceive(parent: Context?, intent: Intent?) {
        toastShow(parent!!, "main activity created")
    }
}
```

发送的广播, 也可以被静态注册/动态注册的Receiver接受, 这里使用静态注册

`manifest.xml`

```xml
<receiver
        android:name=".broadcast.receiver.MainActivityCreatedReceiver"
        android:enabled="true"
        android:exported="false">
    <intent-filter>
        <action android:name="org.harvey.intent.action.MAIN_ACTIVITY_CREATED" />
    </intent-filter>
</receiver>
```

消息action用枚举常量

```kotlin
enum class CustomerReceiverAction(override val action: String) : ReceiverAction {
    MAIN_ACTIVITY_CREATED("org.harvey.intent.action.MAIN_ACTIVITY_CREATED")
}
```

## 发送标准广播

MainActivity发送

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(CustomerReceiverAction.MAIN_ACTIVITY_CREATED.action)
        intent.setPackage(packageName)
        sendBroadcast(intent)
    }
}
```

- `packageName` 由父类`ContextWrapper`提供, 是本类的包名
- 这里一定要调用setPackage()方法，指定这条广播是发送给哪个应用程序的，从而让它变成一条**显式广播**
- 否则静态注册的BroadcastReceiver将无法接收到这条广播。

## 发送有序广播

发送

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(CustomerReceiverAction.MAIN_ACTIVITY_CREATED.action)
        intent.setPackage(packageName)
        sendOrderedBroadcast(intent, /* receiverPermission = */ null)
    }
}
```

- 第二个参数是一个与权限相关的字符串

设置接收者的优先级, 优先级值大的可以先收到广播。

优先级默认是0

```xml
<receiver
        android:name=".broadcast.receiver.MainActivityCreatedReceiver"
        android:enabled="true"
        android:exported="false">
    <intent-filter android:priority="100">
        <action android:name="org.harvey.intent.action.MAIN_ACTIVITY_CREATED" />
    </intent-filter>
</receiver>
```

截断广播

```kotlin
class MainActivityCreatedReceiver() : BroadcastReceiver() {
    override fun onReceive(parent: Context?, intent: Intent?) {
        toastShow(parent!!, "main activity created")
        abortBroadcast()
    }
}
```

