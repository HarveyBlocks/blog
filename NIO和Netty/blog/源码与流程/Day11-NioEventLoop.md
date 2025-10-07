# NioEventLoop

-   NioEventLoop的重要组成
    -   selector
    -   线程
    -   任务队列
-   NioEventLoop既会处理IO事件, 又会处理普通任务和定时任务

## Selector创建

1.  `NioEventLoop`构造器:

    ```java
    NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider,
                 SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler,
                 EventLoopTaskQueueFactory taskQueueFactory, EventLoopTaskQueueFactory tailTaskQueueFactory) {
        ...
        final SelectorTuple selectorTuple = openSelector();
        ...
    }
    ```

2.  ->`NioEventLoop#openSelector()`

    ```java
    unwrappedSelector = provider.openSelector();
    ```



### NioEventLoop的两个Selector成员

```java
private Selector selector;
private Selector unwrappedSelector;
```

实际上,被创建出来的是`unwrappedSelector`,

但是JDK使用的Selector内部的SelectionKeys使用的是集合, 遍历的效率不高(底层HashMap)

Netty选择使用数组来存储SelectionKeys, 以提高效率



## NIO线程在启动

NIO的线程加载采用懒惰加载, 在第一次执行该异步任务的时候创建线程, 并且只会启动一次

```java
private void execute(Runnable task, boolean immediate) {
    boolean inEventLoop = inEventLoop(); // 判断当前线程和EventLoop的线程是否是同一线程
    addTask(task);
    if (!inEventLoop) {
        // 不是同一个线程
        startThread(); // 见下
        if (isShutdown()) {
            boolean reject = false;
            try {
                if (removeTask(task)) {
                    reject = true;
                }
            } catch (UnsupportedOperationException e) {
                // The task queue does not support removal so the best thing we can do is to just move on and
                // hope we will be able to pick-up the task before its completely terminated.
                // In worst case we will log on termination.
            }
            if (reject) {
                reject();
            }
        }
    }

    if (!addTaskWakesUp && immediate) {
        wakeup(inEventLoop);
    }
}
```

-   ` startThread()`

    ```java
    if (state == ST_NOT_STARTED) { // 是ST_NOT_STARTED: 未启动的状态
        if (STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
            boolean success = false; // 确保线程只会被启动一遍
            try {
                doStartThread();
                success = true;
            } finally {
                if (!success) {
                    STATE_UPDATER.compareAndSet(this, ST_STARTED, ST_NOT_STARTED);
                }
            }
        }
    }
    ```

-   `doStartThread()`

    ```java
    assert thread == null;
    executor.execute(new Runnable() {
        @Override
        public void run() {
            thread = Thread.currentThread(); // 让这个新创建的线程赋值给EventLoop的线程属性
            if (interrupted) {
                thread.interrupt();
            }
    
            boolean success = false;
            updateLastExecutionTime();
            try {
                SingleThreadEventExecutor.this.run(); // NioEventLoop的Run方法
                success = true;
            } catch (Throwable t) {
                logger.warn("Unexpected exception from an event executor: ", t);
            } finally {
                // ....
            }
        }
    });
    ```

-   `run()`

    ```java
    protected void run() {
        int selectCnt = 0;
        for (;;) {
            try {
                
            } catch (Error e) {
                // ...
            } finally {
                // ...
            }
        }
    }
    ```

## select阻塞

###select的定时阻塞

在原生的JDK的Select中, 为了防止空轮询占用CPU, Select选择阻塞, 直到有IO事件到来, 才唤醒线程

但是对于Netty来说, Select不仅有做IO任务的作用, 还要起到执行普通任务和定时任务的职责. 

所以, Netty设计了可以配置超时时间的select, 在有时间或超过超时时间时, 线程被唤醒

```java
@Override
public int select(long timeout) throws IOException {
    selectionKeys.reset();
    return delegate.select(timeout);
}
```



### wakeup()方法

`selector.wakeup()`是一个重量级的任务, 需要尽量减少对其的调用

如果有多个线程同时提交任务, selector只需要被唤醒一次就够了

使用` nextWakeupNanos.getAndSet(AWAKE) != AWAKE `(nextWakeupNanos是原子变量, 当多线程来的时候, 会先得到原子变量的值, 可能有多个线程拿到, 但总有一个线程会先更改值, 原子变量里会将各个线程拿到的值和当前值进行比较, 如果不一样, 就不能进入修改, 以保证只有一个线程能修改的wakeup的boolean值)

