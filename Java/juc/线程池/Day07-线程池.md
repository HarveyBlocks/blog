# ExecutorService

线程池在出异常后(没有被捕捉) 会创建新线程顶替, 保证始终有线程执行任务

![image-20240912164945165](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/juc/线程池/Day07-线程池/image-20240912164945165.png)

Scheduled定时任务

## 线程池状态

```java
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
```

用int表示线程池状态

高3位标识状态, 低29位表示线程池数量

```java
// Packing and unpacking ctl
private static int runStateOf(int c)     { return c & ~COUNT_MASK; }
private static int workerCountOf(int c)  { return c & COUNT_MASK; }
private static int ctlOf(int rs, int wc) { return rs | wc; }
```

现在怎就扣扣嗖嗖的了?为了共用一个原子变量, 省的维护两个原子变量的CAS

```java
private void advanceRunState(int targetState) {
    // assert targetState == SHUTDOWN || targetState == STOP;
    for (;;) {
        int c = ctl.get();
        if (runStateAtLeast(c, targetState) ||
            ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))))
            break;
    }
}
```

| 状态       | 高3位二进制 | 接收新任务 | 处理阻塞任务队列 | 说明                                            |
| ---------- | ----------- | ---------- | ---------------- | ----------------------------------------------- |
| RUNNING    | 111         | Y          | Y                |                                                 |
| SHUTDOWN   | 000         | N          | Y                | 不会接收新任务, 单位处理阻塞队列剩余任务        |
| STOP       | 001         | N          | N                | 中断正在执行的任务, 并抛弃任务队列中的任务      |
| TIDYING    | 010         | -          | -                | 任务全执行完毕吗活动线程状态全部为0即将进入终结 |
| TERMINATED | 011         | -          | -                | 终结状态, 所有线程都不再运行                    |

### 状态获取

```java
boolean isSutdown();
```

-   RUNNING返回假, 其余返回真

```java
boolean isTerminated();
```

-   TERMINATED返回真, 其余返回假

```java
boolean awaitTerminated(long,TimeUtil) throw InterruptedException;
```

-   等待直到所有线程真的全部结束

## 构造

```java
public ThreadPoolExecutor(int corePoolSize,  // 核心线程数
                          int maximumPoolSize, // 最大线程数
                          long keepAliveTime, // 空闲线程对新任务的最长等待时间
                          TimeUnit unit, // 存活时间的单位(针对idle线程)
                          BlockingQueue<Runnable> workQueue, // 阻塞队列
                          ThreadFactory threadFactory, // 线程工厂, 可以用来规范线程的名字
                          RejectedExecutionHandler handler); // 拒绝策略
```

1.  任务来了

2.  创建新核心线程执行任务

3.  任务来了

    -   上一个任务执行完了

        -> 创建新核心线程执行任务(很奇妙, 就是这样子的)

    -   上一个任务执行没完

        -> 创建新核心线程执行任务

4.  任务来了, 核心线程还在执行, 任务队列上限了

5.  创建idle线程

6.  从任务队列中取出任务

7.  idle线程执行任务

8.  任务结束了

9.  idle线程开始等待keepAliveTIme期间的任务

10.  没任务, idle线程下线了

11.  核心线程时间长了会下线吗? 可以配置`allowCoreThreadTimeOut`

## 拒绝策略

![image-20240912182626056](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/juc/线程池/Day07-线程池/image-20240912182626056.png)

-   AbortPolicy 抛出RejectedExceutionException 默认
-   CallerRunsPolicy 提供任务的线程执行该任务
-   DiscardPolicy 啥也不做, 直接忽略
-   DiscardOldestPolicy 忽略最老的任务
-   Dubbo 抛出异常前记录日志, 用dump记录线程栈信息
-   Netty 创建新线程执行任务(你对我的CPU也太自信了吧😓)
-   ActiveMQ 超时等待60s尝试后放入队列
-   PinPoint 拒绝策略链, 逐一尝试策略链中每种拒绝策略

## Excutors

自己看源码

-   FixedThreadPool

    -   任务频率不高, 任务耗时较长

-   CacheThreadPool
    -   线程数量会根据任务量不断增长, 没有上限
    -   所有的线程都可以在60s无任务后回收
    -   任务密集但每个任务执行时间较短

-   SingleThreadExecutor

    -   希望任务是串行的效果, 排队执行任务

    -   FinalizableDelegatedExecutorService

        ![image-20240912202632394](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/juc/线程池/Day07-线程池/image-20240912202632394.png)

        包装类`FinalizableDelegatedExecutorService`是一个私有内部类

        直接返回一个ThreadPoolExecutor, 即使返回值类型是ExecutorService, 也可以用强制类型转换转成ThreadPoolExecutor

        但是`FinalizableDelegatedExecutorService`封装之后, 就不能转化成ThreadPoolExecutor了, 只能是ExecutorService了

        也就是说, 只能调用ExecutorService的那些方法了, ThreadPoolExecutor的拓展方法都不能调用了

        例如, ThreadPoolExecutor中有能更改核心线程数量的方法, ExecutorService没有这种方法

        保证了单线程的线程池不会被破坏

## 提交任务API

```java
// 执行任务
void execute(Runnable command);

<T> Future<T> submit(Callable<T> task);

// 提交 tasks 中所有任务, 排到任务队列后面, tasks全部执行完了, 才能获取到List<Future>
<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
 throws InterruptedException;

<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
 long timeout, TimeUnit unit)
 throws InterruptedException;

// 哪个任务先成功执行完毕，返回此任务执行结果，其它任务打断
<T> T invokeAny(Collection<? extends Callable<T>> tasks)
 throws InterruptedException, ExecutionException;

<T> T invokeAny(Collection<? extends Callable<T>> tasks,
 long timeout, TimeUnit unit)
 throws InterruptedException, ExecutionException, TimeoutException;

```

invokeAll

```java
public static void main(String[] args) {
    ExecutorService service = Executors.newFixedThreadPool(3);
    Future<String> submit = service.submit(() -> {
        log.debug("执行!");
        sleep(15);
        return "执行完成15";
    });
    List<Future<String>> futures;
    try {
        futures = service.invokeAll(List.of(() -> {
            log.debug("begin");
            sleep(1);
            return "你好1";
        }, () -> {
            log.debug("begin");
            sleep(10);
            return "你好10";
        }));
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    getOnNewThread(submit);
    getOnNewThread(futures.get(0));
    getOnNewThread(futures.get(1));
}

private static void getOnNewThread(Future<String> future) {
    new Thread(() -> {
        try {
            log.debug(future.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }).start();
}
```

```log
20:53:33.629 [pool-1-thread-3] DEBUG org.harvey.juc.Main -- begin
20:53:33.629 [pool-1-thread-1] DEBUG org.harvey.juc.Main -- 执行!
20:53:33.629 [pool-1-thread-2] DEBUG org.harvey.juc.Main -- begin
20:53:43.652 [Thread-1] DEBUG org.harvey.juc.Main -- 你好1
20:53:43.652 [Thread-2] DEBUG org.harvey.juc.Main -- 你好10
20:53:48.642 [Thread-0] DEBUG org.harvey.juc.Main -- 执行完成15
```

## 关闭线程池

```java
void shutdown();
```

-   状态置为SHUTDOWN
-   不接收新任务
-   打断空闲线程
-   执行完成已提交任务
-   不阻塞调用线程的执行

```java
List<Runnable> shutdownNow();
```

-   状态置为STOP
-   不接收新任务, 返回未完成任务
-   打断所有线程

