# 自定义线程池

只是写一个思路, 没用考虑安全性和效率

## 自定义活动线程

在线程创建之初就启动线程

```java
package org.harvey.juc;

import java.util.Queue;

/**
 * 活动线程
 * 在线程创建之初就启动线程
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-09-05 14:03
 */
public class ActiveThread {
    private final Thread thread;
    private boolean running = true; // 在调用close方法之后running为false
    private boolean force = false; // 在关闭时是否需要等待所有taskQueue中的任务都完成?还是强制关闭?

    private Runnable advice(Queue<Runnable> taskQueue) {
        return () -> {
            while (true) {
                Runnable task;
                synchronized (taskQueue) {
                    if (checkStopThread(taskQueue)) {
                        break;
                    }
                    task = waitForTask(taskQueue);
                }
                if (task == null) {
                    continue;
                }
                task.run();
            }
        };
    }

    private boolean checkStopThread(Queue<Runnable> taskQueue) {
        return !running && (force || taskQueue.isEmpty());
    }

    private static Runnable waitForTask(Queue<Runnable> taskQueue) {
        while (taskQueue.isEmpty()) {
            try {
                taskQueue.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return taskQueue.poll();
    }

    public ActiveThread(Queue<Runnable> taskQueue) {
        thread = new Thread(advice(taskQueue));
        thread.start();
    }

    public ActiveThread(Queue<Runnable> taskQueue, String name) {
        thread = new Thread(advice(taskQueue), name);
        thread.start();
    }

    public void close(boolean force) {
        running = false;
        this.force = force;
    }

    public void join() throws InterruptedException {
        thread.join();
    }
}
```

## 活动线程线程池

```java
package org.harvey.juc;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 线程池, 里面管理的线程全部都是一创建就启动的
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-09-05 14:07
 */
public class ActivePool {

    private final List<ActiveThread> threadList = new ArrayList<>();
    private final Queue<Runnable> taskQueue;
    private boolean running = true;

    public ActivePool(int count) {
        taskQueue = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < count; i++) {
            threadList.add(new ActiveThread(taskQueue));
        }
    }

    public void exec(Runnable task) {
        if (!this.running) {
            return;
        }
        synchronized (taskQueue) {
            taskQueue.offer(task);
            taskQueue.notify();
        }
    }

    public void join() throws InterruptedException {
        for (ActiveThread activeThread : threadList) {
            activeThread.join();
        }
    }

    private void close(boolean force, boolean clog) throws InterruptedException {
        for (ActiveThread activeThread : threadList) {
            activeThread.close(force);
        }
        this.running = false;
        if (clog) {
            this.join();
        }
    }

    public void shutdown(boolean clog) throws InterruptedException {
        this.close(false, clog);
    }

    
}
```

## 使用Demo

```java
private static class Sum {
    int value = 0;

    void add(int plus) {
        this.value += plus;
    }

    public int get() {
        return value;
    }
}

public static void demo() {
    final Random random = new Random(System.currentTimeMillis());
    final Sum sum = new Sum();
    long start = System.currentTimeMillis();
    ActivePool activePool = new ActivePool(20);
    for (int i = 0; i < 200; i++) {
        String a = "" + i;
        activePool.exec(() -> {
            long inStart = System.currentTimeMillis();
            System.out.println(a);
            try {
                Thread.sleep(1000 + random.nextInt() % 500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            long inEnd = System.currentTimeMillis();
            sum.add((int) (inEnd - inStart));
        });
    }
    try {
        activePool.shutdown(true); // 关闭线程的行为是阻塞的
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    for (int i = 200; i < 300; i++) {
        String a = "" + i;
        activePool.exec(() -> {
            System.out.println(a);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
    long end = System.currentTimeMillis();
    System.out.println("串行花销: " + sum.get() / 1000.0 + " s");
    System.out.println("并行花销: " + (end - start) / 1000.0 + " s");
}
```
## 更多需求

-   任务上限 给任务队列设置上限
-   总线程上限, 活动线程数的创建和回收
-   拒绝策略 任务队列满了之后, 是直接返回, 还是抛出异常, 还是等待, 等待是timeout还是永久等待
-   

# 自定义异步

## 自定义异步类

```java
package org.harvey.juc;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 异步类, 只是思路, 在格式上和Java原生的FeatureTask完全不同
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-09-05 14:03
 */
public class MyFeature<R> {
    private final Thread thread;
    private final Result<R> result;

    private static class Result<R> {
        private R value;

        public R get() {
            return value;
        }

        public void set(R value) {
            this.value = value;
        }
    }

    private Runnable advice(Supplier<R> task, Result<R> result) {
        return () -> {
            synchronized (result) {
                result.set(task.get());
            }
        };
    }

    public MyFeature(Supplier<R> task) {
        this.result = new Result<>();
        thread = new Thread(advice(task, this.result));
    }

    public MyFeature(Supplier<R> task, String name) {
        this.result = new Result<>();
        thread = new Thread(advice(task, this.result), name);
    }

    public void start() {
        thread.start();
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    public R getResult() throws InterruptedException {
        this.join();
        return result.get();
    }

    public void listenResult(Consumer<R> consumer) throws InterruptedException {
        MyFeature<R> rMyFeature = this;
        new Thread(() -> {
            try {
                rMyFeature.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            R r = result.get();
            consumer.accept(r);
        }).start();
    }
}
```

### 超时

```java
taskThread.join(3000); // 3s
if (taskThread.isAlive()) {
	// 如果线程仍在运行, 超时
	System.out.println("Task timeout, interrupting...");
	taskThread.interrupt(); // 中断任务
}
```

## 异步使用Demo

```java
public static void demo() {
    serialDemo(new MyFeature<>(() -> {
        try {
            System.out.println("exec");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 12;
    }));
    System.out.println("-----------------------");
    syncDemo(new MyFeature<>(() -> {
        try {
            System.out.println("exec");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 12;
    }));
}

private static void serialDemo(MyFeature<Integer> integerMyFeature) {
    // 串行执行
    integerMyFeature.start();
    System.out.println("before get");
    try {
        System.out.println("result = " + integerMyFeature.getResult());
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    System.out.println("after get");
}

private static void syncDemo(MyFeature<Integer> integerMyFeature) {
    // 异步执行
    integerMyFeature.start();
    System.out.println("before get");
    try {
        integerMyFeature.listenResult(r -> {
            System.out.println("before true get");
            System.out.println("r = " + r);
            System.out.println("after true get");
        });
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    System.out.println("after get");
}
```

