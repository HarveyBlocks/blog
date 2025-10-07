# Zookeeper命令

[Apache ZooKeeper - Server 3.7.2 API](https://zookeeper.apache.org/doc/r3.7.2/apidocs/zookeeper-server/index.html)

```bash
docker exec -it zookeeper bash
cd /apache-zookeeper-3.7.0-bin/bin/
ls -l
```

## 服务端命令

`/apache-zookeeper-3.7.0-bin/bin/zkServer.sh`

```bash
./zkServer.sh help
```

```bash
ZooKeeper JMX enabled by default
Using config: /conf/zoo.cfg
Usage: ./zkServer.sh [--config <conf-dir>] {start|start-foreground|stop|version|restart|status|print-cmd}
```



-   启动

    ```bash
    ./zkServer.sh start
    ```

-   停止

    ```bash
    ./zkServer.sh stop
    ```

-   重启

    ```bash
    ./zkServer.sh restart
    ```

-   查看版本

    ```bash
    ./zkServer.sh version
    ```

    Apache ZooKeeper, version 3.7.0 2021-03-17 09:46 UTC

-   查看状态

    ```bash
    ./zkServer.sh status
    ```

    Mode: standalone 单机状态

##客户端命令

[org.apache.zookeeper.cli](https://zookeeper.apache.org/doc/r3.7.2/apidocs/zookeeper-server/org/apache/zookeeper/cli/package-summary.html)

大小写敏感

### 连接服务端

```bash
./zkCli.sh [-server localhost:2181]
```

默认连本机, 省略参数和地址

```
[zk: localhost:2181(CONNECTED) 0] <= 表示进入
```



```
ZooKeeper -server host:port -client-configuration properties-file cmd args
        addWatch [-m mode] path # optional mode is one of [PERSISTENT, PERSISTENT_RECURSIVE] - default is PERSISTENT_RECURSIVE
        addauth scheme auth
        close 
        config [-c] [-w] [-s]
        connect host:port
        create [-s] [-e] [-c] [-t ttl] path [data] [acl]
        delete [-v version] path
        deleteall path [-b batch size]
        delquota [-n|-b|-N|-B] path
        get [-s] [-w] path
        getAcl [-s] path
        getAllChildrenNumber path
        getEphemerals path
        history 
        listquota path
        ls [-s] [-w] [-R] path
        printwatches on|off
        quit 
        reconfig [-s] [-v version] [[-file path] | [-members serverID=host:port1:port2;port3[,...]*]] | [-add serverId=host:port1:port2;port3[,...]]* [-remove serverId[,...]*]
        redo cmdno
        removewatches path [-c|-d|-a] [-l]
        set [-s] [-v version] path data
        setAcl [-s] [-v version] [-R] path acl
        setquota -n|-b|-N|-B val path
        stat [-w] path
        sync path
        version 
        whoami 

```



断开连接并退出

```bash
quit
```





### 查看目录/节点

```bash
ls [-s] [-w] [-R] path
ls2 path [watch]
```

-   无参

    查看当前节点子节点

    ```powershell
    [zk: localhost:2181(CONNECTED) 12] ls /
    [dubbo, zookeeper]
    
    ```

-   `-w`

    ```bash
    [zk: localhost:2181(CONNECTED) 12] ls -w /dubbo
    [com.harvey.dubbo.api.service.UserService, com.harvey.dubbo.service.HelloService, com.harvey.dubbo.service.UserService, com.harvey.dubbo.web.HelloController%24HelloService]
    
    ```

-   `-R`区分大小写

    查看当前节点后代节点信息

    ```bash
    [zk: localhost:2181(CONNECTED) 19] ls -R /dubbo/com.harvey.dubbo.api.service.UserService
    /dubbo/com.harvey.dubbo.api.service.UserService
    /dubbo/com.harvey.dubbo.api.service.UserService/configurators
    /dubbo/com.harvey.dubbo.api.service.UserService/consumers
    /dubbo/com.harvey.dubbo.api.service.UserService/providers
    /dubbo/com.harvey.dubbo.api.service.UserService/routers
    /dubbo/com.harvey.dubbo.api.service.UserService/consumers/consumer%3A%2F%2F192.168.54.1%2Fcom.harvey.dubbo.api.service.UserService%3Fapplication%3Dweb%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.6.2%26interface%3Dcom.harvey.dubbo.api.service.UserService%26methods%3DsayHello%26pid%3D6440%26side%3Dconsumer%26timestamp%3D1712467353397
    /dubbo/com.harvey.dubbo.api.service.UserService/providers/dubbo%3A%2F%2F192.168.54.1%3A20882%2Fcom.harvey.dubbo.api.service.UserService%3Fanyhost%3Dtrue%26application%3Dweb%26dubbo%3D2.6.2%26generic%3Dfalse%26interface%3Dcom.harvey.dubbo.api.service.UserService%26methods%3DsayHello%26pid%3D440%26side%3Dprovider%26timestamp%3D1712467444364
    /dubbo/com.harvey.dubbo.api.service.UserService/providers/dubbo%3A%2F%2F192.168.54.1%3A22820%2Fcom.harvey.dubbo.api.service.UserService%3Fanyhost%3Dtrue%26application%3Dweb%26dubbo%3D2.6.2%26generic%3Dfalse%26interface%3Dcom.harvey.dubbo.api.service.UserService%26methods%3DsayHello%26pid%3D3960%26side%3Dprovider%26timestamp%3D1712467347257
    
    ```

-   `-s`

    查看当前节点详细信息

    ```bash
    ls2 path [watch] # 和`ls -s`一样的效果, 已经被淘汰, 应使用`ls -s`代替
    ```

    ```bash
    [zk: localhost:2181(CONNECTED) 13] ls -s /dubbo
    [com.harvey.dubbo.api.service.UserService, com.harvey.dubbo.service.HelloService, com.harvey.dubbo.service.UserService, com.harvey.dubbo.web.HelloController%24HelloService]
    cZxid = 0x2
    ctime = Sat Apr 06 08:30:01 UTC 2024
    mZxid = 0x2
    mtime = Sat Apr 06 08:30:01 UTC 2024
    pZxid = 0x188
    cversion = 4
    dataVersion = 0
    aclVersion = 0
    ephemeralOwner = 0x0
    dataLength = 12
    numChildren = 4
    
    ```

    

###创建节点

```bash
create [-s] [-e] [-c] [-t ttl] path [data] [acl]
```

path唯一

`-e`暂时

`-s`有序

```bash
[zk: localhost:2181(CONNECTED) 29] create /app1 1
Created /app1
[zk: localhost:2181(CONNECTED) 30] create /app2 2
Created /app2
[zk: localhost:2181(CONNECTED) 31] create /app3
Created /app3

```

存的值是字符串, `datalength`是字符串长度







### 获取节点内数据

```bash
[zk: localhost:2181(CONNECTED) 37] get /app2
2
[zk: localhost:2181(CONNECTED) 38] get /app1
1
[zk: localhost:2181(CONNECTED) 39] get /app3
null

```



### 设置数据

```bash
[zk: localhost:2181(CONNECTED) 37] get /app2
2
[zk: localhost:2181(CONNECTED) 38] get /app1
1
[zk: localhost:2181(CONNECTED) 39] get /app3
null

```



### 删除数据

```bash
 delete [-v version] path
```



```bash
[zk: localhost:2181(CONNECTED) 37] get /app2
2
[zk: localhost:2181(CONNECTED) 38] get /app1
1
[zk: localhost:2181(CONNECTED) 39] get /app3
null

```

#### 删除所有

```bash
deleteall path [-b batch size]
```



```bash
[zk: localhost:2181(CONNECTED) 12] create /app
Created /app
[zk: localhost:2181(CONNECTED) 13] create /app/ap
Created /app/ap
[zk: localhost:2181(CONNECTED) 14] create /app/ap/a
Created /app/ap/a
[zk: localhost:2181(CONNECTED) 15] delete /app
Node not empty: /app
[zk: localhost:2181(CONNECTED) 16] deleteall /app

```

### 临时节点

保存一次会话(一次客户端)

客户端一开, 一关, 就清除

```bash
create -e /app
```



### 顺序节点

```bash
create -s /app
```

```bash
[zk: localhost:2181(CONNECTED) 1] create -es /app
Created /app0000000009
[zk: localhost:2181(CONNECTED) 2] create -es /app
Created /app0000000010
[zk: localhost:2181(CONNECTED) 3] create -es /app
Created /app0000000011
[zk: localhost:2181(CONNECTED) 4] create -es /app
Created /app0000000012
[zk: localhost:2181(CONNECTED) 5] create -es /app
Created /app0000000013
[zk: localhost:2181(CONNECTED) 6] create -es /app
Created /app0000000014
[zk: localhost:2181(CONNECTED) 7] create -es /app
Created /app0000000015
[zk: localhost:2181(CONNECTED) 8] create -es /app
Created /app0000000016
[zk: localhost:2181(CONNECTED) 9] create -es /app
Created /app0000000017

```

所有节点共用一个编号

```bash
[zk: localhost:2181(CONNECTED) 1] create -es /app
Created /app0000000019
[zk: localhost:2181(CONNECTED) 2] create -es /app1
Created /app10000000020
[zk: localhost:2181(CONNECTED) 3] create -es /app2
Created /app20000000021
[zk: localhost:2181(CONNECTED) 4] create -es /app3
Created /app30000000022
[zk: localhost:2181(CONNECTED) 5] create -es /app4
Created /app40000000023

```

