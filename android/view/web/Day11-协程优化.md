# Android 协程优化



## 引入依赖

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

## 工具

```kotlin
/**
 * @param continuation 来自[suspendCoroutine]协程作用域
 */
class ContinuationCallback<T : Any>(private val continuation: Continuation<Response<T>>) :
    Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        continuation.resume(response)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        continuation.resumeWithException(t)
    }
}


suspend fun <T : Any> Call<T>.suspendExecute(): Response<T> = suspendCoroutine { continuation ->
    try {
        continuation.resume(execute())
    } catch (t: Throwable) {
        continuation.resumeWithException(t)
    }
}

suspend fun <T : Any> Call<T>.suspendEnqueue(): Response<T> = suspendCoroutine { continuation ->
    enqueue(ContinuationCallback(continuation))
}
```

简单使用

```kotlin
interface Service {
    fun get(): Call<String>
}

fun main() {
    RetrofitProxy.config.url = "https://www.baidu.com"
    val manager = RetrofitProxy.manager
    val proxy = manager.proxy<Service>()
    runBlocking {
        coroutineScope {
            // 在代码上看是阻塞的, 在执行上是在IO中会被挂起
            val response1: Response<String> = proxy.get().suspendEnqueue()
            val response2: Response<String> = proxy.get().suspendExecute()
        }
    }
}
```
