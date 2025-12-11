# 垃圾回收器

## 查看当前使用的垃圾回收器

查看触发次数和STW的时间

arthus

```shell
dashboard -n l
```

或

```shell
memory
```

在Memory-GC一栏中, 新生代在上, 老年代在下

-   `copy` 复制算法
-   `marksweepcompact` 标记整理

## 分代GC调优思路

尽量只触发回收新生代的minor gc, 而能满足不触发full ge, SWT时间就会减少

### 内存分配调优

在虚拟机的默认设置中, 新生代大小远小于老年代的大小, 因为JVM认为老年代的东西会越积越多

但是, 在互联网项目中, 新生代的对象往往来的快, 去的快, 数量还多, 容易把一些暂时的数据挤到老年代里去

故可以调整年轻代空间的比例, 来适配不同类型的应用程序

### 算法选择

新生代和老年代选择不同的垃圾回收算法

新生代使用复制算法, 增加吞吐量

老年代选择标记-清除和标记-整理, 增加内存占有率

## 垃圾回收器组合

![image-20240520180613418](../assets/Day08-%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8/image-20240520180613418.png)

- 通用垃圾回收器
    - G1 JDK9之后主流推荐
- 年轻代的垃圾回收器
    - Serial
    - ParNew
    - Parallel Scavenge JDK8默认
- 老年代的垃圾回收器
    - CMS 在特殊情况下调用Serial Old
    - Serial Old
    - Parallel Old

常用组合

-   Serial +CMS 在JDK9废弃

-   ParNew +Serial Old 在JDK9废弃

    ```shell
    -XX:+UseParNewGC
    ```

-   Parallel Scavenge + Serial Old  在JDK14废弃

-   CMS 在JDK14废弃



-   Serial + Serial Old

    ```shell
    -XX:+UseSerialGC
    ```

-   ParNew + CMS JDK8之前 关注STW

    ```shell
    -XX:+UseParNewGC -XX:+UserConcMarkSweepGC
    ```

-   Paralel Scavenge + Parallel Old JDK8之前 关注吞吐量

    ```shell
    -XX:+UseParallelGC -XX:+UseParallelOldGC
    ```

    两个参数任选一
    
-   高版本用G1(默认)

    ```shell
    -XX:+UseG1GC
    ```

## Serial+Serial Old

在arthus显示为

单线程串行垃圾回收器

### 原理

Serial使用复制算法

SerialOld 使用标记-整理算法

阻塞用户线程执行垃圾回收线程

### 优缺点

-   单CPU处理器吞吐量出色
-   多CPU下吞吐量不如其他垃圾回收器, 堆如果偏大会让用户线程长时间等待

### 适用场景

Java客户端

硬件有限







## ParNew + CMS

ParNew 本质上是堆Serial在多CPU下的优化, 使用多线程进行垃圾回收器

CMS Concurrent Mark Sweep 并行标记-清理, CMS关注STW, 允许用户线程和垃圾回收线程在某些过程中同时执行, 减少用户等待时间

新生代使用ParNew拷贝算法, 老年代使用CMS

### CMS原理

1.  初始标记
    -   暂停用户线程
    -   单线程, 极短的时间标记出GCRoots能关联到的对象
2.  并发标记
    -   和用户线程并行地标记所有对象
    -   标记的执行也使用多线程
3.  重新标记
    -   停止用户线程
    -   并发标记阶段有些对象可能发生变化, 存在错标, 漏标的情况, 需要重新标记
4.  并发清理
    -   不停止用户线程
    -   清理死亡的对象



-   处理内存碎片

    -   会导致用户线程的暂停

    -   配置在N次调整Full GC之后再整理内存

        ```shell
        -XX:CMSFullGCsBeforeCompaction=N
        ```

        默认0



### 优缺点

-   多CPU停顿时间较短

-   CMS对SWT的停顿短, 用户体验更好

-   ParNew吞吐量和停顿时间仍有不足

-   CMS仍有内存碎片问题, 退化问题, 浮动垃圾问题

    -   浮动垃圾问题 并发清理过程中产生的对象无法在本次的垃圾回收中处理

        如果恰好在并发清理的时候是一个并发高峰

    -   退化问题 如果老年代内存不足无法分配对象, CMS退化成Serial Old单线程回收老年代

### 适用场景

请求数据量大, 频率高



## Parallel Savenge/Old

在arthus显示为`gc.ps_scavenge`, `gc.ps_marksweep`

多线程并行垃圾回收器

停止用户线程

关注系统吞吐量

Parallel Savenge**能自动调整堆内存的大小(老年代和新生代每一个部分的大小)**

Parallel Savenge使用复制算法, Parallel Old使用标记-整理

### 优缺点

-   吞吐量高
-   Savenge手动可控吞吐量参数
-   Parallel Savenge动态调整堆的大小, ==故建议不要配置堆内存最大值==
-   Parallel Savenge不能保证单次停顿时间
    -   但Parallel Savenge可以设置最大的单次暂停时间

### 配置

