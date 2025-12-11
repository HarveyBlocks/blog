# Notification

给手机发送通知

当某个应用程序希望向用户发 出一些提示信息，而该应用程序又不在前台运行时，就可以借助通知来实现

## 通知渠道

Android 8.0 提出

给发送的通知分类, 例如聊天消息, 关注推送, @我的, 等等

### 创建

创建通知需要先创建通知渠道

```kotlin
// 获取NotificationManager对通知进行管理
val manager = super.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
// 使用NotificationChannel类构建一个通知渠道
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 进行版本检查
    // 构造channel
    val channel = NotificationChannel(
        /* id = */ "push following",
        /* name = */ "关注推送",
        /* importance = */ NotificationManager.IMPORTANCE_LOW
    )
    // 完成创建
    manager.createNotificationChannel(channel)
}
```

**创建通知渠道只在第一次执行的时候才会创建**

- importance可选值
  - NotificationManager.IMPORTANCE_HIGH 弹出横幅
  - NotificationManager.IMPORTANCE_DEFAULT 
  - NotificationManager.IMPORTANCE_LOW
  - NotificationManager.IMPORTANCE_MIN

重要程度从高到低

优化NotificationManager的获取

```kotlin
// val manager = super.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
// 以上方法太过丑陋, 使用androidx的扩展
val manager = NotificationManagerCompat.from(this)
```

底层是一样的

![image-20250918230119568](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Multimedia/Day08-Notification/image-20250918230119568.png)

### 等级

通知渠道发出的通知有高低重要等级之分

高重要等级的可以弹出横幅、发出声音

低重要等级的可能会在某些情况下被隐藏，可能会改变显示的顺序，被排在更重要的通知之后

开发者只能在创建通知渠道的时候指定初始的重要等级，如果用户不认可这个重要等级，可以随时进行修改，开发者对此无权再进行调整和变更

## 通知

可以在Activity, BroadcastReceiver,Service里创建

准备常量

写一个产量

```kotlin
object NotificationChannelId {
    const val PUSH_FOLLOWING = "push following"
}
```

创建一个Notification

```kotlin
val notification =
    NotificationCompat.Builder(
        /* context = */this,
        /* channelId = */ NotificationChannelId.PUSH_FOLLOWING
    ).setContentTitle("This is content title")
     .setContentText("This is content text")
     // 通知上展示
     .setSmallIcon(R.drawable.small_icon)
     // 下拉后展示
     .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.large_icon))
     .build()
```

## 发送通知

在API版本33以上, 需要申请权限才能发送消息

使用`NotificationManagerCompat`发送通知需要注册权限`android.permission.POST_NOTIFICATIONS`

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

进行动态申请权限

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    val safelyNotify = safeWrapper(
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        "notify notification"
    ) {
        manager.notify(notification)
    }
    safelyNotify()
} else {
    manager.notify(notification)
}
```

在Lint的检查下, 报错了, 它不知道我其实使用了自定义函数将代码进行了检查

![image-20250918232619379](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Multimedia/Day08-Notification/image-20250918232619379.png)

```kotlin
package org.harvey.android.first.lint

import android.annotation.SuppressLint
import android.app.Notification
import androidx.core.app.NotificationManagerCompat

@SuppressLint("MissingPermission")
fun NotificationManagerCompat.unsafeNotify(id:Int, notification: Notification) {
    this.notify(id, notification);
}
```

需要用户确认

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Multimedia/Day08-Notification/image-20250918232300832.png" alt="image-20250918232300832" style="zoom:50%;" />

发送成功

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Multimedia/Day08-Notification/image-20250918233407084.png" alt="image-20250918233407084" style="zoom:67%;" />

## PendingIntent

用于注册通知被点击之后的事件

PendingIntent倾向于在某个合适的时机执行某个动作, 而Intent立即执行

在Notification中设置

```kotlin
val notification =
    NotificationCompat.Builder(this, NotificationChannelId.PUSH_FOLLOWING)
        // ...
        // 加上点击进入的PendingIntent
        .setContentIntent(createPendingIntent())
		// ...
        .build()
