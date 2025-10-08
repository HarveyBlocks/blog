# 多锁

## 需求

如果一个资源有两个完全独立的功能, 互不相干

如果这个资源因为需要使用其中一个功能而被锁住, 仅需要使用另一个功能的线程也要去竞争锁, 反而降低了效率

```java
private static class Source {
    public synchronized void task1() {
        log.debug("task1");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized void task2() {
        log.debug("task2");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

public static void demo() {
    Source source = new Source();
    new Thread(source::task1).start();
    new Thread(source::task2).start();
}
```

## 解决

降低锁的粒度

保证没有关联的资源被细分

```java
private static class Source {
    private final Object task1Lock = new Object();
    private final Object task2Lock = new Object();

    public void task1() {
        synchronized (task1Lock) {
            log.debug("task1");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public synchronized void task2() {
        synchronized (task2Lock) {
            log.debug("task2");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

public static void demo() {
    Source source = new Source();
    new Thread(source::task1).start();
    new Thread(source::task2).start();
}
```

可能产生死锁

