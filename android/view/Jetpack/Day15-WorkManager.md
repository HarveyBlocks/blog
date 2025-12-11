# WorkManager

用于处理定时任务

操作系统的版本自动选择底层是使用AlarmManager实现还是JobScheduler实现

还支持周期性任务、链式任务处理等功能

保证即使在应用退出甚至手机重启的情况下，之前注册的任务仍然将会得到执行

用WorkManager注册的周期性任务不能保证一定会准时执行。是系统为了减少电量消耗，可能会将触发时间临近的几个任务放在一起执行，这样可以大幅度地减少CPU被唤醒的次数，从而有效延长电池的使用时间

==一键杀死功能会让WorkManager失效==

## 依赖

```kotlin
dependencies {
    // work manager
    implementation("androidx.work:work-runtime:2.10.4")
}
```

## 用法

1. 定义一个后台任务，并实现具体的任务逻辑
2. 配置该后台任务的运行条件和约束信息，并构建后台任务请求
3. 将该后台任务请求传入WorkManager的enqueue()方法中，系统会在合适的时间运行

### 定义任务

```kotlin
class SimpleWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    val logger: Logger = logger()
    override fun doWork(): Result {
        return if ((1..100).random() > 99) {
            logger.debug("make it failure")
            Result.failure()
        } else {
            logger.debug("do work in SimpleWorker")
            Result.success()
        }
    }
}
```

还有Result.retry()方法，可以结合WorkRequest.Builder的setBackoffCriteria()方法来重新执行任务

### 请求

构建**单次运行的后台任务**请求

```kotlin
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).build()
```

构建**周期性运行的后台任务**请求

为了降低设备性能消耗，运行周期间隔不能短于15分钟

```kotlin
val request = PeriodicWorkRequest.Builder(SimpleWorker::class.java, 15,
    TimeUnit.MINUTES).build()
```

两个Builder都继承自WorkRequest.Builder, 其相关API

```kotlin
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
    .addTag("simple") // 多个request可持有同一tag, 可以通过标签来取消请求
    .setInitialDelay(5, TimeUnit.MINUTES) // 任务在5分钟后运行
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    // BackoffPolicy.LINEAR 线性增加重试间隔
    // BackoffPolicy.EXPONENTIAL 指数级增加重试间隔
    .build()
```

### WorkManager

创建WorkManager

```kotlin
private fun instanceWorkManager(): WorkManager = WorkManager.getInstance(/*context = */this)
```

开启任务

```kotlin
workManager.enqueue(request)
```

取消后台任务

```kotlin
// 用ID取消
workManager.cancelWorkById(request.id)
// 用tag取消
workManager.cancelAllWorkByTag("simple")
```

通知任务运行结果(Result.successful/failure/retried), 监听并处理结果回调

```kotlin
workManager.getWorkInfoByIdLiveData(request.id)
    // getWorkInfosByTagLiveData 也有
    .observe(this) {
        it?.let { workInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                logger.debug("do work succeeded")
            } else if (workInfo.state == WorkInfo.State.FAILED) {
                logger.debug("do work failed")
            }
        }
    }
```

链式任务

必须在前一个后台任务运行成功之后，下一个后台任务才会运行

如果某个后台任务运行失败，或者被取消了，那么接下来的后台任务就都得不到运行了

```kotlin
workManager.beginWith(step1Request)
    .then(step2Request)
    .then(step3Request)
    .enqueue()
```