```

创建PendingIntent

```kotlin
private fun createPendingIntent(): PendingIntent {
    val intent = Intent(this, NewsContentActivity::class.java)
    return PendingIntent.getActivity(/* context = */this, /* requestCode = */ 0, intent,
        PendingIntent.FLAG_IMMUTABLE )
}
```

- requestCode, 一般不会用到

第四个参数

- `FLAG_IMMUTABLE`和`FLAG_MUTABLE` 而选一, 表示是否可变
  - Build.VERSION_CODES.R版本之前, 默认 PendingIntents 是可变的
  - 从Build.VERSION_CODES.S，需要在创建时使用这两个标签显式指定 PendingIntents 的可变性
  - **强烈建议使用 FLAG_IMMUTABLE**
  - 只有当某些功能依赖于修改基础意图时，才应使用FLAG_MUTABLE，例如任何需要与内联回复或气泡一起使用的endingIntent。
- `FLAG_ONE_SHOT` ,`FLAG_NO_CREATE`, `FLAG_CANCEL_CURRENT`, `FLAG_UPDATE_CURRENT`四个选项组合(可以不选)
  - `FLAG_ONE_SHOT`  此PadingIntent只能使用一次. 在调用了PadingIntent的`send()`方法发送之后, 将自动设置为禁用, 此后任何操作都会失败, 抛出异常
  - `FLAG_NO_CREATE` 如果Intent描述的PadingIntent不存在, 则返回null, 而不是创建
  - `FLAG_CANCEL_CURRENT`  如果Intent描述的PadingIntent已经存在, 则这个PadingIntent被取消, 而另外创建一个PadingIntent
  - `FLAG_UPDATE_CURRENT`  如果Intent描述的PadingIntent已经存在, 则用Intent的新的内部数据来替换这个PadingInntent的内部数据

## 取消通知

点击通知后, 应当取消通知, 否则通知将一直保留在通知栏

![image-20250919014633868](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Multimedia/Day08-Notification/image-20250919014633868.png)

自动取消

```kotlin
val notification =
    NotificationCompat.Builder(this, NotificationChannelId.PUSH_FOLLOWING)
        // ...
        // 自动关闭通知信息
        .setAutoCancel(true)
		// ...
        .build()
```

在通知点击的目标进行手动取消(需要和**notify的RequestCode**匹配)

```kotlin
NotificationManagerCompat.from(this).cancel(requestCode)
```

那么, 进行一些改进

上有, putExtra

```kotlin
val pendingIntent = createPendingIntent(
    Intent(this, NewsContentActivity::class.java).apply {
        putExtra("__notify_request_code__", notifyRequestCode)
    }
)
```

其中

```kotlin
private fun createPendingIntent(intent: Intent): PendingIntent = PendingIntent.getActivity(
    this,  0, intent, PendingIntent.FLAG_IMMUTABLE
)
```

在下游getIntExtra

```kotlin
NotificationManagerCompat.from(this).cancel(intent.getIntExtra("__notify_request_code__", -100))
```

## NotificationCompat.Builder

创建出更加多样的通知效果

### setStyle

接受参数`NotificationCompat.Style`

#### 大文本

一般的大文本, 会在通知里直接变成`previous text can show, but if more, it will...`隐藏

```kotlin
val notification = NotificationCompat.Builder(this, NotificationChannelId.PUSH_FOLLOWING)
    // ...
    //.setContentText(
	//	 """Learn how to build notifications,
    //            | send and sync  data, and use voice actions.
    //            | Get the officialAndroid IDE and developer tools
    //            | to build apps for Android.""".trimMargin()
	//)
    .setStyle(
        NotificationCompat.BigTextStyle().bigText(
            """Learn how to build notifications,
                | send and sync  data, and use voice actions.
                | Get the officialAndroid IDE and developer tools
                | to build apps for Android.""".trimMargin()
        )
    )
	// ...
	.build()
```

### 大图片

```kotlin
val notification = NotificationCompat.Builder(this, NotificationChannelId.PUSH_FOLLOWING)
    // ...
    .setStyle(
        NotificationCompat.BigPictureStyle().bigPicture(
        BitmapFactory.decodeResource(resources, R.drawable.big_image))
    )
	// ...
	.build()
```

多次setStyle,多个Style, 不可共存, 后设置的生效

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/android/view/Multimedia/Day08-Notification/image-20250919093648863.png" alt="image-20250919093648863" style="zoom:50%;" />

## 封装

考虑到Notify能在Servie, Broadcast中都能使用, 于是将其从Activity中抽象出来

### IdGenerator

Notify需要RequestCode, RequestCode的生成封装一个线程安全的IdGenerator

```kotlin
class IntIdGenerator(start: Int = InnerConstants.DEFAULT_START) {
    companion object {
        const val NOT_EXIST = -1
    }

    private object InnerConstants {
        const val DEFAULT_START = NOT_EXIST + 1
    }

    private val atomicCounter = AtomicInteger(start)

    fun next(): Int {
        val andIncrement = atomicCounter.getAndIncrement()
        return andIncrement
    }
}
```

**在应用启动时加载持久化的`LAST_ID`+1作为本次START, 应用关闭时把`LAST_ID`持久化**

这部分实现涉及自定义Application,此处不考虑这一点

### notification的有关封装

```kotlin
package org.harvey.android.first.common.util

import org.harvey.android.first.common.IntIdGenerator
import org.harvey.android.first.lint.unsafeNotify

val Context.notificationManager: NotificationManagerCompat
    get() {
        return NotificationManagerCompat.from(this)
    }

private object NotificationConstant {
    const val INTENT_REQUEST_CODE_KEY: String = "__notify_request_code__"

