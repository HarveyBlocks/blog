# 性能调优

## 性能

每次请求必须在5s内响应之类的需求

## 目标

找到CPU占用率高的代码段

## 线程的转储

>   Thread Dump

提供了对所有运行中的线程当前状态的快照

线程转储可以通过jstack, visualvm等工具

可用来解决CPU占用率高, 死锁等问题

### JDK自带

```shell
jstack <PID> > thread_dump.tdump
```

### Visual VM

![image-20240527221844400](../asset/Day11-%E6%80%A7%E8%83%BD%E9%97%AE%E9%A2%98/image-20240527221844400.png)

## 分析线程转储文件

### 构成



-   线程名
-    `prio` 优先级 线程优先级
-    `tid` Java ID JVM中线程的唯一ID
-   `nid` 本地ID 操作系统分配给线程的唯一ID
-   状态
    -   `NEW ` 新创建的线程 尚未开始执行
    -   `RUNNABLE` 正在执行或准备执行, 在Native方法的等待(例如IO等待)时, 不消耗CPU资源, 但JVM认为是`RUNNABLE`状态 
    -   `BlOKED` 等待获取监视器锁以进入/重新进入同步方法块/方法
    -   `WAITING` 等待其他线程执行特定操作, 没有时间限制
    -   `TIED_WAITING` 等待其他线程执行特定操作, 有时间限制
    -   `TERMINATED` 已完成执行
-   栈追踪 显示整个方法的栈帧信息

### 可视化在线平台

[jstack](https://jstack.review/)

[fastthread](https://fastthread.io/)

## 定位高CPU消耗线程

1.  定位高CPU消耗进程

    ```shell
    top -c
    ```

    按照CPU的使用率倒序排序, 获取目标PID

2.  依据PID进入进程

    ```shell
    top -p <PID>
    ```

3.  按下`shift + h` (大写H)

4.  找到线程ID(虽然也是PID, 但是不一样) , 称其为PID2

    ```shell
    jstack PID2 > service.tdump
    ```

    

## 在调用链上定位性能问题出现的方法

使用Arthus命令

### trace

trace 在调用到某方法时, 打印出该方法执行各语句信息, 如果方法里面有方法调用不会方法里面的语句执行显示

```shell
trace 类名 方法名
```

-   `--skipJDKMethod false `  参数输出JDK核心包中的方法及耗时, 默认true
-    `#cost > 毫秒数`  只会显示消耗时间超过的调用
-   `-n 数值` 最多显示多少条数据
-   所有监控结束后, 输入`stop`结束监控, 重置arthas增强的对象, 对性能产生影响



### watch

`watch`可以查看调用时使用参数信息, 定位什么参数导致了性能大量损耗,获取返回值, 获取更详细的方法调用信息

```shell
watch 类名 方法名 '{params}'
```

-    `{params,returnObj} `  
-    `#cost > 毫秒数`  只会显示消耗时间超过的调用
-   `-x` 如果打印结果中有嵌套(比如对象中的属性), 允许最大4层

### 火焰图

Arthus生成性能火焰图, 显示所有方法执行时间的长短

![image-20240528194405081](../asset/Day11-%E6%80%A7%E8%83%BD%E8%B0%83%E4%BC%98/image-20240528194405081.png)

每个方块(本质是栈的一个帧)是一个方法

每个方块的宽度代表其消耗时长

下面的方块调用上面的方块

黄色的是给JVM使用的C++实现的栈区

绿色的是给Java代码使用的栈区



生成火焰图

```shell
profiler start
```

```shell
profiler stop --format html
```

只能在Linux或者MacOs上运行

## 死锁

### 介绍

两个以上的线程争夺同一个资源, 只能无限循环

```java
public static void main(String[] args) throws IOException {
    Object obj1 = new Object();
    Object obj2 = new Object();
    new Thread(createRunnable(obj1, obj2), "thread-1").start();
    new Thread(createRunnable(obj2, obj1), "thread-2").start();
}

private static Runnable createRunnable(Object obj1, Object obj2) {
    return () -> {
        synchronized (obj1) {
            System.out.println(Thread.currentThread().getName() + "->1");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignore) {
            }
            System.out.println(Thread.currentThread().getName() + "->2");
            // 永远停滞
            synchronized (obj2) {
                System.out.println(Thread.currentThread().getName() + "->3");
            }
        }
    };
}
```

### 定位

#### jstack

```shell
jstack -l <PID> > FILEPATH
```

-   `-l` 锁信息

#### visual vm/Jconsole

`visual vm `-> `thread` ->`DeadLock deteted`

![image-20240528201502016](../asset/Day11-%E6%80%A7%E8%83%BD%E8%B0%83%E4%BC%98/image-20240528201502016.png)

#### ThreadDump+FastThread

复杂的死锁问题如哲学家就餐问题

### 解决

使用可重入锁

```shell
Lock lock = new ReentrantLock();
```



