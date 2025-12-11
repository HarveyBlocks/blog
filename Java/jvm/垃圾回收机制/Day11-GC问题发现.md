# 问题发现

-   

## 指标

### 吞吐量

>   Through Put

-   业务吞吐量

    -   一定时间内, 程序需要完成的业务数量

-   垃圾回收吞吐量

    -   CPU用于执行用户代码与CPU总执行时间的比值
        $$
        垃圾回收吞吐量=\frac{CPU用于用户代码时间}{CPU执行总时间}
        $$
        

    -   CPU总执行时间即执行用户代码时间, 和GC时间之和
        $$
        CPU执行总时间 = CPU用于用户代码时间+CPU用于GC时间
        $$
        

### 延迟

$$
延迟=GC延迟+业务执行时间
$$

### 内存使用量

Java应用占系统内存的最大值

可以通过JVM参数调整

在满足业务的延迟和吞吐量的要求的情况下, 内存的使用俩越小越好



### 展示指标

>   GC Easy





## 方法

### Jstat

JDK自带

```shell
jstat -gc PID MILLION_TIME COUNT
```

-   MILLION_TIME 间隔时长
-   COUNT 次数

![image-20240527133124398](../assets/Day11-GC%E8%B0%83%E4%BC%98/image-20240527133124398.png)

-   `C` Capacity
-   `U` Used
-   `S` 幸存者区
-   `E` Eden区
-   `O` 老年区
-   `M` 元空间
-   `YGC` 年轻代GC次数
-   `YGT` 年轻代GC耗时
-   `FGC` Full GC次数
-   `FGCT` Full GC耗时
-    `GCT` GC总耗时



比较粗糙, 不能直到哪个时间点执行了FullGC

### Visual GC插件

VisualVM中的插件, 下载地址在国外

实时监控Java进程的

-   堆内存结构
-   堆内存变化趋势
-   垃圾回收时间变化趋势
-   监控对象的晋升直方图





适合开发时使用, 生产时VisualVM为了获取数据还是比较消耗性能的

### Promethueus+Grafane

GC Pressure

## GC日志及其分析

### 配置打开GC日志

GC日志的打印配置

```shell
-verbose:gc
```

保存GC日志到文件

-   GDK8以下, verbose不生效

    ```shell
    -XX:+PrintGCDetails -Xloggc:文件名
    ```

-   GDK 9+

    ```shell
    -Xlog:gc*:file=文件名
    ```



### GCViewer

[下载](https://github.com/chewiebug/GCViewer)

启动

```shell
java -jar gcviewer_1.3.4.jar gc_log.log
```



### GCeasy

收款使用机器去学习的在线GC分析和诊断工具

定位内存泄漏, GC延迟高的问题, 提出JVM参数优化建议

[官网](https://gceasy.io/)

上传GC日志到网站, 让网站更新, 免费每月五次,付费



## GC模式及分析

### 锯齿状

![image-20240527174539591](../assets/Day11-GC%E8%B0%83%E4%BC%98/image-20240527174539591.png)

下降的最终内存占用差不多, 没有内存泄漏



### 缓存对象过多

![image-20240527174617469](../assets/Day11-GC%E8%B0%83%E4%BC%98/image-20240527174617469.png)

下降的高度依旧很高, 可能是起缓存作用的对象占用内存

在用户突然大量请求时有隐患

### 内存泄漏

局部锯齿状, 总体上升

![image-20240527174758234](../assets/Day11-GC%E8%B0%83%E4%BC%98/image-20240527174758234.png)

### 持续FullGC

在某个时间点FullGC飙升

![image-20240527174854173](../assets/Day11-GC%E8%B0%83%E4%BC%98/image-20240527174854173.png)

用户的请求突然飙升, 程序产生了大量对象

### 元空间不足

在堆区极低的的部分反复FullGC



![image-20240527175153041](../assets/Day11-GC%E8%B0%83%E4%BC%98/image-20240527175153041.png)

堆内存充足, 元空间不足, 导致JVM对元空间反复FullGC

