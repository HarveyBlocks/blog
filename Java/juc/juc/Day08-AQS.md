# AQS

>   Abstract Queued Synchronizer

阻塞所和相关的同步器工具的框架/抽象父类

![image-20240913184445815](../assetss/Day08-AQS/image-20240913184445815.png)

## 特点

-   State属性标识资源状态
    -   子类维护State属性, 控制如何获取锁和释放锁
    -   可用**乐观锁**对State进行更新操作
    -   独占模式: 只有一个线程能够访问资源
    -   共享模式: 允许多个线程访问资源
-   基于FIFO的等待队列
    -   类似于Monitor的EntryList
-   Condition
    -   实现等待, 唤醒机制
    -   支持多个Condition
    -   AQS有内部类ConditionObject
    -   类似Monitor的WaitSet

## 方法规范

不实现方法就抛出异常`UnsupportedOperationException`

```java
/**
 * @return true if 成功获取锁
 */
protected abstract boolean tryAcquire(int arg);
```

```java
/**
 * @return true if 成功释放锁
 */
protected abstract boolean tryRelease(int arg);
```

```java
protected abstract int tryAcquireShared(int arg);
```

```java
protected abstract boolean tryReleaseShared(int arg);
```

```java
/**
 * @return true if 持有独占锁
 */
protected abstract boolean isHeldExclusively();
```

