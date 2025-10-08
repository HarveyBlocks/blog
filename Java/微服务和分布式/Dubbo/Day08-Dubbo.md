# Dubbo

## 概述

>   Alibaba 开源的, 高性能 , 轻量级的Java RPC框架

-   致力于提供高性能和透明化的RPC远程服务调用方案, 以及SOA服务治理方案
-   [官网](http:\\dubbo.apache.org)

## 架构

![architecture-2.png (2364×1370)](../assert/Day01-Dubbo/architecture-2.png)

## ZooKepper安装

[安装](.\资料\Zookeeper安装.md)

Dubbo推荐的注册中心

### Docker安装

1.  安装镜像

    ```bash
    docker search zookeeper
    docker pull zookeeper
    docker images
    ```

2.  准备挂载目录

    ```bash
    mkdir /root/docker-data
    cd /root/docker-data
    mkdir zookeeper
    cd zookeeper
    mkdir data
    mkdir conf
    mkdir logs
    ```

3.  改权限

    ```bash
    chmod -R 777 /root/docker-data
    ```

    

4.  启动

    ```bash
    docker run -d --name \
    	zookeeper \
    	--privileged=true \
    	-p 2181:2181 \
    	-v /root/docker-data/zookeeper/data:/data \
    	-v /root/docker-data/zookeeper/conf:/conf \
    	-v /root/docker-data/zookeeper/logs:/datalog \
    	zookeeper
    ```

5.  开机自启动

    ```bash
    docker update --restart=always zookeeper
    ```

6.  挂载配置文件目录(/mydata/zookeeper/conf)下

    zoo.conf

    ```properties
    dataDir=/data
    dataLogDir=/datalog
    clientPort=2181
    tickTime=2000
    initLimit=5
    syncLimit=2
    autopurge.snapRetainCount=3
    autopurge.purgeInterval=0
    maxClientCnxns=60
    standaloneEnabled=true
    admin.enableServer=true
    server.1=localhost:2888:3888
    
    ```

7.  访问端口

    ```bash
    curl localhost:2181
    ```

8.  查看状态

    ```bash
    docker exec -it zookeeper bash
    ```

    ```bash
    /bin/zkServer.sh status
    ```

    ```
    ZooKeeper JMX enabled by default
    Using config: /conf/zoo.cfg
    Client port found: 2181. Client address: localhost. Client SSL: false.
    Mode: standalone # 没有使用集群
    
    ```

9.  配置日志

    ```bash
    touch /rook/docker-data/zookeeper/conf/log.properties
    ```

    ```properties
    log4j.rootLogger=INFO,console,dailyFile,im
    log4j.additivity.org.apache=true
    
    # 控制台(console)
    log4j.appender.console=org.apache.log4j.ConsoleAppender
    log4j.appender.console.Threshold=INFO
    log4j.appender.console.ImmediateFlush=true
    log4j.appender.console.Target=System.err
    log4j.appender.console.layout=org.apache.log4j.PatternLayout
    log4j.appender.console.layout.ConversionPattern=[%-5p] %d(%r) --> [%t] %l: %m %x %n
    # 日志文件(logFile)
    log4j.appender.logfile.Encoding=UTF-8
    log4j.appender.logFile=org.apache.log4j.FileAppender
    log4j.appender.logFile.Threshold=INFO
    log4j.appender.logFile.ImmediateFlush=true
    log4j.appender.logFile.Append=true
    log4j.appender.logFile.layout=org.apache.log4j.PatternLayout
    log4j.appender.logFile.layout.ConversionPattern=[%-5p] %d(%r) --> [%t] %l: %m %x %n
    # Set up for Log Factor 5
    log4j.appender.socket.layout=org.apache.log4j.PatternLayout
    log4j.appender.socket.layout.ConversionPattern=[%-5p] %d(%r) --> [%t] %l: %m %x %n
    # Log Factor 5 Appender
    log4j.appender.LF5_APPENDER=org.apache.log4j.lf5.LF5Appender
    log4j.appender.LF5_APPENDER.MaxNumberOfRecords=2000
    ```

    成功



## Dubbo试用

1.  创建服务提供者Provider
2.  创建服务消费者Consumer
3.  在服务提供者模块编写UserServiceImpl提供服务
4.  在服务消费者中的UserController远程调用UserServiceImpl提供的服务
5.  分别启动两个服务, 测试

### 项目结构

```tree
DUBBO
|   pom.xml
|
|
+---service
|   |   .gitignore
|   |   pom.xml
|   |
|   \---src
|       +---main
|       |   +---java\com\harvey
|		|	|			|
|       |   |           \---dubbo
|       |   |               |   ServiceApplication.java
|       |   |               |
|       |   |               \---service
|       |   |                   |   HelloService.java
|       |   |                   |
|       |   |                   \---impl
|       |   |                           HelloServiceImpl.java
|       |   |
|       |   \---resources
|       |       |   application.yml
|       |       |
|       |       \---static
|       |               index.html
|       |
|       \---test\java\com\harvey\dubbo
|							|
|                           \---service
|                                   ServiceApplicationTests.java
|
\---web
    |   .gitignore
    |   pom.xml
    |
    +---src
        +---main
        |   +---java
        |   |   \---com\harvey
        |   |           |
        |	|			\---dubbo
        |   |               |   WebApplication.java
        |   |               |
        |   |               +---service
        |   |               |       HelloService.java # 仅仅是作为标识的接口
        |   |               |
        |   |               +---vo
        |   |               |       Null.java
        |   |               |       Result.java
        |   |               |
        |   |               \---web
        |   |                       HelloController.java
        |   |
        |   \---resources
        |       |   application.yml
        |       |
        |       \---static
        |               index.html
        |
        \---test
            \---java\com\harvey\dubbo
            				|
                            \---web
                                    WebApplicationTests.java
```