    /**
     * 由于 [notifyRequestCodeGenerator], -1 不可能作为code
     */
    const val INTENT_REQUEST_CODE_NOT_FOUND: Int = -1

    /**
     * TODO 将LastID持久化
     */
    val notifyRequestCodeGenerator: IntIdGenerator = IntIdGenerator()

    val SUPPORT_NOTIFICATION_CHANNEL: Boolean = ApiVersion.support(Build.VERSION_CODES.O)
    val SUPPORT_NOTIFICATION_POST_NOTIFICATIONS_PERMISSION: Boolean =
        ApiVersion.support(Build.VERSION_CODES.TIRAMISU)
}

/**
 * 使用NotificationChannel类构建一个通知渠道
 */
fun NotificationManagerCompat.createNotificationChannel(
    id: String, name: CharSequence, importance: Int
) {
    if (NotificationConstant.SUPPORT_NOTIFICATION_CHANNEL) {
        // 构造channel
        val channel = NotificationChannel(id, name, importance)
        // 完成创建
        createNotificationChannel(channel)
    }
}

/**
 * @param intent the intent in PendingIntent while the PendingIntent is in the [notification]
 * @param notification build by customer with [intent]
 */
fun NotificationManagerCompat.safelyNotify(
    activity: Activity, intent: Intent, notification: Notification
) {
    if (NotificationConstant.SUPPORT_NOTIFICATION_POST_NOTIFICATIONS_PERMISSION) {
        val safelyNotify = activity.safeWrapper(
            arrayOf(Permission.POST_NOTIFICATIONS), "notify notification"
        ) {
            unsafeNotify(intent.notifyRequestCode, notification)
        }
        safelyNotify()
    } else {
        unsafeNotify(intent.notifyRequestCode, notification)
    }
}

/**
 * @param intent the intent in PendingIntent while the PendingIntent is in the [notification]
 * @param notification build by customer with [intent]
 */
fun NotificationManagerCompat.notifyIfEnable(
    context: Context, intent: Intent, notification: Notification
) {
    if (NotificationConstant.SUPPORT_NOTIFICATION_POST_NOTIFICATIONS_PERMISSION) {
        val checkedWrapper = context.checkWrapper(arrayOf(Permission.POST_NOTIFICATIONS)) {
            unsafeNotify(intent.notifyRequestCode, notification)
        }
        checkedWrapper()
    } else {
        unsafeNotify(intent.notifyRequestCode, notification)
    }
}
/**
 * 还可以有点击目标是Activity之外的PendingIntent, 这种方法还需创建
 */
fun Context.getActivityPendingIntent(intent: Intent): PendingIntent {
    pendingIntentInitPre(intent)
    return PendingIntent.getActivity(
        this, /* requestCode = */ 0, intent, PendingIntent.FLAG_IMMUTABLE
    )
}

private fun pendingIntentInitPre(intent: Intent) {
    intent.initNotifyRequestCode()
}

/**
 * 关闭intent对应的notification
 */
fun NotificationManagerCompat.cancel(intent: Intent) {
    if (intent.initializedNotifyRequestCode()) {
        cancel(intent.notifyRequestCode)
    }
    // 如果不是从notify来的, 则不进行cancel
}

private val Intent.notifyRequestCode: Int
    get() :Int {
        val requestCode = this.getIntExtra(
            NotificationConstant.INTENT_REQUEST_CODE_KEY,
            NotificationConstant.INTENT_REQUEST_CODE_NOT_FOUND
        )
        check(requestCode != NotificationConstant.INTENT_REQUEST_CODE_NOT_FOUND) { "No notify request code initialized in the intent!" }
        return requestCode
    }

private fun Intent.initNotifyRequestCode() {
    if (initializedNotifyRequestCode()) {
        return
    }
    val notifyRequestCode = NotificationConstant.notifyRequestCodeGenerator.next()
    this.putExtra(
        NotificationConstant.INTENT_REQUEST_CODE_KEY, notifyRequestCode
    )
}

private fun Intent.initializedNotifyRequestCode(): Boolean {
    val requestCode = this.getIntExtra(
        NotificationConstant.INTENT_REQUEST_CODE_KEY,
        NotificationConstant.INTENT_REQUEST_CODE_NOT_FOUND
    )
    return requestCode != NotificationConstant.INTENT_REQUEST_CODE_NOT_FOUND
}
```

### 对封装的使用示例

```kotlin
val notificationManager = NotificationManagerCompat.from(this)
notificationManager.run {
    createNotificationChannel(
        NotificationChannelId.PUSH_FOLLOWING,
        "关注推送",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    // ... more channel
}
notificationManager.run {
    val intent = Intent(this@MainActivity, NewsContentActivity::class.java)
    safelyNotify(this@MainActivity, intent, buildNotification(intent))
    // ... more notify
}
```

