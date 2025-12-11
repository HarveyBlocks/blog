## 协程



协程是在线程上的进一步轻量化

**不同协程上的任务可能执行在同一条线程**

协程上的任务, 可能执行到一半后挂起, 然后执行另一个协程的任务

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}
```



```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking { // 开启一个主协程块
        launch { // 开创一个新协程
            delay(timeMillis = 400L) // 非阻塞(执行其他任务)延迟, 1s
            println("World!") // 重回本协程进行任务执行
        }
        delay(timeMillis = 100L) // 非阻塞(执行其他任务)延迟, 1s
        println("Hello") // 前一个协程进入阻塞后, 主协程继续执行
    }
    // runBlocking 中的代码全部完成后才会往下执行
    // runBlocking 是阻塞的
    print("done")
}
```





## 简单使用

### GlobalScope.launch

创建一个协程的作用域

```kotlin
val dateFormat = SimpleDateFormat("hh:mm:ss.SSS", Locale.PRC)

fun log(msg: String) {
    val now = dateFormat.format(java.util.Date(System.currentTimeMillis()))
    println("$now [${Thread.currentThread().name}] : $msg")
}

fun main() {
    GlobalScope.launch {
        // 这是顶级协程, main函数结束也会结束
        log("coroutine $i run in coroutine scope")
    }
    Thread.sleep(3000)
}
```



大量启动协程

```kotlin
for (i in 1..1000) {
    GlobalScope.launch {
        log("coroutine $i run in coroutine scope")
    }
}
```

阅读日志发现, 最多打开了20条线程

![image-20250922004003011](../../assetss/Day05-协程/image-20250922004003011.png)



线程安全问题依然存在

```kotlin
var count = 0
for (i in 1..1000) {
    GlobalScope.launch {
        log("coroutine $i run in coroutine scope")
        for (n in 1..1000) {
            count++
        }
        log(count.toString())
    }
}
Thread.sleep(3000)
log("end")
```

![image-20250922004134018](../../assetss/Day05-协程/image-20250922004134018.png)



协程代码块中的代码在1秒钟之内不能运行结束，那么就会被强制 中断

### delay

- 只能在协程的作用域或其他挂起函数中调用
- 非阻塞式的挂起函数，只会挂起当前协程，并不会影响其他协程的运行
- Thread.sleep 阻塞当前线程, 当前线程下的所有协程被阻塞

```kotlin
GlobalScope.launch {
    log("launch start")
    delay(1500)// 非阻塞式的挂起函数，只会挂起当前协程，并不会影响其他协程的运行
    // delay() 只能在协程的作用域或其他挂起函数中调用
    log("launch end")
}
Thread.sleep(1000) // 阻塞当前线程, 当前线程下的所有协程被阻塞
log("end")
```

### runBlocking

创建一个协程的作用域

保证在协程作用域内的所有代码和**子协程**没有全部执行完之前**一直阻塞当前线程**

只应该在测试环境下使用，在正式环境中将造成性能不足

```kotlin
runBlocking {
    log("launch start")
    delay(1500)
    log("launch end")
}
Thread.sleep(1000)
log("end")
```

日志打印顺序如下

![image-20250922005515745](../../assetss/Day05-协程/image-20250922005515745.png)

### launch

协程作用域 CoroutineScope, 在协程作用域中调用launch函数

```kotlin
runBlocking {
    launch {
        log("created 1")
        delay(1000)
        log("end coroutines 1")
    }
    launch {
        log("created 2")
        delay(1000)
        log("end coroutines 2")
    }
}
log("end")
```

其中, CoroutineScope是协程作用域(block lambda)的Receiver

![image-20250922010137570](../../assetss/Day05-协程/image-20250922010137570.png)

由于runBlocking, 其内部的子协程都运行在Main线程上, 但两个协程并发运行了

```kotlin
fun main() {
    val start = System.currentTimeMillis()
    runBlocking {
        repeat(100000) {
            launch {
                log(".")
            }
        }
    }
    val end = System.currentTimeMillis()
    log(end - start)
}
```

看来IO操作在协程里是非阻塞的



## suspend

挂起

关键字, 用于修饰函数, 挂起函数之间是可以互相调用的

```kotlin
suspend fun logDot() { 
    logDot(".") 
    delay(1000)  // 允许调用挂起函数
} 
```

suspend 函数**不提供协程作用域**, 因此其内无法调用`CoroutineScope.launch` 函数

### coroutineScope

使用`coroutineScope`函数, 继承外部协程作用域并创建子协程, 也就是说, 可以调用`CoroutineScope.launch` 函数

```kotlin
suspend fun logDot() = coroutineScope {
    launch {
        log(".")
        delay(1000)
    }
}
```

coroutineScope函数保证**其作用域内的所有代码和子协程在全部执行完之前**，外部的**协程**会一直被**挂起**

```kotlin
fun main() {
    runBlocking {
        coroutineScope {
            launch {
                for (i in 1..10) {
                    log(i)
                    delay(1000)
                }
            }
            log("after launch") // 不阻塞
        }
        log("after coroutineScope") // 等待coroutineScope内所有子协程执行完毕
    }
    log("after runBlocking") // 等待runBlocking内所有子协程执行完毕
}
```

coroutineScope函数**只会阻塞当前协程**，既**不影响其他协程**，也**不影响任何线程**

## 作用域构建器

- GlobalScope.launch
- runBlocking
- CoroutineScope.launch
- CoroutineScope.coroutineScope



### Job

作用域构建器的Job, 在协程作用域外, 可以用Job对对应协程进行关闭

```kotlin
val job = Job()
val scope = CoroutineScope(job)
scope.launch {
    log("do ... ")
}
job.cancel()
```

GlobalScope.launch的返回值就是job, 如果需要在GlobalScope.launch的协程作用域外提前关闭GlobalScope.launch的作用域,就使用其返回值Job





### CoroutineScope.async

在协程作用域中有效, 用于获取内部协程作用域的返回值

```kotlin
fun main() = runBlocking {
    val deferred: Deferred<Int> = async {
        delay(1000)
        log("async finished")
        5 + 5
    }
    log("some task")
    val result: Int = deferred.await()
    log(result)
}
```

###  线程参数

表示一种线程策略

- Dispatchers.Default 计算密集型, 过高并行对CPU并无提升, 反而导致性能损耗
- Dispatchers.Main 不开启子线程, 在Android主线程中执行代码
  - 只能在Android项目中使用
  - **纯 Kotlin 程序使用会出现错误**
- Dispatchers.IO IO密集型, 大部分时间阻塞or等待, 故提高并行线程数量



### withContext

立即执行代码块中的代码, 挂起外部协程

代码块中的代码全部执行完, block的结果作为withContext的返回值

withContext()函数强制要求指定一个**线程参数**

效果是sync+await一样(?)

```kotlin
fun main() = runBlocking {
    val result = withContext(Dispatchers.Default) {
        5 + 5
    }
    println(result)
}
```