`NioEventLoop`

```java
@Override
protected void wakeup(boolean inEventLoop) {
    // inEventLoop = 当前线程等于NioEventLoop里的线程字段
    if (!inEventLoop // 非NioEventLoop线程调用了wakeup;
        && nextWakeupNanos.getAndSet(AWAKE) != AWAKE 
    ) {
        selector.wakeup();
    }
}
```

## 进入SelectStrategy分支的条件

```java
protected void run() {
    int selectCnt = 0;
    for (; ; ) {
        try {
            int strategy;
            try {
                strategy = selectStrategy.calculateStrategy(selectNowSupplier, hasTasks());
                // ...
                switch (strategy) {
                    case SelectStrategy.BUSY_WAIT:
                        // ...

                    case SelectStrategy.SELECT:
                        // 何时进入该分支?
                        // ...

                        if (...){
                        strategy = select(curDeadlineNanos);
                    }

                    // ...
                    default:
                }
            }
        } catch (IOException e) {
            // ...
            continue;
        }
    }
}
```

-   `selectStrategy.calculateStrategy(selectNowSupplier, hasTasks());`

    ```java
    final class DefaultSelectStrategy implements SelectStrategy {
    	// ...
        
        @Override
        public int calculateStrategy(IntSupplier selectSupplier, boolean hasTasks) throws Exception {
            // hasTasks(), 没有任务, 返回假 , 进入SELECT分支
            return hasTasks ? selectSupplier.get() : SelectStrategy.SELECT;
            // 有任务->get->selectNow()
        }
    }
    ```

-   `selectNow()`

    ```java
    int selectNow() throws IOException {
        // 立刻到Selector上查看有没有事件, 如果有, 就返回事件, 如果没有, 就返回0,表示没有事件
        return this.selector.selectNow();
    }
    ```

-   不进入Select分支, 就不会被select()方法阻塞, 就可以顺利执行普通任务

### selctor阻塞时长

```java
@Override
public int select(long timeout) throws IOException {
    selectionKeys.reset();
    return delegate.select(timeout);
}
```

`timeout`

`nanos`纳秒

-   `timeoutMillis`

    ```java
    long timeoutMillis = deadlineToDelayNanos(deadlineNanos + 995000L) / 1000000L; // 1s-5ms
    ```

    -   `curDeadlineNanos`

        ```java
        long curDeadlineNanos = nextScheduledTaskDeadlineNanos();
        // ...
        strategy = select(curDeadlineNanos);
        ```

        `nextScheduledTaskDeadlineNanos`

        ```java
        protected final long nextScheduledTaskDeadlineNanos() {
            ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
            return scheduledTask != null ? scheduledTask.deadlineNanos() : -1;
        }
        ```

    -   `deadlineToDelayNanos()`

        ```java
        protected static long deadlineToDelayNanos(long deadlineNanos) {
            return ScheduledFutureTask.deadlineToDelayNanos(defaultCurrentTimeNanos(), deadlineNanos);
        }
        ```

        `defaultCurrentTimeNanos()`

        ```java
        static long defaultCurrentTimeNanos() {
            return System.nanoTime() - START_TIME;
        }
        ```

        `deadlineToDelayNanos`

        ```java
        static long deadlineToDelayNanos(long currentTimeNanos, long deadlineNanos) {
            return deadlineNanos == 0L ? 0L : Math.max(0L, deadlineNanos - currentTimeNanos);
        }
        ```

        

### NIO空轮询的Bug

#### 体现

JDK `Selector#select()`底层的Bug, 在Linux操作系统中小概率发生

#### 解决

```java
@Override
protected void run() {
    int selectCnt = 0;
    for (;;) {
        try {
            int strategy;
            try {
                // ...
                strategy = select(curDeadlineNanos);
                // 如果select没有阻塞住, 就会疯狂空轮询
                // ...
            } catch (IOException e) {
                // ...
                selectCnt = 0;
                continue;
            }

            selectCnt++; // 一旦疯狂空轮询, selectCnt的大小就会疯狂上涨,
            // ...

            if (ranTasks || strategy > 0) { // strategy > 0 表示不是IO任务, 是普通任务或定时任务
                if (selectCnt > MIN_PREMATURE_SELECTOR_RETURNS && logger.isDebugEnabled()) {
                    logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.",
                            selectCnt - 1, selector);
                    // Selector.select()为 Selector :{selector} 提前返回, 循环了 {selectCnt - 1} 次
                }
                selectCnt = 0;
            } else if (unexpectedSelectorWakeup(selectCnt)) { // Unexpected wakeup (unusual case)
                selectCnt = 0;
            }
        } catch (CancelledKeyException e) {
            // ...
        }
    }
}
```



