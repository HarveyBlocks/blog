# Semaphore

>   信号量, 现在同时访问共享资源的线程上限

基于AQS

用于限制线程数量, 而不是资源数量(例如连接数)

##使用

```java
// 允许三个线程
Semaphore semaphore = new Semaphore(3);
for (int i = 0; i < 5; i++) {
    new Thread(()->{
        try {
            semaphore.acquire(); // 获取许可
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.debug("start");
        sleep(0.5);
        log.debug("end");
        semaphore.release(); // 归还许可
    }).start();
}
```

```log
21:44:18.023 [Thread-4] DEBUG org.harvey.juc.juc.SemaphoreDemo -- start
21:44:18.023 [Thread-1] DEBUG org.harvey.juc.juc.SemaphoreDemo -- start
21:44:18.023 [Thread-0] DEBUG org.harvey.juc.juc.SemaphoreDemo -- start
21:44:18.535 [Thread-4] DEBUG org.harvey.juc.juc.SemaphoreDemo -- end
21:44:18.535 [Thread-0] DEBUG org.harvey.juc.juc.SemaphoreDemo -- end
21:44:18.535 [Thread-1] DEBUG org.harvey.juc.juc.SemaphoreDemo -- end
21:44:18.535 [Thread-2] DEBUG org.harvey.juc.juc.SemaphoreDemo -- start
21:44:18.535 [Thread-3] DEBUG org.harvey.juc.juc.SemaphoreDemo -- start
21:44:19.050 [Thread-3] DEBUG org.harvey.juc.juc.SemaphoreDemo -- end
21:44:19.050 [Thread-2] DEBUG org.harvey.juc.juc.SemaphoreDemo -- end
```
