# 线程运行原理

## 栈与栈帧

-   一个线程对应一个栈

-   一个栈内, 每次方法调用, 从栈中划分一个栈帧给方法使用

    ```java
    public Thread(ThreadGroup group, Runnable target, String name,
                  long stackSize) {
        this(...);
    }
    ```

    stackSize设置新线程栈的大小, 单位: 字节

-   一个线程只有一个 **活动栈帧** , 对应当前正在执行的方法

## 上下文切换

>   Thread Context Switch

从使用CPU到不占用CPU的状态变化

Context Switch反复发生会影响性能

### 发生时机

-   线程CPU的时间片用完, 任务调度器收回CPU使用权
-   垃圾回收
-   有更高优先级的线程需要运行
-   线程自己调用`sleep()`, `yield()`, `wait()`, `join()` , `park()`, `synchronized()` , `lock`等方法时

### 切换流程

当Context Switch发生

操作系统保存当前线程状态, 并恢复另一个线程的状态

Java中对应的概念即程序计数器(Program Counter Register), 用于记住吓一跳JVM指令的执行地址, 线程私有

状态包括`程序计数器`, `虚拟机栈` (栈中每个栈帧, 包含局部变量, 操作数栈, 返回地址)