```java
// 如果selectCnt需要被重置就返回true
private boolean unexpectedSelectorWakeup(int selectCnt) {
    if (Thread.interrupted()) { // interrupted 打断
        // Thread was interrupted so reset selected keys and break so we not run into a busy loop.
        // 线程已被中断, 因此重置selected keys 并且跳出循环, 以保证我们不会做一个空轮询
        // As this is most likely a bug in the handler of the user or it's client library we will
        // 因为这很可能是一个由于用户或者其他的客户端库造成的, 所以我们会
        // also log it.
        // 将他记录日志
        // See https://github.com/netty/netty/issues/2426
        if (logger.isDebugEnabled()) {
            logger.debug("Selector.select() returned prematurely because " +
                    "Thread.currentThread().interrupt() was called. Use " +
                    "NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
        }
        return true;
    }
    if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 &&
            selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
        // SELECTOR_AUTO_REBUILD_THRESHOLD 设置的阈值, 默认512
        // The selector returned prematurely many times in a row.
        // selector过早返回, 这是由于连续多次的空轮询。
        // Rebuild the selector to work around the problem.
        // 重新生成selector以解决此问题
        logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.",
                selectCnt, selector);
        // Selector.select()提前返回, 空轮询了 {selectCnt - 1} 次, 我们将执行重建Select :{selector}
        rebuildSelector(); // 比较复杂qwq 
        // 1. 创建新的Selector
        // 2. 将原来Selector 的key, 关注的Channel , 关注的事件等等复制过去
        // 3. 替换带哦原来的Selector
        return true;
    }
    return false;
}
```

-   阈值`SELECTOR_AUTO_REBUILD_THRESHOLD`

    ```java
    // "io.netty.selectorAutoRebuildThreshold" 系统环境变量
    // -Dio.netty.selectorAutoRebuildThreshold
    int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);
    if (selectorAutoRebuildThreshold < MIN_PREMATURE_SELECTOR_RETURNS) { 
        // MIN_PREMATURE_SELECTOR_RETURNS是3直接写死
        selectorAutoRebuildThreshold = 0;
    }
    
    SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
    ```

    

Netty也有一套完全重写了的Selector, 直接取缔了JDK原生的Selelctor, 已解决空轮询Bug

## IoRatio

`run()`

```java
final int ioRatio = this.ioRatio;
boolean ranTasks;
if (ioRatio == 100) {
    try {
        if (strategy > 0) {
            processSelectedKeys(); // 执行IO任务
        }
    } finally {
        // Ensure we always run tasks.
        ranTasks = runAllTasks(); // IO任务执行完, 执行普通任务
    }
} else if (strategy > 0) {
    final long ioStartTime = System.nanoTime(); 
    try {
        processSelectedKeys(); // 执行IO任务
    } finally {
        // Ensure we always run tasks.
        final long ioTime = System.nanoTime() - ioStartTime;
        ranTasks = runAllTasks(ioTime * (100 - ioRatio) / ioRatio);  // IO任务执行完, 执行普通任务
    }
} else {
    ranTasks = runAllTasks(0); // This will run the minimum number of tasks
    // 参数为0 , 将会把最小的任务时间分配给普通任务来做, 而不是一点也不做
}
```

问:  当普通任务队列的耗时较长时, 会不会影响IO任务?

NioEventLoop是个单线程啊

### 控制对象

为了防止普通任务的消耗时间过长, 影响了IO时间, Netty会做一个参数`ioRatio`的控制

**ioRatio是控制处理IO事件所占用的时间比例**

默认是50%, 一半时间用来处理IO事件, 另一半事件用来处理普通任务队列

```
ranTasks = runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
```

-   `ioTime * (100 - ioRatio) / ioRatio` 允许普通任务队列花的时间
-   超出分配的时间, 直接终止任务的进行



