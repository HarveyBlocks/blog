# 线程API

## 创建-启动线程

### Thread-Runnable

Thread啊. lambda Runnable啊, 啥的

```cpp
Thread thread1 = new Thread() {
    @Override
    public void run() {
        log.info("Run");
    }
};
Thread thread2 = new Thread(new Runnable() {
    @Override
    public void run() {
        log.info("Run");
    }
});
thread1.start();
thread2.start();
```

虽然IDLE都会推荐化为

```java
Thread thread2 = new Thread(() -> log.info("Run"));
```

### FeatureTask

获取方法结果

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/juc/线程与进程/Day01-Java原生多线程/image-20240905174644759.png" alt="image-20240905174644759" style="zoom: 33%;" />

使用

```java
FutureTask<Integer> futureTask = new FutureTask<>(
        new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                // Callable 能抛出异常, 有返回值
                log.info("in");
                Thread.sleep(1000);
                log.info("out");
                return Integer.valueOf("12");
            }
        });
// 开启新线程
new Thread(futureTask, "futureTask").start();
Integer result;
try {
    log.info("before get");
    result = futureTask.get();
    log.info("after get");
} catch (InterruptedException | ExecutionException e) {
    throw new RuntimeException(e);
}
log.info("result = " + result);
```

测试

```log
17:47:41.383 [main] INFO org.harvey.juc.Main -- before get
17:47:41.383 [futureTask] INFO org.harvey.juc.Main -- in
17:47:42.409 [futureTask] INFO org.harvey.juc.Main -- out
17:47:42.411 [main] INFO org.harvey.juc.Main -- after get
17:47:42.414 [main] INFO org.harvey.juc.Main -- result = 12
```

可见`get`方法是阻塞式的

添加超时检查

```cpp
try {
    log.info("before get");
    result = futureTask.get(500, TimeUnit.MILLISECONDS);
    log.info("after get");
} catch (InterruptedException | ExecutionException | TimeoutException e) {
    throw new RuntimeException(e);
}
```

Future在等待结果的过程中出现异常, 则会从Future抛出异常

```java
FutureTask<Integer> futureTask = new FutureTask<>(
        () -> {
            // Callable 能抛出异常, 有返回值
            log.info("in");
            int x = 1 / 0;
            log.info("out");
            return Integer.valueOf("12");
        });
new Thread(futureTask, "futureTask").start();
Integer result;
try {
    log.info("before get");
    result = futureTask.get();
    log.info("after get");
} catch (InterruptedException | ExecutionException e) {
    throw new RuntimeException(e);
}
log.info("result = " + result);
```

```log
22:21:49.253 [futureTask] INFO org.harvey.juc.Main -- in
22:21:49.253 [main] INFO org.harvey.juc.Main -- before get
Exception in thread "main" java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.ArithmeticException: / by zero
	at org.harvey.juc.Main.main(Main.java:47)
Caused by: java.util.concurrent.ExecutionException: java.lang.ArithmeticException: / by zero
	at java.base/java.util.concurrent.FutureTask.report(FutureTask.java:122)
	at java.base/java.util.concurrent.FutureTask.get(FutureTask.java:191)
	at org.harvey.juc.Main.main(Main.java:44)
Caused by: java.lang.ArithmeticException: / by zero
	at org.harvey.juc.Main.lambda$main$0(Main.java:36)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.lang.Thread.run(Thread.java:829)
```

## Thread常见方法

### 一览

#### 对象方法

| 方法名 | 概述 | 注意 |
| ------ | ---- | ---- |
|`start()`|启动一个新线程, 在新的线程运行`run`方法 中的代码   | `start`方法只是让线程进入就绪，里面代码不一定立刻运行 (CPU 的时间片还没分给它)。每个线程对象的`start`方法只能调用一次，如果调用了多次会出现 `IllegalThreadStateException` |
| `run()` | 新线程启动后会调用的方法 | 如果在构造 Thread 对象时传递了 Runnable 参数，则 线程启动后会调用 Runnable 中的 run 方法，否则默认不执行任何操作。但可以创建 Thread 的子类对象， 来覆盖默认行为 |
| `getId()` | 获取线程长整型的 id | id 唯一 |
| `getName()` | 获取线程名 | |
| `setName(String)` | 修改线程名 | |
| `getPriority()` | 获取线程优先级 | |
| `setPriority(int)` | 修改线程优先级 | java中规定线程优先级是1~10 的整数，较大的优先级 能提高该线程被 CPU 调度的几率, 但实际上还是操作系统掌握最终决定权 |
| `getState()` | 获取线程状态 | Java 中线程状态是用 6 个 enum`java.lang.Thread.State` |
| `isAlive() ` | 线程是否存活 | 指还没有运行完毕 |
| `isInterrupted()` | 判断是否被打断, 返回**打断标记**                   | 不会清除**打断标记(boolean) **(只读) |
| `interrupt()` | 打断线程 | 如果被打断线程正在`sleep`，`wait`，`join`会导致被打断的线程抛出`InterruptedException`，并清除**打断标记(boolean)**(置为false) ；如果打断的正在运行的线程，则会设置**打断标记(boolean)**(置为true\)；`park`的线程被打断，也会设置**打断标记(boolean)**(置为true) |
| `join()` | 等待线程运行结束 |      |
| `join(long n)` | 等待线程运行结束, **n毫秒**后超时                | |

