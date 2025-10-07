# 原子累加器

如果ActomicInteger来加工成累加器, 效率略低

原子累加器使用得还满频繁的, 确实有必要将原子累加器在Cpp层面实现

-   Stiped64 (抽象)
-   DoubleAccumulator
-   DoubleAdder
-   LongAccumulator
-   LongAdder

## Adder

### 构造

```java
LongAdder adder = new LongAdder();
```

### 累加

```java
adder.increment();
```

### 取值

```java
System.out.println(adder.longValue());
```

### 执行原理

设置多个**累加单元**(个数在一开始的2个, 竞争多时会加到和CPU数量一样多, 再多就将失去意义)

尽管线程很多, 但是它们可供竞争的资源也很多

在写操作时分散线程的竞争, 减少compare时命中失败的概率, 降低循环重试次数

在读取值的时候将结果结合起来(由于没有需求说读取到的一定要是最新数据, 所以在读取时没必要搞线程安全)

效率就升高了

## Striped64原理

```java
abstract class Striped64 extends Number {
    
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    transient volatile Cell[] cells;

    transient volatile long base;
}
```

### base和CAS锁

Striped64底层在累加运算时会使用到CAS锁

CAS本身是无锁的, 用CAS的API做一个锁

```java
public class CasLock {
    private static final boolean LOCKED_STATE = true;
    private static final boolean UNLOCK_STATE = false;
    private final AtomicBoolean state = new AtomicBoolean(UNLOCK_STATE);

    public void lock() {
        //noinspection StatementWithEmptyBody
        do {
            // 空体循环
        } while (!state.compareAndSet(UNLOCK_STATE, LOCKED_STATE));
    }

    public void unlock() {
        state.set(UNLOCK_STATE);
    }
}
```

#### 原理分析

lock方法

1.  线程1先进入判断`compareAndSet`, 成功就将state设置为LOCKED_STATE
2.  线程2后进入, 在其判断`compareAndSet` 时必定失败, 此时, 将会while(true)不断循环, 不能继续执行,相当于阻塞

unlock方法

1.  没有被阻塞的线程, 在调用unlock方法之后, state设置为UNLOCKED_STATE
2.  此时不断循环的线程终于成功`compareAndSet`, 得以正确执行



#### 缺点

对CPU的消耗非常大



####在Striped64的使用

在cells创建或扩容时执行加锁操作



### Cell和CPU缓存行

Cell是Striped64中用于存储计数的单元

```java
@jdk.internal.vm.annotation.Contended
static final class Cell {
    volatile long value;
    
    // VarHandle mechanics
    private static final VarHandle VALUE;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            VALUE = l.findVarHandle(Cell.class, "value", long.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
```

Striped64中用Cell数组存储多个计数单元

####缓存行失效

CPU各核心的高速缓存中有多个缓存行, 一个缓存行64个字节

CPU为了保证数据的一致性, 

如果一个核心更改了一个缓存行中的一个数据

然后这个数据在其他缓存行中的拷贝将全部都失效

这个失效的操作将导致这个数据所在缓存行中的所有数据都将失效

一个CPU的缓存行数据, 将因为别的CPU核心数据失效而整个失效, 不得不从内存中读取数据

####@Contended填充与伪共享

>   Contended 竞争

为了防止自己的数据因为别的核心而失效

*Contended*注解会在对象/字段的空间的前后各加128字节大小的padding, 占据了位子, 就不会被其他核心影响了

以此防止一个缓存行容纳多个对象/字段的方式称为*伪共享*

##源码

LongAdder源码

### `#add(int)`

### `#sum`

很简单, 略

##Accumulator



## 效率对比

>   40线程, 每个线程累加一百万次, 任务循环1000次(JIT优化)

-   ActomicLong 初: 1035ms 末:
-   LongAdder 初 : 153ms 末 : 35ms

| 累加器      | 初(ms) | 末(ms) | 千次时间总计(ms) |
| ----------- | ------ | ------ | ---------------- |
| ActomicLong | 1035   | 930↑↓  | TIMEOUT          |
| LongAdder   | 244    | 31     | 35534            |



###