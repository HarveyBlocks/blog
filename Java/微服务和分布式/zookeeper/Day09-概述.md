# 概述

## 概念

Apache Hadoop项目下的一个子项目

是一个**树形目录服务**

Hadoop是一个大数据的项目

ZooKeeper 动物管理员

-   Hadoop 大象
-   Hive 蜜蜂
-   Pig 小猪



Zookeeper是一个**分布式**的, 开源的分布式应用程序的协调服务

## 功能

-   配置管理
-   分布式锁
-   集群管理









## 安装

[安装](..\Dubbo\资料\Zookeeper安装.md)

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

