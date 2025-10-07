# CyclicBarrier

>   循环栅栏
>
>   等待线程满足计数, 构造时设置*计数个数*, 每个线程执行到某个要 *同步* 的时刻, 调用await()方法进行等待
>
>   当等待线程数满足 *计数个数*的时候, 继续执行

和CountdownLatch基本没区别

唯一的区别在于, 可以**重置(*reset*)**计数, 不用反复创建对象

## 使用

```java
ExecutorService service = Executors.newFixedThreadPool(4);
CyclicBarrier barrier = new CyclicBarrier(2,()->{
    log.debug("所有线程完成任务, 汇总");
});
Runnable target = () -> {
    sleep(RANDOM.nextInt(1000) / 2000.0);
    try {
        log.debug("完成");
        barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
        throw new RuntimeException(e);
    }
    log.debug("退出线程");
};
service.submit(target);
service.submit(target);
service.submit(target);
service.submit(target);
```

```
01:01:00.818 [pool-1-thread-1] DEBUG org.harvey.juc.Main -- 完成
01:01:00.827 [pool-1-thread-3] DEBUG org.harvey.juc.Main -- 完成
01:01:00.827 [pool-1-thread-3] DEBUG org.harvey.juc.Main -- 所有线程完成任务, 汇总
01:01:00.827 [pool-1-thread-1] DEBUG org.harvey.juc.Main -- 退出线程
01:01:00.827 [pool-1-thread-3] DEBUG org.harvey.juc.Main -- 退出线程
01:01:00.887 [pool-1-thread-2] DEBUG org.harvey.juc.Main -- 完成
01:01:01.247 [pool-1-thread-4] DEBUG org.harvey.juc.Main -- 完成
01:01:01.247 [pool-1-thread-4] DEBUG org.harvey.juc.Main -- 所有线程完成任务, 汇总
01:01:01.247 [pool-1-thread-4] DEBUG org.harvey.juc.Main -- 退出线程
01:01:01.247 [pool-1-thread-2] DEBUG org.harvey.juc.Main -- 退出线程
```

汇总两次

