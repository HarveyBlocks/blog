# Android多线程

## 多线程基础

kotlin中的多线程写法(kotlin支持传统的JavaThread写法, 也有一些拓展写法)

```kotlin
thread {
   logInfo("hi")
}
```

可以阅读thread函数的源码, 就是对Java原生Thread的扩展和简便

```kotlin
public fun thread(
    start: Boolean = true,
    isDaemon: Boolean = false,
    contextClassLoader: ClassLoader? = null,
    name: String? = null,
    priority: Int = -1,
    block: () -> Unit
): Thread {
    val thread = object : Thread() {
        public override fun run() {
            block()
        }
    }
    if (isDaemon)
        thread.isDaemon = true
    if (priority > 0)
        thread.priority = priority
    if (name != null)
        thread.name = name
    if (contextClassLoader != null)
        thread.contextClassLoader = contextClassLoader
    if (start)
        thread.start()
    return thread
}
```

使用

```kotlin
thread {
   logInfo("world")
}
logInfo("hello")
```

![image-20250920170820188](../../assets/Day10-Android多线程/image-20250920170820188.png)

## 异步消息

由于线程安全问题, Android的更新UI操作必须在主线程进行, 在子线程更新UI程序将会崩溃(连toast也不行)

但是可以用异步操作, 在子线程进行通知需要进行的UI更新操作, 然后在主线程执行这个操作

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> toastShow(this@MainActivity, msg.data.getString("show") ?: "null")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thread {
            val msg = Message()
            msg.what = 1 // 类似request code
            msg.data.putString("show", "world")
            handler.sendMessage(msg)
        }
        toastShow(this, "hello")
    }
}
```

### 处理机制

- android.os.Message

  - 有`arg1`, `arg2`, `obj` 字段携带少量数据

- android.os.Handler

  - 处理消息在`handlerMessage`
  - 发送消息用`post`, `sendMessage`

- `MessageQueue`

  - 每个线程只有一个`MessageQueue`

- `android.os.Looper`

  - 是每个线程中的MessageQueue的Manager

  - `loop()`方法会进入一个无限循环

    在循环中发现MessageQueue中存在一条消息时，就会取出并传递到Handler的handleMessage()方法中

  - 每个线程只有一个Looper对象

