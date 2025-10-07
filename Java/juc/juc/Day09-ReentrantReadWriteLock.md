# ReenctrantReadWriteLock

读写锁

## 需求

对于读操作, 可以并发提高效率, 当读操作远多于写操作时, 更有必要

读-读并发

读-写, 写-写互斥

## 使用

### 可读可写的资源

```java
@AllArgsConstructor
static class Source {
    private int data;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static <T> T sync(Lock lock, Supplier<T> target) {
        lock.lock();
        try {
            return target.get();
        } finally {
            lock.unlock();
        }
    }

    private static void sync(Lock lock, Runnable target) {
        sync(lock, (Supplier<Void>) () -> {
            target.run();
            return null;
        });
    }

    public int read() {
        return sync(lock.readLock(), () -> {
            log.debug("start read");
            sleep(1);
            log.debug("end read, value={}", data);
            return data;
        });
    }

    public void write(int newValue) {
        sync(lock.writeLock(), () -> {
            log.debug("start write, old={}", data);
            sleep(2);
            data = newValue;
            log.debug("end write, new{}", data);
        });
    }

}
```

### 线程运行和阻塞关系

```java
public static void demo() {
    Source source = new Source(RANDOM.nextInt(10));
    try {
        log.debug("read->read");
        exec(getReadTarget(source), getReadTarget(source));
        System.out.println();
        log.debug("read->write");
        exec(getReadTarget(source), getWriteTarget(source));
        System.out.println();
        log.debug("write->read");
        exec(getWriteTarget(source), getReadTarget(source));
        System.out.println();
        log.debug("write->write");
        exec(getWriteTarget(source), getWriteTarget(source));
        System.out.println();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}

private static Runnable getReadTarget(Source source) {
    return source::read;
}

private static Runnable getWriteTarget(Source source) {
    return () -> {
        source.write(RANDOM.nextInt(10));
    };
}

private static void exec(Runnable firstTarget, Runnable secondTarget) throws InterruptedException {
    Thread thread1 = new Thread(firstTarget);
    Thread thread2 = new Thread(secondTarget);
    thread1.start();
    sleep(0.1);
    thread2.start();
    thread1.join();
    thread2.join();
}
```

### 测试日志

```
17:48:04.186 [main] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- read->read
17:48:04.190 [Thread-0] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- start read
17:48:04.290 [Thread-1] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- start read
17:48:05.203 [Thread-0] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- end read, value=2
17:48:05.295 [Thread-1] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- end read, value=2

17:48:05.296 [main] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- read->write
17:48:05.297 [Thread-2] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- start read
17:48:06.298 [Thread-2] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- end read, value=2
17:48:06.299 [Thread-3] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- start write, old=2
17:48:08.314 [Thread-3] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- end write, new4

17:48:08.315 [main] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- write->read
17:48:08.317 [Thread-4] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- start write, old=4
17:48:10.326 [Thread-4] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- end write, new4
17:48:10.326 [Thread-5] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- start read
17:48:11.341 [Thread-5] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- end read, value=4

17:48:11.342 [main] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- write->write
17:48:11.343 [Thread-6] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- start write, old=4
17:48:13.346 [Thread-6] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- end write, new5
17:48:13.346 [Thread-7] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- start write, old=5
17:48:15.348 [Thread-7] DEBUG org.harvey.juc.juc.ReadWriteLockDemo -- end write, new2
```

## 使用注意

-   读锁不支持Condition

-   写锁支持Condition

-   不支持重入升级: 获取读锁之后重入写锁

-   支持重入降级: 获取写锁之后重入读锁(用处: 在更新后马上获取新的数据)

    ```java
    public void writeAndGet(int newValue) {
        sync(lock.writeLock(), () -> {
            log.debug("start write, old={}", data);
            sleep(2);
            data = newValue;
            log.debug("end write, new{}", data);
            lock.readLock().lock(); // 在写锁没有释放时加读锁
            // 此时一定能获取到读锁
            // 防止其他线程干扰(有其他线程先占据写锁, 重新写入值, 而导致本次修改的值被覆盖)
        });
        try {
            log.debug("new value is: {}", data);
        } finally {
            lock.readLock().unlock();
        }
    }
    ```

    addWaiter的时候, 使用的是加入SHARED节点

##读写锁实现缓存

-   缓存-持久化数据不一致(写操作时, 先情况删除缓存的数据, 再写到数据库中去)

-   读写不一致(脏读, 可重复读等)

    ```mermaid
    sequenceDiagram
    
    participant r as 读线程
    participant w as 写线程
    participant c as 缓存
    participant db as 数据库
    
    w->>c : 清理数据
    r-x c : 读取失败
    r->> db : 读取数据
    w->>db : 更新数据
    r->> c : 写入缓存(旧数据!)
    
    
    
    ```

    加锁

    ```mermaid
    sequenceDiagram
    
    participant r as 读线程
    participant w as 写线程
    participant c as 缓存
    participant db as 数据库
    
    w->>+ c : 清理数据
    w->>-db : 更新数据
    r-x c : 读取失败
    r->> db : 读取数据
    r->> c : 写入缓存(新数据!)
    ```

-   容量

-   缓存过期

-   分布式(通讯)

-   细分锁(各表的锁)

## 原理

用 *A(bstract)Q(ueued)S(ynchronizer)*

ReentrantReadWriteLock, 其state低16位给写锁, 高16位给读锁

