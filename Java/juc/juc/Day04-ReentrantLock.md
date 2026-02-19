# ReentrantLock

>   可重入锁

用于解决饥饿和死锁

## 特点

-   可重入
    -   当前线程已经获取了这个锁之后, 如果这个线程再次尝试获取同一把锁, 还能成功, 称为可重入
-   可中断
-   可以设置超时时间
-   可以设置为公平锁
    -   先到先得
-   支持多个等待的条件变量Condition
    -   不同的条件可以在不同的地方等待
    -   notify不会全部唤醒, 只唤醒一部分

## 语法

```java
final ReentrantLock lock = new ReentrantLock(); // 默认不公平锁
new Thread(() -> {
    while (true) {
        lock.lock();
        try {
            log.debug("代码逻辑");
            sleep(1);
        } finally {
            lock.unlock();
        }
    }
}, "t1").start();
new Thread(() -> {
    while (true) {
        lock.lock();
        try {
            log.debug("代码逻辑");
            sleep(1);
        } finally {
            lock.unlock();
        }
    }
}, "t2").start();
```

```java
private static void sync(ReentrantLock lock, Runnable target) {
    lock.lock();
    try {
        target.run();
    } finally {
        lock.unlock();
    }
}
```

## 上锁

调用AQS的sync

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) // 加锁成功, 直接进入语句
        &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) // 加锁失败创建节点对象, 加入等待队列
        selfInterrupt();
}
```

### 成功上锁

调用非公平锁的方法

```java
protected final boolean tryAcquire(int acquires) {
    return nonfairTryAcquire(acquires);
}
```

然后转到自己的父类Sync的`nonfairTryAcquire`方法中

```java
@ReservedStackAccess
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            // 加锁成功
            setExclusiveOwnerThread(current);
            return true;
        }
    } else {
        // 锁重入相关
        // 暂略
        return true;
    }
    return false;
}
```

### 上锁失败

AQS方法

#### 创建等待节点

1.  构造Node的WaitStatus状态, 其中0为默认正常的状态
2.  Node的创建是懒惰的
3.  第一个Node称为Dummy(哑元)或哨兵, 用来占位, 并不关联线程

```java
private Node addWaiter(Node mode) {
    Node node = new Node(mode);

    for (;;) {
        Node oldTail = tail;
        if (oldTail != null) {
            // 双向链表
            // 此时新NOde属于方法, 不被共享
            // oldTail在队列中属于共享资源, 注意线程安全
            // 设置前驱
            node.setPrevRelaxed(oldTail);
            // CAS设置oldNode的后驱
            if (compareAndSetTail(oldTail, node)) {
                // 成功, 设置新Node后驱
                oldTail.next = node;
                // 新节点加入到了队列中
                return node;
            }
        } else {
            // 懒加载哑元
            initializeSyncQueue();
        }
        // 失败重新设置队列关系, 直到节点创建完毕
    }
}
```

#### 再次重试然后进入阻塞

还会重试多次(每走一步都会先重试一波), 具体见下

```java
final boolean acquireQueued(final Node node, int arg) {
    boolean interrupted = false;
    try {
        for (;;) {
            // 获取前驱节点
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                // 属于逻辑上(忽略哨兵)的第一个节点
                // 也就是说, 是一个第一顺位的候选唤醒线程

                // 用tryAcquire尝试成功了
                setHead(node);
                p.next = null; // help GC
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node)){
                // 判断是否在重试获取锁失败后park等待
                // park等待
             	interrupted |= parkAndCheckInterrupt()   ;
                // 是否在park等待的时候被打断? 记录这个打断的标记
                // 注意, 这是一个不可打断锁, 故这里只是标注打断标记, 而不是抛出异常
            }
        }
    } catch (Throwable t) {
        cancelAcquire(node);
        if (interrupted)
            selfInterrupt();
        throw t;
    }
}
```

```java
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    int ws = pred.waitStatus;
    //  Node.SIGNAL标识: 有该标识的节点有责任唤醒其后面的节点
    if (ws == Node.SIGNAL)// SIGNAL-1
        // 已经通过标识, 表示需要park
        return true;
    if (ws > 0) {
        // 前驱是CANCELLED状态(=0)了, 跳过前驱(删除)并向更前驱重试
        do {
            node.prev = pred = pred.prev;
        } while (pred.waitStatus > 0);
        pred.next = node;
    } else {
        // waitStatus 必须为 0 或 PROPAGATE。 PROPAGATE -3
        // 指示我们需要信号，
        // 暂时不park。调用方重试以确保在park前Acquire依然失败
        pred.compareAndSetWaitStatus(ws, Node.SIGNAL);
    }
    return false;
}
```

## 等待状态

等待状态不同于锁状态, 在ReentrantLock中的锁状态是用于实现可重入的

```java
/**
 * 没有任务
 */