最大暂停时间, 默认2^32^, 如果把最大暂停时间调小, ==堆内存也会减少==, 以减少扫描回收整理的时间, 而且设置的太小, 以至于无法实现的化, GC也不会严格比这个配置小, 

```shell
-XX:MaxGCPauseMillis=100
```

吞吐量, 默认99

```shell
-XX:GCTimeRatio=99
```

$$
用户线程执行时间=\frac{GCTimeRatio}{GCTimeRatio+1}
$$



最大暂停时间和吞吐量矛盾, PS会同时满足两个值(==如果一个配置得不恰当, 就会拖后腿==)

故要不断测试, 保证每个配置之间达到平衡



自动调整内存大小, 默认开启

```shell
-XX:+UseAdapticeSizePolicy
```







### 适用场景

后台任务, 不需要与用户交互, 容易产生大量对象的情况

如大数据的处理, 大文件的导出

## G1

>   Gabage First

JDK9之后的默认垃圾回收器, 在JDK7之后发布, 可能不太成熟

Parallel Scavenge关注吞吐量, 允许用户设置最大暂停时间, 但是会减少年秦代可用空间大小

CMS关注暂停时间, 但是吞吐量方面下降

G1有两种垃圾回收方式

-   年轻代垃圾回收 Young GC
-   混合回收 Mixed GC

### 优缺点

-   支持巨大的堆空间回收, 有较高的吞吐量
-   支持多CPU并行垃圾回收
-   允许用户设置最大暂停时间
-   **JDK8及之前不够成熟**

### 原理

G1出现之前的垃圾回收器内存结构一般是连续的

![image-20240520204000259](../assets/Day08-%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8/image-20240520204000259.png)

G1的整个内存划分为多个大小相等的**Region**, 分为Eden, Survivor, Old

Region的大小通过对空间的大小/2048获得

配置Region的大小, 必须是2的指数幂, 范围是1M到32M

```shell
-XX:G1HeapRegionSize=32M
```

<img src="../asset/Day08-%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8/image-20240520204607319.png" alt="image-20240520204607319" style="zoom:67%;" />

### 年轻代垃圾回收

>   Young GC 只回收年轻代的Eden和Servivor

用户停滞, 导致STW

配置Young GC每次垃圾回收时的最大暂停时间ms数, 默认200, G1尽可能保证时间

```shell
-XX:MaxGCPauseMillis=200
```

1.  新创建的对象保存在Eden区, G1判断年轻代区不足( 默认下达到60%), 无法分配对象时会执行Young GC

2.  标记Eden和Servivor区域中的存活对象

3.  G1根据最大暂停时间选择某些区域将存活对象复制到一个**新的Survivor区**中(年龄+1), 清空未存活对象

    <img src="../asset/Day08-%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8/image-20240520205542577.png" alt="image-20240520205542577" style="zoom:67%;" />

    <img src="../asset/Day08-%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8/image-20240520205638070.png" alt="image-20240520205638070" style="zoom:67%;" />

4.  根据每一个Region的耗时计算平均值, 这个平均值和配置做比较, 对每次回收几个区域做调整

5.  年龄大于15放入老年代(晋升)

    -   若对象的大小超过Regin的一半,直接放入老年代, 这类老年代称为Humongous(巨大的)区
    -   如果对象过大会跨越多个Region



### 混合垃圾回收

多次回收后, 会出现很多老年代, 此时总堆栈率占有率达到阈值(默认45%)

```shell
-XX:InitiatingHeapOccupancyPercent=45
```

会触发混合回收MixedGC, 回收所有年轻代, 部分老年代和大对象区, 采用复制算法完成

G1对老年代的清理会原则存货度最低(存活的最少)的区域来进行回收, 这样可以保证回收效率最高, 即Garbage First



1.  初始标记 Initial mark

    -   阻塞用户进程
    -   多线程并行执行
    -   被GC Root引用的对象标记为存活

2.  并发标记 concurrent mark

    -   用户线程并发执行
    -   单线程串行执行
    -   标记存活的对象引用的对象标记为存活

3.  最终标记 remark/Finalize Marking

    -   处理在并发标记过程中用户线程对对象引用产生新变化的处理

    -   阻塞用户进程

    -   多线程并发执行

    -   标记引用漏标的对象(即再次被引用而复活的), 不管刚刚被断开引用的对象(此次被认为是存活的)和不被关联的对象(一开始就卒了的)

        保证了效率

4.  并发清理 cleanup

    -   用户线程并行执行
    -   单线程串行
    -   将存活对象复制到别的Region, 不会产生内存碎片

### Full GC



如果出现内存内没有Region可以用来拷贝

<img src="../asset/Day08-%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8/image-20240521003847341.png" alt="image-20240521003847341" style="zoom:67%;" />

就会触发**Full GC**对整个区域做一个回收, 导致**用户线程的暂停**

## Shenandoah GC

不分代

并行标记+并行复制-整理

由RedHat开发的OpenJDK用垃圾回收器

堆的大小对STW没有影响

回收小对象的时候性能最佳, STW在10ms级, 回收大对象的时候性能最差, 不会开启大量线程, 占用CPU资源较少,所以用户线程基本收影响最小

