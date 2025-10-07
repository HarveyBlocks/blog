#StampedLock

在读锁, 写锁, 配合Stamp(戳)来使用

## 乐观读

1.  获取一个戳
2.  验戳
    -   戳已经旧了, 戳已经被升级了, 升级成读锁

```java
StampedLock lock = new StampedLock();
long stamp = lock.tryOptimisticRead(); // 获取戳
if (lock.validate(stamp)) {// 验戳
    // 戳依旧可使用
    // 执行代码逻辑
} else {
    // 锁升级成读锁
}
```

### 戳生成

```java
private static final int LG_READERS = 7;
private static final long RUNIT = 1L;                   // 0... 0000_0000 0000_0001
private static final long WBIT = 1L << LG_READERS;      // 0... 0000_0000 1000_0000
private static final long RBITS = WBIT - 1L;            // 0... 0000_0000 0111_1111
private static final long RFULL = RBITS - 1L;           // 0... 0000_0000 0111_1110
private static final long ABITS = RBITS | WBIT;			// 0... 0000_0000 1111_1111
private static final long SBITS = ~RBITS;				// 1... 1111_1111 0000_0000
```

读

```java
public long tryOptimisticRead() {
    long s;
    return (((s = state) & WBIT) == 0L) ? (s & SBITS) : 0L; // 去低8位
} 
```

写

```java
public long writeLock() {
    long next;
    return ((next = tryWriteLock()) != 0L) ? next : acquireWrite(false, 0L);
}
```

```java
public long tryWriteLock() {
    long s;
    return (((s = state) & ABITS) == 0L) ? tryWriteLock(s) : 0L;
}
```

```java
private long tryWriteLock(long s) {
    // assert (s & ABITS) == 0L;
    long next;
    if (casState(s, next = s | WBIT)) {
        VarHandle.storeStoreFence();
        return next;
    }
    return 0L;
}
```

### 验锁

```java
public boolean validate(long stamp) {
    VarHandle.acquireFence();
    return (stamp & SBITS) == (state & SBITS);
}
```

## 使用

```java
StampedLock stampedLock = new StampedLock();
for (int i = 0; i < 100; i++) {
    new Thread(() -> {
        if (RANDOM.nextInt(30)>1){
            long stamp1 = stampedLock.tryOptimisticRead();
            sleep(0.5);
            boolean validate = stampedLock.validate(stamp1);
            if (validate) {
                System.out.println("可以读");
            }else {
                System.out.println("锁升级");
            }
        }else {
            long l = stampedLock.writeLock();
            try{
                System.out.println(l);
            }finally {
                stampedLock.unlockWrite(l);
            }
        }
    }).start();
}
```

## 注意

-   不可重入
-   没有Condition

##Future

Future获取到多个线程中的结果