设置成100, 就是**取消了的IoRatio的分配任务时间这个功能**, Netty就会不再干涉普通任务的执行时间

```java
ranTasks = runAllTasks();
```

`runAllTasks`的无参方法, 就是用来不加顾及地执行普通任务的

```java
protected boolean runAllTasks(long timeoutNanos) {
    fetchFromScheduledTaskQueue();
    Runnable task = pollTask();
    if (task == null) {
        afterRunningAllTasks();
        return false;
    }

    // 对时间的限制
    final long deadline = timeoutNanos > 0 ? getCurrentTimeNanos() + timeoutNanos : 0;
    long runTasks = 0; // 当前是做到第几个任务了
    long lastExecutionTime; // 上一次任务做完之后, 
    for (;;) {
        safeExecute(task);

        runTasks ++;

        // Check timeout every 64 tasks because nanoTime() is relatively expensive.
        // 每63个任务做检查一次检查, 因为 nanoTime()的消耗太高
        // XXX: Hard-coded value - will make it configurable if it is really a problem.
        // 硬编码值 - 如果确实有问题，将使其可配置。 (我怀疑这里是作者的TODO)
        if ((runTasks & 0x3F) == 0) { // 对于deadline是0来说, 做完前64个任务, 就可以和说bye,bye了
            // 也就是说, 64个任务就是上面说的最小值
            lastExecutionTime = getCurrentTimeNanos();
            if (lastExecutionTime >= deadline) {
                break;
            }
        }

        task = pollTask();
        if (task == null) {
            lastExecutionTime = getCurrentTimeNanos();
            break;
        }
    }

    afterRunningAllTasks();
    this.lastExecutionTime = lastExecutionTime;
    return true;
}
```

## selectedKey优化

把JDK的原生的Set集合改成了数组 , 加快了的遍历数组的效率

```java
private void processSelectedKeys() {
    if (selectedKeys != null) {
        // selectedKeys集合, 集合为空, 说明Netty已经把原来的Select的集合转换成了数组
        processSelectedKeysOptimized();
    } else {
        processSelectedKeysPlain(selector.selectedKeys());
    }
}
```

```java
private void processSelectedKeysOptimized() {
    for (int i = 0; i < selectedKeys.size; ++i) {
        final SelectionKey k = selectedKeys.keys[i];
        // null out entry in the array to allow to have it GC'ed once the Channel close
        // See https://github.com/netty/netty/issues/2363
        selectedKeys.keys[i] = null;

        // 获取key上关联的附件, 依据启动流程, 此附件乃是Channel
        final Object a = k.attachment();

        if (a instanceof AbstractNioChannel) {
            processSelectedKey(k, (AbstractNioChannel) a); // 区分不同事件并执行
        } else {
            @SuppressWarnings("unchecked")
            NioTask<SelectableChannel> task = (NioTask<SelectableChannel>) a;
            processSelectedKey(k, task);
        }

        if (needsToSelectAgain) {
            // null out entries in the array to allow to have it GC'ed once the Channel close
            // See https://github.com/netty/netty/issues/2363
            selectedKeys.reset(i + 1);

            selectAgain();
            i = -1;
        }
    }
}
```

## 区分不同事件类型

对事件的区分

```java
private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
    // ...
    int readyOps = k.readyOps();
    // We first need to call finishConnect() before try to trigger a read(...) or write(...) as otherwise
    // the NIO JDK channel implementation may throw a NotYetConnectedException.
    if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
        // 客户端的连接事件
        
        // remove OP_CONNECT as otherwise Selector.select(..) will always return without blocking
        // See https://github.com/netty/netty/issues/924
        int ops = k.interestOps();
        ops &= ~SelectionKey.OP_CONNECT;
        k.interestOps(ops);

        unsafe.finishConnect();
    }

    // Process OP_WRITE first as we may be able to write some queued buffers and so free memory.
    if ((readyOps & SelectionKey.OP_WRITE) != 0) {
        // 可写事件
        // Call forceFlush which will also take care of clear the OP_WRITE once there is nothing left to write
        unsafe.forceFlush();
    }

    // Also check for readOps of 0 to workaround possible JDK bug which may otherwise lead
    // to a spin loop
    if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
        // 可读事件
        unsafe.read();
    }
    // ...
}
```

## 