### 下载与版本选择

-   [下载](https://builds.shipilev.net/openjdk-jdk-shenandoah/)提供Shenandoah的OpenJDK
-   只提供Linux版本
-   使用`arch`查看系统架构
-   垃圾回收器类型
    -   `minimal` 关闭了一部分垃圾回收器
    -   `server` 所有的垃圾回收求
    -   `zero` 关闭所有的垃圾回收器
-   `release` 发布版



### 开启

```shell
-XX:+UseShenandoahGC
```



## ZGC

并行标记+并行复制-整理, 在JDK21分代(性能更优秀)

可扩展低延迟GC(不超过1ms). Oracle开发

每次回收一点点, 多回收几次

堆的大小对STW没有影响

吞吐量不佳

建议JDK15以上版本再使用ZGC

回收大小对象时性能都没有问题, STW在0.1ms级, 但是开启大量线程占用CPU资源, 导致用户请求延迟

### 开启

不开启分代的ZGC

```shell
-XX:UseZGC
```

开启分代ZGC

```shell
-XX:UseZGC -XX:+ZGenerational
```

### 配置

自动设置年轻代大小`-Xmn`

自动决定晋升阈值`-XX:TenuringThreshold`

JDK17之后自动决定并行线程数`-XX:ConcGCThreads`





需要设置最大堆内存大小, ZGC需要更多的内存用于垃圾回收`-Xmx`

建议堆内存控制再`-XX:SoftMaxHeapSize=value`以下

### Huge Page

使用Linux Huge Page 技术(默认2M, 减少映射造成的开销), 提升吞吐量, 降低延迟

操作系统直接将这部分内存预热, 认为已经被占用, 方便程序直接使用

1.  计算页数
    $$
    页数=\frac{堆空间+预留}{2M}
    $$

2.  配置系统的大页池, 需要root权限

    ```shell
    echo 上面算出的页数 > /sys/kernel/mm/hugepages/hugepages-2048kB/nr_hugepages
    ```

    不适用Huge Page , 配置成0关闭HugePage

3.  添加参数

    ```shell
    -XX:+UseLargePages
    ```

    

## 性能测试

```java
package org.sample;

import com.sun.management.OperatingSystemMXBean;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// 执行5轮预热，每次持续2秒
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
// 输出毫秒单位
@OutputTimeUnit(TimeUnit.MILLISECONDS)
// 统计方法执行的平均耗时
@BenchmarkMode(Mode.AverageTime)
// java -jar benchmarks.jar -rf json
@State(Scope.Benchmark)
public class MyBenchmark {

    // 每次测试对象大小 4KB和4MB
    @Param({"4","4096"})
    int perSize;

    private void test(Blackhole blackhole){
		// 使用JMX技术获取JVM的各种数据
        //每次循环创建堆内存60%对象 JMX获取到Java运行中的实时数据
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        //获取堆内存大小
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        // 获取到剩余的堆内存大小, 每次生成总内存占用量要达到堆内存的60%
        long heapSize = (long) ((heapMemoryUsage.getMax() - heapMemoryUsage.getUsed()) * 0.6);
        //计算循环次数
        long size = heapSize / (1024 * perSize);

        for (int i = 0; i < 4; i++) {
            List<byte[]> objects = new ArrayList<>((int)size);
            for (int j = 0; j < size; j++) {
                objects.add(new byte[1024 * perSize]);
            }
            blackhole.consume(objects);
        }
    }

//    @Benchmark
//    @Fork(value = 1,jvmArgsAppend = {"-Xms4g","-Xmx4g","-XX:+UseSerialGC"})
//    public void serialGC(Blackhole blackhole){
//        test(blackhole);
//    }
//
//    @Benchmark
//    @Fork(value = 1,jvmArgsAppend = {"-Xms4g","-Xmx4g","-XX:+UseParallelGC"})
//    public void parallelGC(Blackhole blackhole){
//        test(blackhole);
//    }
//
//    @Benchmark
//    @Fork(value = 1,jvmArgsAppend = {"-Xms4g","-Xmx4g"})
//    public void g1(Blackhole blackhole){
//        test(blackhole);
//    }
//
//    @Benchmark
//    @Fork(value = 1,jvmArgsAppend = {"-Xms4g","-Xmx4g","-XX:+UseShenandoahGC"})
//    public void shenandoahGC(Blackhole blackhole){
//        test(blackhole);
//    }

    //-XX:+UseZGC -XX:+ZGenerational

    @Benchmark
    @Fork(value = 1,jvmArgsAppend = {"-Xms4g","-Xmx4g","-XX:+UseZGC","-XX:+UseLargePages"})
    public void zGC(Blackhole blackhole){
        test(blackhole);
    }

    @Benchmark
    @Fork(value = 1,jvmArgsAppend = {"-Xms4g","-Xmx4g","-XX:+UseZGC","-XX:+ZGenerational","-XX:+UseLargePages"})
    public void zGCGenerational(Blackhole blackhole){
        test(blackhole);
    }


    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}

```

