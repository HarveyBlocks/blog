# synchronized

## 概念

对象锁

采用互斥让同一时刻至多只有一个线程能持有 *对象锁* 其他线程想获取 *对象锁* 就会被阻塞住

以此保证拥有锁的线程可以安全地执行临界区的代码, 不用担心上下文的切换

-   互斥

    保证临界区的竞态条件发生, 同一时刻只能有一个线程执行临界区代码

-   同步

    线程执行的先后顺序不同, 需要一个线程等待其他线程执行到某个点

## 使用

```java
public static Integer num = 0;

public static void main(String[] args) {
    new Thread(() -> {
        for (int i = 0; i < 100; i++) {
            synchronized (num) {
                num++;
            }
        }
    });
}
```



## synchronized锁方法

加在对象方法上, 锁this对象

加在静态方法上, 锁当前类的字节码对象

```java
public synchronized void a() {

}

public synchronized static void b() {
    
}
```

## 原理

synchronized保证了对象锁内部的区域的 **原子性**

详见[管程](./Day02-管程.md)