static final int CANCELLED =  1;
/**
 * 有责任唤醒其后的节点
 */
static final int SIGNAL    = -1;
/**
 * 有CONDITION的Waiting状态
 */
static final int CONDITION = -2;
/**
 * 表示下一个节点是没有CONDITION的Waiting的Propagate(?)
 * doReleaseShared相关
 */
static final int PROPAGATE = -3;
```

## 释放锁

线程一释放锁之后, 开始竞争锁资源

```java
public void unlock() {
    sync.release(1);
}
```

转到AQS方法

```java
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0){
            // 头节点之后是否有节点, 头节点是否有责任唤醒该节点
            // 唤醒该节点
            unparkSuccessor(h);
        }
        return true;
    }
    return false;
}
```

sync实现

```java
@ReservedStackAccess
protected final boolean tryRelease(int releases) {
    // getState()用于标注锁重入
    int c = getState() - releases; // 减去一层锁
    if (Thread.currentThread() != getExclusiveOwnerThread()){
        // 这种一般是没有上锁就调用了释放锁的缘故
        throw new IllegalMonitorStateException();
    }
    boolean free = false; // free = c==0;
    // 用于标注可重入
    if (c == 0) {
        free = true;
        // 将Owner设置为null
        setExclusiveOwnerThread(null);
    } // 没进入该分支表示锁重入的几层锁还没有轮完
    // 设置锁状态(锁的层数)转变
    setState(c);
    return free;
}
```

### 唤醒后继

```java
private void unparkSuccessor(Node node) { 
    // node是负责唤醒的节点, 其后继是需要被唤醒的节点
    // Successor 后继

    // 尝试在signal之前clear状态至0
    // 如果失败，也没关系
    int ws = node.waitStatus;
    if (ws < 0){
        node.compareAndSetWaitStatus(ws, 0);
    }
    // 获取到后继, 就是需要被唤醒的节点
    // 如果为null或waitStatus不对劲, 把s置为null
    // 则从 tail 向前遍历以查找实际的, 还状态正常(<=0)的后继路由
    Node s = node.next;
    if (s == null || s.waitStatus > 0) {
        s = null;
        for (Node p = tail; p != node && p != null; p = p.prev)
            if (p.waitStatus <= 0) // 尽可能地往前找节点, 越往前的唤醒的优先级越高
                s = p;
    }
    if (s != null){
        // 如果有正常的线程, 就唤醒它
        LockSupport.unpark(s.thread);
    }
}
```

被唤醒的节点需要和那些抢夺锁的线程竞争锁

### 竞争

线程被唤醒后, 从原来被park的地方醒来: 

```java
final boolean acquireQueued(final Node node, int arg) {
    boolean interrupted = false;
    try {
        for (;;) {
            // 获取前驱节点
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                // 用tryAcquire尝试成功了
                setHead(node);
                p.next = null; // 提醒 GC 回收
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node)){
                // park等待
             	interrupted |= parkAndCheckInterrupt();
                // 从这里醒来, 醒来后再次循环并尝试获取锁
            }
        }
    } catch (Throwable t) {
        cancelAcquire(node);
        if (interrupted)
            selfInterrupt();
        throw t;
    }
}
```

## 可重入性

### 使用

```java
new Thread(() -> {
    sync(lock, () -> {
        log.debug("1");
        sync(lock, () -> {
            log.debug("2");
            sync(lock, () -> {
                log.debug("3");
            });
        });
    });
}, "t").start();
```

```log
23:11:18.558 [t] DEBUG org.harvey.juc.demo.ReentrantLockDemo -- 1
23:11:18.561 [t] DEBUG org.harvey.juc.demo.ReentrantLockDemo -- 2
23:11:18.561 [t] DEBUG org.harvey.juc.demo.ReentrantLockDemo -- 3
```

### 原理

sync方法

#### 上锁

```java
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState(); // 用来标记是第几层锁重入
    if (c == 0) {
        // 正常第一次获取锁, 见上, 此处略
    } else if (current == getExclusiveOwnerThread()) {
        // 当前线程和Owner线程是同一个线程, 这就是锁重入
        int nextc = c + acquires; // 增加一层锁重入
        if (nextc < 0) // 溢出了(难道有21亿层锁?!)
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}
```

#### 释放锁

```java
@ReservedStackAccess
protected final boolean tryRelease(int releases) {
    // getState()用于标注锁重入
    int c = getState() - releases; // 减去一层锁
    if (Thread.currentThread() != getExclusiveOwnerThread()){
        // 这种一般是没有上锁就调用了释放锁的缘故
        throw new IllegalMonitorStateException();
    }
    boolean free = false; // free = c==0;
    // 用于标注可重入, 是不是真的每一层的锁都被释放掉了?
    if (c == 0) {
        free = true;
        // 将Owner设置为null
        setExclusiveOwnerThread(null);
    } // 没进入该分支表示锁重入的几层锁还没有轮完
    // 设置锁状态(锁的层数)转变
    setState(c);
    return free;
}
```

## 可中断

### 使用

```java
private static void syncInterruptibily(ReentrantLock lock, Runnable target) throws InterruptedException {
    lock.lockInterruptibly();
    // 设置可以被其他线程的interrupt打断
    try {
        target.run();
    } finally {
        lock.unlock();
    }
}
```

```java
Thread thread = new Thread(() -> {
    try {
        syncInterruptibily(lock1, () -> {
            log.debug("获取到了lock1");
        });
    } catch (InterruptedException e) {
        log.warn("lock1被其他线程打断啦");
    }
}, "thread");
thread.start();
thread.start();
sleep(2);
thread.interrupt();
```

### 原理

#### 可打断锁

用可打断锁去上锁

```java
public void lockInterruptibly() throws InterruptedException {
    sync.acquireInterruptibly(1);
}
```

```java
public final void acquireInterruptibly(int arg)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    if (!tryAcquire(arg))
        doAcquireInterruptibly(arg);
}
```

不能够获取到锁, 执行可打断的锁

```java
private void doAcquireInterruptibly(int arg)
    throws InterruptedException {
    final Node node = addWaiter(Node.EXCLUSIVE);
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                return;
            }
            // 特别的
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                // 特别的
                // 一有打断就抛出异常
                throw new InterruptedException();
        }
    } catch (Throwable t) {
        cancelAcquire(node);
        throw t;
    }
}
```

```java
private final boolean parkAndCheckInterrupt() {
    // park当前线程
    LockSupport.park(this);
    // 如果park期间没有被打断, 没啥, Thread.interrupted()返回打断标记为false
    // 如果被打断了, Thread.interrupted()返回打断标记true, 然后将打断标记置为true
    // Thread.interrupted()将打断标记置为true了, 保证了下一次park还能park得住
    return Thread.interrupted();
}
```

#### 不可中断锁

```java
final boolean acquireQueued(final Node node, int arg) {
    boolean interrupted = false;
    try {
        for (;;) {
            // 重试获取锁,此处略
            if (shouldParkAfterFailedAcquire(p, node)){
                // 判断是否在重试获取锁失败后park等待
                // park等待
                interrupted |= parkAndCheckInterrupt()   ;
                // 是否在park等待的时候被打断? 记录这个打断的标记
                // 依然有机会重试获取锁
            }
        }
    } catch (Throwable t) {
        // 此线程在等待/重试获取锁的过程中发生了异常(除打断异常外的异常)
        cancelAcquire(node);
        // 检查是否被打断过
        if (interrupted) {
            // 如果标记了interrupted为true, 表示在循环之中发生过打断了
            // 此时优先抛出打断异常
            selfInterrupt();
    	}
        throw t;
    }
}
```

## 锁超时

```java
boolean b = lock.tryLock();
int retry = 3;
while (!b && retry-- >0) {
    b = lock.tryLock(20, TimeUnit.MILLISECONDS);
}
if(!b){
    return;
}
try {
	target.run();
} finally {
	lock.unlock();
}
```

锁超时也可以被打断

## 解决死锁

```java
private static void syncFixDeath(ReentrantLock lock1, ReentrantLock lock2, Runnable target) throws InterruptedException {
    while (true) {
        if (!lock1.tryLock(20, TimeUnit.MILLISECONDS)) {
            continue;
        }
        try {
            if (!lock2.tryLock(20, TimeUnit.MILLISECONDS)) {
                continue;
            }
            try {
                target.run();
                break;
            } finally {
                lock2.unlock();
            }
        } finally {
            // lock2无论有没有获取到, 都要释放lock1
        	// lock2获取到了, 执行target, 然后释放lock2, 释放lock1
        	// lock2没获取到, 也释放lock1, 也不要占有
            lock1.unlock();
        }
    }
}
```

### 解决哲学家就餐

```java
@Getter
@AllArgsConstructor
private static class Fork extends ReentrantLock {
    public final String name;
}
```

(Fork继承ReentrantLock类)

```
Thread[Philosopher1,5,]-184
Thread[Philosopher2,5,]-232
Thread[Philosopher3,5,]-188
Thread[Philosopher4,5,]-206
Thread[Philosopher5,5,]-190
tryFirstFailCountSum = 6507
trySecondFailCountSum = 4237
```

如果除去随机等待时间间隔

```log
Thread[Philosopher1,5,]-60
Thread[Philosopher2,5,]-28
Thread[Philosopher3,5,]-22
Thread[Philosopher4,5,]-53
Thread[Philosopher5,5,]-841
tryFirstFailCountSum = 0
trySecondFailCountSum = 129
```

```log
Thread[Philosopher1,5,]-94
Thread[Philosopher2,5,]-112
Thread[Philosopher3,5,]-622
Thread[Philosopher4,5,]-92
Thread[Philosopher5,5,]-85
tryFirstFailCountSum = 0
trySecondFailCountSum = 124
```

```log
Thread[Philosopher1,5,]-70
Thread[Philosopher2,5,]-736
Thread[Philosopher3,5,]-111
Thread[Philosopher4,5,]-84
Thread[Philosopher5,5,]-3
tryFirstFailCountSum = 0
trySecondFailCountSum = 114
```

## 公平锁

但是公平锁会降低并发度

```java
final ReentrantLock lock = new ReentrantLock(true); // 公平锁
```

同样是哲学家就餐, Fork的fair置为true

```java
@Getter
private static class Fork extends ReentrantLock {
    public final String name;

