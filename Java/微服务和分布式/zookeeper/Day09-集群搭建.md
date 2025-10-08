# 集群搭建

1.  启动

    ```bash
    docker run -d --name \
    	zookeeper1 \
    	--privileged=true \
    	-p 2183:2181 \
    	-v /root/docker-data/zookeeper1/data:/data \
    	-v /root/docker-data/zookeeper1/conf:/conf \
    	-v /root/docker-data/zookeeper1/logs:/datalog \
    	zookeeper
    ```

2.  开机自启动

    ```bash
    docker update --restart=always zookeeper1
    ```

3.  改权限

    ```bash
    chmod -R 777 /root/docker-data/zookeeper1
    ```

    

4.  挂载配置文件目录(/mydata/zookeeper/conf)下

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

5.  访问端口

    ```bash
    curl localhost:218
    ```

6.  查看状态

    ```bash
    docker exec -it zookeeper bash
    ```

    ```bash
    /bin/zkServer.sh status
    ```

    返回: 

    ```
    ZooKeeper JMX enabled by default
    Using config: /conf/zoo.cfg
    Client port found: 2181. Client address: localhost. Client SSL: false.
    Mode: standalone # 没有使用集群
    
    ```

7.  配置日志

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

8.  复制配置文件

    ```bash
    cp -r /rook/docker-data/zookeeper/conf /rook/docker-data/zookeeper0/conf
    cp -r /rook/docker-data/zookeeper/conf /rook/docker-data/zookeeper1/conf
    ```

    

## 选举

Leader-Follower

-   `Serverid` 服务器ID

    编号越大, 在选举算法中权重越大

-   `ZID` 数据ID

    服务器中存放的最大数据ID

    值越大说明数据越新

    在选举算法中权重越大

-   如果某台ZooKeeper获得了半数(集群中的半数, 没启动的也算集群成员, 但当选的只能是启动的)的选票, 则此ZooKeeper就可以成为Leader了

-   如果选举出Leader之后, 有集群里新成员启动, 则不再选举

## 搭建集群

网络连接

```bash
docker network create zoo
docker network connect zoo zookeeper
docker network connect zoo zookeeper0
docker network connect zoo zookeeper1
```



创建`myid`文件

```bash
cd ~/docker-data
echo 1 >zookeeper/data/myid
echo 2 >zookeeper0/data/myid
echo 3 >zookeeper1/data/myid
```





更改配置文件

注意是两个端口

```bash
vim zookeeper/conf/zoo.cfg 
vim zookeeper0/conf/zoo.cfg 
vim zookeeper1/conf/zoo.cfg 
```

每个节点配置的一样:

```bash
server.1=zookeeper:2881:3881
server.2=zookeeper0:2881:3881
server.3=zookeeper1:2881:3881
```

-   服务器IP: 服务器之间通信端口: 服务器之间投票选举端口


### 启动

```bash
zkServer.sh status
```

```
ZooKeeper JMX enabled by default
Using config: /conf/zoo.cfg
Client port found: 2181. Client address: localhost. Client SSL: false.
Mode: leader
```

## 故障测试

当集群中正常启动的节点数量小于集群半数, 即投票方不到半数, 当前的Leader也会不会运行(当前的集群处于休眠状态)

当主节点宕机, 而又有大于半数的节点存活, 即集群存在选举能力, 则会选举新的Leader



## 角色

-   同一个集群的节点数据都是相同的
-   Leader 
    -   处理事物请求
    -   集群内部各服务器的调用者(同步数据)
-   Follower
    -   处理客户端非事物请求
    -   转发事物请求给Leader服务器
    -   参与Leader选举投票
-   Observer
    -   处理客户端非事物请求
    -   转发事物请求给Leader