### 引入依赖

版本配置

```java
<dubbo.version>2.7.4.1</dubbo.version>
<zookeeper.version>4.0.0</zookeeper.version>
```

```xml
<!--Dubbo的起步依赖，版本2.7之后统一为rg.apache.dubb -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo</artifactId>
    <version>${dubbo.version}</version>
</dependency>
<!--在本项目中不需要的Zookeeper依赖-->
<!--ZooKeeper客户端实现 -->
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-framework</artifactId>
    <version>${zookeeper.version}</version>
</dependency>
<!--ZooKeeper客户端实现 -->
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>${zookeeper.version}</version>
</dependency>
```

spring boot的dubbo依赖

```xml
<dependency>
    <groupId>com.alibaba.boot</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>0.2.0</version>
</dependency>
```

不需要依赖自己的包

```xml
<dependency>
    <groupId>com.harvey.dubbo.service</groupId>
    <artifactId>service</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

现在要结构上web依赖于service



#### 配置注册中心Zookeeper

service上

```yaml
dubbo:
  application:
    name: hello-service # 需要唯一
  # 注册中心
  registry:
    address: zookeeper://centos:2181
  # 包扫描
  scan:
    base-packages: com.harvey.dubbo.service.impl
```

web上

```yaml
dubbo:
  application:
    name: hello-web # 需要项目唯一
  # 注册中心
  registry:
    address: zookeeper://centos:2181
  # 包扫描
  scan:
    base-packages: com.harvey.dubbo.web
```

### 启动类注解

两边的启动类都需要注解

```java
@EnableDubbo
```

### Provider-Service

```java
package com.harvey.dubbo.service.impl;

import com.harvey.dubbo.service.HelloService;


// @org.springframework.stereotype.Service // Spring的Service注解, 将该类的对象创建出来, 放到Spring的IOC容器中, bean定义
@com.alibaba.dubbo.config.annotation.Service
// 使用Dubbo的Service注解, 将这个类提供的方法(服务)对外发布. 将访问的地址(IP,端口, 路径)注册到注册中心
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "Hello "+name;
    }
}
```

### Consumer-Controller/Web

```java
package com.harvey.dubbo.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.harvey.dubbo.service.HelloService;
import com.harvey.dubbo.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Name
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-04-06 15:32
 */
@RestController
@RequestMapping("/hello")
@Api(tags = "测试接口")
@Slf4j
public class HelloController {

    @com.alibaba.dubbo.config.annotation.Reference // 远程注入
    // 1. 从注册中心获取UserService的访问路径
    // 2. 进行远程调用Rpc
    // 3. 将结果赋值为一个代理对象, 给变量赋值
    // 4. 3.X版本的Dubbo使用@DubboReference
    private HelloService helloService;

    @ApiOperation("测试")
    @GetMapping("/{name}")
    public Result<String> sayHello(
            @PathVariable("name") String name) {
        log.info("来了," + name);
        return new Result<>(helloService.sayHello(name));
    }
}
```

### 解决在Consumer方对Provider方的接口不存在的报错问题

例如上面的HelloService一定要在Consumer所在接口也写一份一模一样的, 否则无法做映射

API一多, 就很完蛋

解决方法: 二者都依赖同一个接口

```xml
<dependency>
    <groupId>com.harvey.dubbo</groupId>
    <artifactId>interface</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

可





## 测试

经测试

当服务提供者下线后,做更改, 上线后, dubbo能认为其进行了改变, 服务的消费者能使用新的提供者

当服务提供者更改地址之后, dubbo能认为其进行了改变, 服务的消费者能使用新的提供者



注意!

当服务的消费者在开启后再开启服务的生产者, dubbo无法再次对服务的生产者提供服务的消费者

服务的消费者无法消费消费者启动之前的服务生产者

那么如果发生循环依赖, 这两个服务将无法正常执行

## 循环依赖

Dubbo会在启动时检查依赖的服务是否可用，不可用时会抛出异常

阻止Spring初始化完成，

以便**上线时能及早发现问题**

**默认check=true**



如果你的Spring容器是**懒加载**的，或者通过API编程延迟引用服务，请关闭check

否则服务临时不可用时，会抛出异常，拿到null引用

如果check=false，总是会返回引用

当服务恢复时，能自动连上



**测试**，对**服务不关心**，或者出现了**循环依赖**,  可以通过check="false"关闭检查

### 关闭检查

关闭某个服务的启动时检查：(没有提供者时报错)

```java
@com.alibaba.dubbo.config.annotation.Reference(check = false)
private HelloService helloService;
```

关闭所有服务的启动时检查：(没有提供者时报错)

```yaml
dubbo:
  consumer:
    check: false
```

关闭注册中心启动时检查：(注册订阅失败时报错)

```yaml
dubbo:
  registry:
    address: zookeeper://centos:2181
    check: false
```

**注意区别**

-   dubbo.reference.check=false，强制改变所有reference的check值，就算提供者配置中有声明，也会**被覆盖**
-   dubbo.consumer.check=false，是设置check的缺省值，如果配置中有显式的声明，如：dubbo.reference.check=true，不会受影响。
-   dubbo.registry.check=false 如果注册订阅失败时，也允许启动，将在后台**定时重试**。



如果需要饥饿加载，即没有人引用也立即生成动态代理，可以配置：

```java
@com.alibaba.dubbo.config.annotation.Reference(init = true )
private HelloService helloService;
```
