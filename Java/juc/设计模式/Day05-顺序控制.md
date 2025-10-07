# 顺序控制

要求即使在不同线程下, 也要有先后顺序

## 需求

先打印1, 再打印2

## wait-notify

```java
FutureResponse<String> response = new FutureResponse<>();
response.setFinished(false);
new Thread(() -> {
    synchronized (response) {
        while (!response.isFinished()) {
            try {
                log.debug("未输出1, 等待");
                response.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("2");
    }
}).start();
sleep(1);
new Thread(() -> {
    synchronized (response) {
        log.debug("1");
        response.setFinished(true);
        response.notify();
    }
}).start();
```

## ReentryLock

```java
ReentrantLock lock = new ReentrantLock();
// 等待输出日志1
Condition waitForLog1 = lock.newCondition();
new Thread(() -> {
    sync(lock, () -> {
        try {
            waitForLog1.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.debug("2");
    });
}).start();
sleep(1);
new Thread(() -> {
    sync(lock, () -> {
        log.debug("1");
        waitForLog1.signal();
    });
}).start();
```

## park#unpark

```java
Thread waitForLog1 = new Thread(() -> {
    LockSupport.park();
    log.debug("2");
});
thread.start();
sleep(1);
new Thread(() -> {
    log.debug("1");
    LockSupport.unpark(waitForLog1);
}).start();
```