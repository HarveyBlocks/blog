# 性能测试

## JMH

OpenJDK的组件

[github](https://github.com/openjdk/jmh), 可以在smples查看例子以学习JMH的用法

JMH会首先执行预热过程, 确保JIT对代码进行优化之后再进行真正的迭代测试, 最后输出测试结果

### 搭建环境

```
mvn archetype:generate -DinteractiveMode=false   -DarchetypeGroupId=org.openjdk.jmh   -DarchetypeArtifactId=jmh-java-benchmark-archetype   -DgroupId=org.sample   -DartifactId=test   -Dversion=1.0
```

修改版本

![image-20240528212406405](../assets/Day13-%E6%80%A7%E8%83%BD%E8%B0%83%E4%BC%98/image-20240528212406405.png)

### 测试代码

```java
@Warmup(iterations = 5, time = 1) // 预热5次, 每次1s
@Fork(value = 1, jvmArgsAppend = {"-Xms1g", "-Xmx1g"})
@BenchmarkMode(Mode.All) // 输出模式
@OutputTimeUnit(TimeUnit.NANOSECONDS)
// 变量共享范围,本Benchmark测试共享这个测试类生成的实体
// Scope.Thread, 在一个线程共享一个实体
@State(Scope.Benchmark)
public class MyBenchmark {
	// ... 
}
```



#### 初始化

```shelll
@Setup
public void init(){
	System.out.println("init")
}
```



#### 测试方法

```java
@Benchmark
public int testMethod() {
    int i = 1;
    i++;
    return i;
}
```
### 打包启动

```shell
mvn clean verify
```

打包之后有一个自动生成的`benchmarks.jar`

```shell
java -jar .\target\benchmarks.jar
```

![image-20240528213725695](../assets/Day13-%E6%80%A7%E8%83%BD%E8%B0%83%E4%BC%98/image-20240528213725695.png)



### main启动

不如Jar包准确

```java
public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
            .include(MyBenchmark.class.getSimpleName())
            .forks(1)
            .build();
    new Runner(options).run();
}
```

## 死代码

### 死代码问题

JIT会把那些没有被使用的代码从代码层面上消去, 以增加效率

但是这样可能会和预想的不一样, 导致速度大小的变化

![image-20240528214410353](../assets/Day13-%E6%80%A7%E8%83%BD%E8%B0%83%E4%BC%98/image-20240528214410353.png)

可以返回这个值, 以保证不会被JIT消去

![image-20240528214457272](../assets/Day13-%E6%80%A7%E8%83%BD%E8%B0%83%E4%BC%98/image-20240528214457272.png)

### 黑洞

如果有两个四代码的变量呢?

```java
@Benchmark
public void testMethod(Blackhole blackhole) {
    int i = 1, j = 2;
    i++;
    j--;
    blackhole.consume(i);
    blackhole.consume(j);
}
```



### SpringBoot下的JMH

SpringBoot有自己的启动器, 则为之奈何?

```java
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgsAppend = {"-Xms1g", "-Xmx1g"})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class PracticeBenchmarkTest {

    private Controller controller;

    @Setup
    public void setup() {
        // 每一轮Benchmark都启动一个SpringApplication以控制变量
        // 同时需要将端口配置成随机
        // server.port=${random.int(2000,8000)}
        this.controller = new SpringApplication(JvmOptimizeApplication.class)
            .run().context.getBean(Controller.class);
    }

    @Test
    public void executeJmhRunner() throws RunnerException, IOException {
        new Runner(new OptionsBuilder()
                .shouldDoGC(true)
                .forks(0) // 使用SpringApplication#run方法启动程序, 故不适用fork
                .resultFormat(ResultFormatType.JSON)
                .shouldFailOnError(true)
                .build()).run();
    }

    @Benchmark
    public void test1(final Blackhole bh) {
        bh.consume(controller.method1());
    }

    @Benchmark
    public void test2(final Blackhole bh) {

        bh.consume(controller.method2());
    }

    @Benchmark
    public void test3(final Blackhole bh) {
        bh.consume(controller.method3());
    }
}
```

## 可视化测试结果

[网站](https://jmh.morethan.io)

生成Json文件测试报告

```java
new Runner(new OptionsBuilder()
        .include(MyBenchmark.class.getSimpleName())
        .forks(1)
        .resultFormat(ResultFormatType.JSON)
        .build()).run();
```

或者

```shell
java -jar .\target\benchmarks.jar -rf json
```

