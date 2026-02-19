# wait-notify

![image-20240907003307780](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/juc/共享模型/Day03-wait-notfy/image-20240907003307780.png)

在wait-notify中, Owner线程调用`wait()`, 将Owner存入Monitor的`WaitSet`字段, 而后将临界区开放给EntryList中的其他线程

待到新进入临界区的线程使用`notify()`的方法时, 再唤醒`WaitSet`中的线程

## 原理

1.  Owner线程依据需求调用锁对象(因为Monitor是和锁对象对应的)的`wait()`方法
2.  Owner线程存入`WaitSet`, 进入WAITING状态, 不占用CPU时间片
3.  Owner线程释放锁对象
4.  处于EntryList的处于BLOCKED的线程, 在Owner线程释放锁时被唤醒, 竞争出新Owner
5.  WAITING线程会在Owner线程中调用锁对象的`notify/All()`方法时唤醒
6.  唤醒后进入EntryLIst重新参与竞争

## 使用

-   `lockObj.wait()` 			进入`lockObj`的监视器的线程转移到`WaitSet`进行等待
-   `lockObj.notify()`         在`lockObj`的监视器的`WaitSet`等待的线程中选出一个唤醒
-   `lockObj.notifyAll()`   在`lockObj`的监视器的`WaitSet`等待的线程中唤醒所有线程然后一并竞争

 必须在获取了对象锁(才有Monitor)之后才能被调用

```java
public static void testWaitNotify() {
    // 验证Wait的线程被之后Notify, 会加入EntryList与Blocked的线程一同竞争, 而不是被优先唤醒
    // 判断方法: notifyAll之后开启的线程不一定会打印wait方法执行之后的"等待后被唤醒"语句
    //          而是会打印新的"等待开始"语句
    List<Thread> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        list.add(new Thread(() -> {
            LockSupport.park();
            waitMethod();
        }, "wait" + i));
    }
    list.add(new Thread(() -> {
        LockSupport.park();
        notifyMethod();
    }, "notifyAll"));
    for (int i = 10; i < 20; i++) {
        list.add(new Thread(() -> {
            LockSupport.park();
            waitMethod();
        }, "wait" + i));
    }
    for (Thread thread : list) {
        thread.start();
    }
    for (Thread thread : list) {
        LockSupport.unpark(thread);
    }
}

private static void waitMethod() {
    synchronized (LOCK) {
        try {
            try {
                // 这里等待200ms是为蓄力, 厚积薄发
                // (确保本使用wait的线程确实锁住了LOCK对象, 从而保证没有其他线程并行运行)
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("等待开始");
            LOCK.wait();
            log.debug("等待后被唤醒");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

private static void notifyMethod() {
    synchronized (LOCK) {
        log.debug("notifyAll");
        try {
            // 这里等待两秒是为蓄力, 厚积薄发
            // (确保本使用notifyAll的线程确实锁住了LOCK对象, 从而保证没有其他线程并行运行)
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOCK.notifyAll();// 能保证只有当前线程被调用之前进入等待的线程全部被唤醒
    }
}
```

甚至发现被notify的线程往往是最后被竞争到锁的

-   wait(long timeout);

    -   wait()->wait(0)
    -   等待timeout之后唤醒, 或者被notify唤醒
    -   唤醒之后同样要经过竞争(验证代码如下)

    ```java
    public static void testWaitNotify() {
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new Thread(() -> {
                LockSupport.park();
                waitMethod();
            }, "wait" + i));
        }
        for (Thread thread : list) {
            thread.start();
        }
        for (Thread thread : list) {
            LockSupport.unpark(thread);
        }
    }

    private static void waitMethod() {
        synchronized (LOCK) {
            try {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("等待开始");
                LOCK.wait(100);
                log.debug("等待后被唤醒");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    ```

## sleep(long)和wait(long)

都能让出当前线程的使用权, 线程状态都是`TIMED_WAITING`

-   sleep是Thread的静态方法, wait的Object的方法
-   sleep不需要强制和synchronized配合使用, 但wait和synchronized一起使用
-   sleep在睡眠的同时不会释放对象锁, 但wait在等待的时候会释放对象锁
-   wait会被notify唤醒, sleep会等到睡眠时间结束, 或interrupt才会被唤醒