#### 类方法

| 方法              | 概述                                                         | 注意                 |
| ----------------- | ------------------------------------------------------------ | -------------------- |
| `currentThread()` | 获取当前正在执行的线程                                       |                      |
| `sleep(long n)`   | 让当前执行的线程休眠n毫秒, 休眠时让出 CPU 的时间片给其它线程 |                      |
| `interrupted()`   | 判断当前线程是否被打断                                       | 会清除`打断标记`     |
| `yield()`         | 提示线程调度器, 让出当前线程对 CPU的使用                     | 主要是为了测试和调试 |

### sleep

建议使用`TimeUnit`的`sleep()`以提高可读性, 内部还是Thread.sleep

```java
try {
    TimeUnit.SECONDS.sleep(2); // 睡眠2秒
} catch (InterruptedException e) {
    throw new RuntimeException(e);
}
```

### yield

线程优先级会提示(hint)任务调度器优先调度该线程, 让出CPU使用去哪

这仅仅是一个提示, 不起决定性作用, 如果当前CPU很闲, 任务调度器可以选择忽略yield, 继续执行当前线程

如果CPU较忙, 更多的CPU时间片将分配给更高优先级的线程, 但CPU空闲时, 优先级几乎没有作用

### 线程优先级

优先级的大小越大, 优先级越高

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/juc/线程与进程/Day01-Java原生多线程/image-20240905194129268.png" alt="image-20240905194129268" style="zoom: 67%;" />

CPU空闲时, 优先级不会对任务调度器产生影响, 所有任务一律执行

CPU繁忙时, 优先级高的, 有更大的概率获取到CPU的时间片

### join-wait

join的底层是Object#wait()

区别在于Join的方法中, 在wait()之后被唤醒, 判断线程是否结束(isAlive), 若结束则不再阻塞, 否则继续wait()

### 打断

>   interrupt()

-   打断标记
    -   boolean
    -   被打断后被置为true
-   被打断线程正在`sleep`，`wait`，`join`
    -   导致被打断的线程抛出`InterruptedException`
    -   清除**打断标记(boolean)**(置为false) 
-   如果打断的正在运行的线程
    -   会设置**打断标记(boolean)**(置为true)
-   `park`的线程被打断
    -   设置**打断标记(boolean)**(置为true)

设计模式-两阶段终止

#### park

>   LockSupport#park()

打断park线程, 不会清空打断状态

当Interrupted为true时, park不会停下来阻塞

```java
Thread t = new Thread(() -> {
    log.debug("{}", Thread.currentThread().isInterrupted());
    log.debug("parking...");
    LockSupport.park();
    log.debug("un parking...");
    log.debug("{}", Thread.currentThread().isInterrupted());
    // 此时isInterrupted为真, park不再阻塞
    log.debug("parking again");
    LockSupport.park();
    log.debug("un parking again");
    log.debug("{}", Thread.interrupted());
    log.debug("{}", Thread.interrupted());
    // Thread#interrupted() 恢复interrupted为false
    // park重新恢复阻塞
    log.debug("parking again");
    LockSupport.park();
    log.debug("un parking again");
});
t.start();
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    throw new RuntimeException(e);
}
t.interrupt();
```

```log
22:25:52.258 [Thread-0] DEBUG org.harvey.juc.Main -- false
22:25:52.277 [Thread-0] DEBUG org.harvey.juc.Main -- parking...
22:25:53.253 [Thread-0] DEBUG org.harvey.juc.Main -- un parking... # 时间差长
22:25:53.254 [Thread-0] DEBUG org.harvey.juc.Main -- true
22:25:53.254 [Thread-0] DEBUG org.harvey.juc.Main -- parking again
22:25:53.254 [Thread-0] DEBUG org.harvey.juc.Main -- un parking again # 几乎没有时间差
22:25:53.254 [Thread-0] DEBUG org.harvey.juc.Main -- true
22:25:53.254 [Thread-0] DEBUG org.harvey.juc.Main -- false # 已经被重置为false了
22:25:53.254 [Thread-0] DEBUG org.harvey.juc.Main -- parking again 
# 长期阻塞
```

## 不推荐的方法

### stop()

直接停止线程的运行

### suspend()

挂起(暂停)线程的运行

### resume()

恢复线程的运行

