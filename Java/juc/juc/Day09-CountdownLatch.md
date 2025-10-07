# CountdownLatch

>   倒计时锁

等待所有线程完成倒计时

用于检测线程不停止(不能用join哩), 但是任务结束了的情况

## 使用

构造函数初始化等待计数值

countDown()让计数减一

await()等待计数归零

```java
CountDownLatch latch = new CountDownLatch(12);
Runnable target = () -> {
    while (latch.getCount() > 0) {
        sleep(RANDOM.nextInt(500) / 1000.0);
        System.out.println(latch.getCount());
        latch.countDown();
    }
};
new Thread(target).start();
new Thread(target).start();
new Thread(target).start();
new Thread(target).start();
log.debug("等待");
try {
    latch.await();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
}
log.debug("完成");
```