## wait-notify使用规范

### sleep改wait

sleep不会释放对象锁, 因此会长期占用对象锁资源, 而其他线程依旧获取不到锁只能长期阻塞

而且sleep定多少时间不确定

因此建议使用wait让出CPU片和锁资源, 以增加效率

### 错误唤醒/虚假唤醒

多个线程的对象锁wait等待一个notify

如果一个线程完成某个前置任务后notify, 那么可能唤醒需要这个前置任务的线程, 也可能唤醒需要其他前置任务的线程

```java
static boolean finishedPreTask1 = false, finishedPreTask2 = false;

public static void main(String[] args) {
    new Thread(() -> {
        task1();
    }, "t1").start();
    new Thread(() -> {
        task2();
    }, "t2").start();
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    synchronized (LOCK) {
        finishedPreTask1 = true;
        log.info("Finished Pre Task1");
        LOCK.notify();
    }
}

private static void task1() {
    synchronized (LOCK) {
        if (!finishedPreTask1) {
            log.warin("Can't do next");
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!finishedPreTask1) {
                log.error("Can't do next");
                return;
            }
        }
        log.info("do next");
    }
}
private static void task2() {
    synchronized (LOCK) {
        if (!finishedPreTask2) {
            log.warin("Can't do next");
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!finishedPreTask2) {
                log.error("Can't do next");
                return;
            }
        }
        log.info("do next");
    }
}
```

如果是需要其他前置任务的线程被唤醒了, 那么那个需要这个前置任务的线程无法被唤醒, 那这一条线程将永远处于等待

解决方法: ==唤醒所有等待线程, 则必定唤醒需要当前前置任务的线程==

```java
synchronized (LOCK) {
    finishedPreTask1 = true;
    log.info("Finished Pre Task1");
    LOCK.notifyAll();
}
```
同时, 如果是需要其他前置任务的线程被唤醒了, 它的前置任务依然没有被完成, 它的后续任务的完成是不正确的

解决方法: ==唤醒之后检查当前任务的前置任务是否完成, 未完成则重新等待, 以此**往复**==

```java
private static void task1() {
    synchronized (LOCK) {
        while (!finishedPreTask1) {
            waitForFinishPre();
        }
        log.info("do next");
    }
}
private static void task2() {
    synchronized (LOCK) {
        while (!finishedPreTask2) {
            waitForFinishPre();
        }
        log.info("do next");
    }
}
private static void waitForFinishPre() {
    log.warn("Can't do next temporary");
    try {
        LOCK.wait();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
```

### wait-notify模板

wait接口

```java
public interface WaitStandardPattern extends Runnable {
    Object getLock();
}

```

wait实现

```java
public abstract class AbstractWaitStandardPattern implements WaitNotifyPattern {
    private final Object lock;

    protected AbstractWaitNotifyPattern(Object lock) {
        this.lock = lock;
    }

    protected AbstractWaitNotifyPattern() {
        this.lock = this;
    }

    @Override
    public final void run() {
        synchronized (lock) {
            while (!this.isPrepared()) {
                this.executeIfUnprepared();
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            this.executeIfPrepared();
        }
    }

    @Override
    public final Object getLock() {
        return lock;
    }

    protected abstract boolean isPrepared();

    protected abstract void executeIfPrepared();

    protected abstract void executeIfUnprepared();
}
```

notify尽可能使用notifyAll

```java
public interface NotifiedStandardPattern extends Runnable {

}
```

```java
public abstract class AbstractNotifiedStandardPattern implements NotifiedStandardPattern {
    private final WaitStandardPattern waitPattern;

    public AbstractNotifiedStandardPattern(WaitStandardPattern waitPattern) {
        this.waitPattern = waitPattern;
    }

    @Override
    public void run() {
        this.notifyWait(waitPattern);
    }

    private void notifyWait(WaitStandardPattern waitPattern) {
        Object lock = waitPattern.getLock();
        synchronized (lock) {
            this.finishPreTask();
            lock.notifyAll();
        }
    }

    protected abstract void finishPreTask();
}
```