    public Fork(String name) {
        super(true);
        this.name = name;
    }
}
```

```lock
Thread[Philosopher1,5,]-200
Thread[Philosopher2,5,]-192
Thread[Philosopher3,5,]-223
Thread[Philosopher4,5,]-195
Thread[Philosopher5,5,]-194
tryFirstFailCountSum = 0
trySecondFailCountSum = 18
```

### 非公平锁原理

```java
@ReservedStackAccess
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        // 对此尝试获取锁的线程直接去竞争
        if (compareAndSetState(0, acquires)) {
            // 成功获取到锁
            setExclusiveOwnerThread(current);
            return true;
        }
    } else if (current == getExclusiveOwnerThread()) {
        // 可重入锁相关, 此处略
    }
    return false;
}
```

### 公平锁原理

```java
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        // !hasQueuedPredecessors() 是公平锁的体现
        // 用于检测AQS, 来看是否有等待的节点也想竞争
        // 如果有等待的节点, 就不会尝试去CAS更新状态, 而是直接失败
        // 否则后来的节点一直上, 前面的节点一直在队列中等, 一直抢不过新来的线程
        if (!hasQueuedPredecessors() &&
            compareAndSetState(0, acquires)) {
            // 成功获取到锁, 同
            setExclusiveOwnerThread(current);
            return true;
        }
    } else if (current == getExclusiveOwnerThread()) {
         // 可重入锁相关, 此处略
    }
    return false;
}
```

sync的方法: 队列中是否存在前驱节点

```java
public final boolean hasQueuedPredecessors() {
    Node h, s;
    if ((h = head) != null) {
        // 有头节点(哑元)
        // 获取哑元的后继(这个后继是优先级最高的节点)
        if ((s = h.next) == null || s.waitStatus > 0) {
            // 哑元没有后继
            // 或这个后继的waitStatus不符合可以被唤醒的状态
            s = null;
            // 从尾遍历队列, 重新找寻可以被唤醒的节点
            for (Node p = tail; p != h && p != null; p = p.prev) {
                if (p.waitStatus <= 0) // 尽可能地往前找节点, 越往前的唤醒的优先级越高
                    s = p;
            }
        }
        // 获取到了等待中的优先级最高的节点
        if (s != null && s.thread != Thread.currentThread()){
            // 且这个节点不是当前线程的节点
            return true;
        }
    }
    return false;
}
```

## Condition

-   不同的条件可以在不同的地方等待
-   notify不会全部唤醒, 只唤醒同属于一个条件的线程

### 语法

```java
Condition condition = lock.newCondition();
condition.await(); 		// 在锁中调用
condition.await(1000);  // 在锁中调用
condition.signal(); 	// 在锁中调用
condition.signalAll(); 	// 在锁中调用
```

### 等待

```java
private static void startNewThread(ReentrantLock lock, Condition condition, String conditionName) {
    new Thread(() -> {
        try {
            lock.lock();
            log.info("进入" + conditionName + "线程");
            condition.await();
            log.info(conditionName + "等待结束");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }).start();
}
```

### 唤醒

```java
private static void signalCondition(ReentrantLock lock, Condition condition, String conditionName) {
    sleep(1);
    try {
        lock.lock();
        log.info("唤醒" + conditionName);
        condition.signal();
    } finally {
        lock.unlock();
    }
}
private static void signalAllCondition(ReentrantLock lock, Condition condition, String conditionName) {;
    sleep(1);
    try {
        lock.lock();
        log.info("唤醒" + conditionName);
        condition.signalAll();
    } finally {
        lock.unlock();
    }
}
```

### Demo

```java
public static void syncCondition(ReentrantLock lock, Runnable target) throws InterruptedException {
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();

    startNewThread(lock, condition1, "condition1");
    startNewThread(lock, condition1, "condition1");
    startNewThread(lock, condition2, "condition2");
    startNewThread(lock, condition1, "condition1");
    startNewThread(lock, condition2, "condition2");
    signalCondition(lock, condition1,"condition1");
    signalAllCondition(lock, condition2,"condition2");
    signalAllCondition(lock, condition1,"condition1");

}
```

### ConditionObejct#await()原理

每个ConditionObejct对象内部维护一个队列, 里面各个节点都是和本ConditionObejct有关的节点

```java
public final void await() throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    Node node = addConditionWaiter(); // 将本线程加入Condition的双向变量(没有空姐点)
    // 并将状态设置为CONDITION(见上等待状态)
    // 释放当前线程占有的锁
    int savedState = fullyRelease(node);  // fullyRelease考虑锁重入
    int interruptMode = 0;
    while (!isOnSyncQueue(node)) {
        LockSupport.park(this);
        // 打断暂略
        if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
            break;
    }
    // 打断的判断
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
        interruptMode = REINTERRUPT;
    // 本节点的下一个节点
    if (node.nextWaiter != null) // clean up if cancelled
        unlinkCancelledWaiters();
    // 报告中途有打断
    if (interruptMode != 0)
        reportInterruptAfterWait(interruptMode);
}
```

释放

```java
final int fullyRelease(Node node) {// AQS的方法, 而不是ConditionObject的了
    try {
        int savedState = getState();
        if (release(savedState))  // 释放n重锁, 并唤醒优先顺位最高的节点
            return savedState;
        throw new IllegalMonitorStateException();
    } catch (Throwable t) {
        node.waitStatus = Node.CANCELLED;
        throw t;
    }
}
```

### ConditionObejct#signalCondition()原理

```java
public final void signal() {
    if (!isHeldExclusively()) // 独家持有
        throw new IllegalMonitorStateException();
    Node first = firstWaiter;
    if (first != null)
        doSignal(first);
}
```

```java
private void doSignal(Node first) {
    do {
        // 从等待队列中删除第一个节点
        if ( (firstWaiter = first.nextWaiter) == null)
            // 如果成为空队列了, 队尾指针也指向null
            lastWaiter = null;
        // 后继置为null(可以防止外部不好的访问)
        first.nextWaiter = null;
    } while (!transferForSignal(first)  // 尝试转移first节点的线程(first的优先级高)到Lock的等待队列
             && // 没有转移成功就继续循环
             (first = firstWaiter) != null );// 遍历队列
}
```

```java
final boolean transferForSignal(Node node) {
    // 如果设置节点状态失败了, 那就说明节点处于CANCELLED状态, 还得往下遍历找到能被唤醒的线程
    if (!node.compareAndSetWaitStatus(Node.CONDITION, 0)) // 转换失败原因有打断和超时, 这种不用被唤醒
        return false;

    // 将node入队(ReentrantLock的等待队列, 和其他线程一起竞争锁(排在最后, 优先级最低))
    Node p = enq(node);
    int ws = p.waitStatus;
    if (ws > 0 || !p.compareAndSetWaitStatus(ws, Node.SIGNAL))
        // 如果等待状态不正常, 也没有成功将等待状态从原来的改成SIGNAL(有责任唤醒其后的线程)
        LockSupport.unpark(node.thread);
    // 此时, 已经将node放入了, 到时候unlock的时候竞争到了, 就被唤醒了
    return true;
}
```

```java
private Node enq(Node node) {
    for (;;) {
        Node oldTail = tail; // ReentrantLock的等待队列的队尾指针
        if (oldTail != null) {
            // 设置node的前驱
            node.setPrevRelaxed(oldTail);
            // 入队
            if (compareAndSetTail(oldTail, node)) {
                // 入队成功
                oldTail.next = node;
                return oldTail;
            }
        } else {
            // 初始化队列, 懒加载
            initializeSyncQueue();
        }
    }
}
```

