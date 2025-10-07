# 有序性

##指令重排

JVM在不影响程序正确性的情况下, 会调整语句的执行顺序, 以提高效率(涉及CPU的寄存器)

**但是指令重排可能在多线程的情况下影响到程序的正确性**

### 指令重排的好处

CPU流水线技术, 将一个指令操作分成了多个小指令(取指令, 指令译码, 执行指令, 内存访问, 数据写回)

![image-20240909181245606](../assets/Day05-%E6%9C%89%E5%BA%8F%E6%80%A7/image-20240909181245606.png)

不会缩短单条指令的执行顺序, 但增大了指令的吞吐量, 提高了效率

### 对多线程产生的影响

诡异的结果

```java
private static class Result {
    private int result;

    public int get() {
        return result;
    }

    public Result set(int num) {
        this.result = num;
        return this;
    }
}

private static class Target {
    private int num = 0;
    private boolean ready = false;

    public void act1(Result result) {
        if (ready) {
            result.set(num + num);
        } else {
            result.set(1);
        }
    }

    public void act2() {
        num = 2;
        ready = true;
    }
}
```

Result结果的可能值

1.  ready=false, result则为1

2.  ready=true ,  但是act1已经进入false分支, 此时值为1

3.  ready=true,  进入true分支, num = 2, 此时result为4

4.  ==由于指令重排列== , ready先为true, num依旧为0, 此时进入true分支, ==结果为0==

    据说是万分之一

## 解决

###volatile解决

在`ready`字段上修饰`volatile`

写屏障, 保证其之上的代码不会发生重排序

### synchronized解决

如果一个变量**完全**被synchronized管理, 那么其不会产生重排序

但是这很难, 而且一般不会为了解决重排序问题而将一个变量处处放在synchronized的代码块里

往往以为全部交给synchronized管理了, 却还是有漏网之鱼

而且synchronized的效率一直被诟病

故, ==别想着用synchronized去解决有序性问题==

分析问题时, 即使有synchronized保护了代码块中的变量, 也要保持怀疑精神怀疑是否有可能产生重排序; 重排序是否对程序正确性产生影响

## 原理

[volatile](Day05-volatile.md)