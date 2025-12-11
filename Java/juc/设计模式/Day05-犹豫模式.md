# 犹豫模式

>   Balking 犹豫

有些代码, 或为了正确性, 或为了效率, 只能被执行一次. 

犹豫模式用于保证在多线程的情况下, 一段代码只被执行一次

## 实现

```java
public class Balking implements Runnable {
    private final Runnable singleExecuteTarget;
    private boolean executed;

    public Balking(Runnable singleExecuteTarget) {
        this.singleExecuteTarget = singleExecuteTarget;
        this.executed = false;
    }

    @Override
    public void run() {
        if (executed) {
            // 绝大多数都从这里出去, 减少synchronized而产生的资源消耗
            return;
        }
        synchronized (this) {
            if (executed) {
                return;
            }
            executed = true;
        }
        singleExecuteTarget.run();
    }

    /**
     * @param singleExecuteTarget 只被执行一次的代码
     */
    public static Runnable balking(Runnable singleExecuteTarget) {
        return new Balking(singleExecuteTarget);
    }

}
```



### 错误

```java
public void run() {
    if (executed) {
        return;
    }
    synchronized (this) {
        if (executed) {
            return;
        }
        executed = true; // 运行到这一行时, executed = true
        // 此时, 突然, 有线程进入该方法, 马上去判断第一个executed, 为true马上返回
        // 然后哪个马上返回的线程一直都会认为确实已经执行了, 但是, 真的确实执行了吗? 没有
    }
    singleExecuteTarget.run();
}
```

优化

```java
public void run() {
    if (executed) {
        return;
    }
    synchronized (this) {
        if (executed) {
            return;
        } 
    	singleExecuteTarget.run();
        executed = true;
    }
}
```

对可见性和有序性的考虑见[volatile](../内存模型/Day05-volatile.md)

### 最终修正

考虑之后的最终修正

```java
public class Balking implements Runnable {
    private final Runnable singleExecuteTarget;
    private volatile boolean executed;

    public Balking(Runnable singleExecuteTarget) {
        this.singleExecuteTarget = singleExecuteTarget;
        this.executed = false;
    }

    @Override
    public void run() {
        // 读到的数据是最新的
        if (executed) {//读屏障之后的操作不会被重排序
            return;
        }
        synchronized (this) {
            if (executed) {
                return;
            }
            singleExecuteTarget.run();
            executed = true; // 写屏障之前的操作不会被重排序
            // 总是会加载到主内存里去
        }
    }

    /**
     * @param singleExecuteTarget 只被执行一次的代码
     */
    public static Runnable balking(Runnable singleExecuteTarget) {
        return new Balking(singleExecuteTarget);
    }

}
```

## 用犹豫模式懒加载单例

### 单例

```java
// final修饰本类, 保证本类不会在子类中被实力化
public final class BalkingSingleton implements Serializable{ 
    // 继承Serializable, 即使程序反序列化对象, 也保证单例
    public Object readResolve(){
        return BalkingSingleton.instance();
    } 
    private static BalkingSingleton singleton;
    private static final Balking BALKING;

    static {
        BALKING = new Balking(() -> {
            singleton = new BalkingSingleton();
        });
    }
	// 反射是防不住的, 想防反射用枚举(枚举也防序列化, 反序列化)
    private BalkingSingleton() {
        // 测试输出
        // System.out.println("NEW");
    }

    public static BalkingSingleton instance() {
        BALKING.run();
        return singleton;
    }
    
    public void print(){
        System.out.println("这是一个单例类:"+this.hashCode());
    }
}
```

用内部类, JVM保证线程安全

```java
// final修饰本类, 保证本类不会在子类中被实力化
public final class InnterClassSingleton{ 
	private static class LazyHolder{
        // 内部类是懒加载的
        static finnal InnterClassSingleton = new InnterClassSingleton();
    }
    public static InnterClassSingleton instance() {
        return LazyHolder.InnterClassSingleton;
    }
}
```



### 测试

```java
List<Thread> threads = new ArrayList<>();
for (int i = 0; i < 200; i++) {
    Thread thread = new Thread(() -> {
        LockSupport.park();
        BalkingSingleton instance = BalkingSingleton.instance();
    }, "t" + i);
    threads.add(thread);
    thread.start();
}
sleep(1);
for (Thread thread : threads) {
    LockSupport.unpark(thread);
}
```

