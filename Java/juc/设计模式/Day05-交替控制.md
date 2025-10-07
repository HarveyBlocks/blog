# 交替控制

##1->2

```java
 private enum LogCondition {
    TIME_TO_LOG_1,
    TIME_TO_LOG_2
}

private static void alternateControl() {
    FutureResponse<LogCondition> response = new FutureResponse<>();
    // 规定一开始输出谁
    response.setResult(LogCondition.TIME_TO_LOG_1);
    new Thread(new AlwaysLoopWarp(() -> {
        synchronized (response) {
            while (!(response.getResult() == LogCondition.TIME_TO_LOG_1)) {
                try {
                    log.debug("未输出2, 等待");
                    response.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.debug("1");
            sleep(0.2);
            response.setResult(LogCondition.TIME_TO_LOG_2);
            response.notify();
        }
    })).start();
    new Thread(new AlwaysLoopWarp(() -> {
        synchronized (response) {
            while (!(response.getResult() == LogCondition.TIME_TO_LOG_2)) {
                try {
                    log.debug("未输出1, 等待");
                    response.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.debug("2");
            sleep(0.2);
            response.setResult(LogCondition.TIME_TO_LOG_1);
            response.notify();
        }
    })).start();
}
```

由于只有俩线程, notify()的结果是可以被预见的

```java
Object lock = new Object();
new Thread(new AlwaysLoopWarp(() -> {
    synchronized (lock) {
        log.debug("1");
        sleep(0.5);
        lock.notify();
        try {
            lock.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
})).start();
new Thread(new AlwaysLoopWarp(() -> {
    synchronized (lock) {
        log.debug("2");
        sleep(0.5);
        lock.notify();
        try {
            lock.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
})).start();
```

## 1->2->3

### wait-notify



```java
private enum LogCondition {
    TIME_TO_LOG_1,
    TIME_TO_LOG_2,
    TIME_TO_LOG_3,
}

private static void alternateControl() {
    FutureResponse<LogCondition> response = new FutureResponse<>();
    // 规定一开始输出谁
    response.setResult(LogCondition.TIME_TO_LOG_1);
    new Thread(loopPrintTarget(response, LogCondition.TIME_TO_LOG_1, LogCondition.TIME_TO_LOG_2, "1")).start();
    new Thread(loopPrintTarget(response, LogCondition.TIME_TO_LOG_2, LogCondition.TIME_TO_LOG_3, "2")).start();
    new Thread(loopPrintTarget(response, LogCondition.TIME_TO_LOG_3, LogCondition.TIME_TO_LOG_1, "3")).start();
}

private static Runnable loopPrintTarget(FutureResponse<LogCondition> response,
                                        LogCondition thisCondition, LogCondition nextCondition,
                                        String msg) {
    return new AlwaysLoopWarp(() -> {
        synchronized (response) {
            while (!(response.getResult() == thisCondition)) {
                try {
                    log.debug("未到输出 " + msg + " 之时, 等待");
                    response.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info(msg);
            sleep(0.2);
            response.setResult(nextCondition);
            response.notifyAll();
        }
    });
}
```

###park-unpark

```java
private static Thread thread1 = null;
private static Thread thread2 = null;
private static Thread thread3 = null;

private static void alternateControl() {
    Object lock = new Object();
    // 规定一开始输出谁

    thread1 = new Thread(new AlwaysLoopWarp(() -> {
        LockSupport.park();
        log.info("1");
        sleep(0.2);
        LockSupport.unpark(thread2);
    }));
    thread1.start();
    thread2 = new Thread(new AlwaysLoopWarp(() -> {
        LockSupport.park();
        log.info("2");
        sleep(0.2);
        LockSupport.unpark(thread3);
    }));
    thread2.start();
    thread3 = new Thread(new AlwaysLoopWarp(() -> {
        LockSupport.park();
        log.info("3");
        sleep(0.2);
        LockSupport.unpark(thread1);
    }));
    thread3.start();
    LockSupport.unpark(thread1);
}
```