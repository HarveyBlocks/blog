# Curator

ZooKeeper的Java客户端框架



## 介绍

ZooKeeper的Java客户端

-   原生的Zookeeper客户端, 不好用
-   ZkClient 简化封装原生Api, 不好用
-   Curator, 重新实现Api, Netflix公司, 捐给Apache了, 不是很好用

Zookeeper是Apache的Hadoop旗下的子项目

Zookeeper的Java客户端Curator是Apache的顶级项目



[Apache Curator](https://curator.apache.org/docs/about)

## 引入依赖

```xml
<!--Zookeeper-->
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>5.6.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-framework</artifactId>
    <version>5.6.0</version>
</dependency>
```

Curator对Zookeeper向下兼容, Curator版本宜高不宜低



## 建立连接



```java
private CuratorFramework CLIENT;

@Test
public void testConnectWay1() {
    CLIENT = CuratorFrameworkFactory.newClient(
            "centos:2181", // 连接字符串, 地址, 集群的话, 逗号隔开
            60000,// 会话超时时间, 单位ms, 在客户端和Zookeeper传输信息的时候没有成功连接就算超时了, 默认60*1000
            3000,// 连接超时时间, 单位ms, 默认15*1000
            new RetryNTimes(2, 3000/*ms*/)// 重试超时策略, RetryPolicy的实现类们
    );
    // 开启连接
    CLIENT.start();
}
@Test
public void testConnectWay2() {
    CLIENT = CuratorFrameworkFactory.builder()
            .connectString("centos:2181")
            .sessionTimeoutMs(60 * 1000)
            .connectionTimeoutMs(15 * 1000)
            .retryPolicy(new RetryOneTime(2*1000))
        	.namespace("test") // 默认此连接上的一切操作都基于test目录下
            .build();
    CLIENT.start();
}
@AfterEach
public static void close() {
    log.info("断开连接");
    if (client!=null){
        client.close();
    }
}
```

关闭日志(因为会有很多日志)

```properties
log4j.rootLogger=off,stdout

log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target = System.out
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern = [%d{yyyy-MM-dd HH/:mm/:ss}]%-5p %c(line/:%L) %x-%m%n
```

## 操作节点

### 创建节点

```java
@Test
public void testCreate() throws Exception {
    // 创建节点
    String path = client.create().forPath("/app1"); // 默认数据是当前客户端的IP地址, 可以在builder设置默认
    log.info("path = {}", path);
    // 指定数据
    path = client.create().forPath("/app2", "Hello World".getBytes(StandardCharsets.UTF_8));
    log.info("path = {}", path);
    // 临时
    path = client.create().withMode(CreateMode.EPHEMERAL).forPath("/app3");
    log.info("path = {}", path);
    // 顺序
    path = client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/app4");
    log.info("path = {}", path);
    // 多级节点
    path = client.create().creatingParentsIfNeeded()
            .forPath("/app5/app/ap/a");
    log.info("path = {}", path);
}
```

###获取节点信息

```java
@Test
public void testGet() throws Exception {
    // 查询数据 get
    byte[] bytes = client.getData().forPath("/app1");
    log.info(new String(bytes,StandardCharsets.UTF_8)); // 192.168.54.1
    // 查询子节点 ls
    List<String> nodes = client.getChildren().forPath("/"); // 这里写了`/`, 表示以namespace`test`为根节点
    log.info(nodes.toString()); //  [app5, app2, app40000000003, app1]
    List<String> children = client.getChildren().forPath("/app2"); // 不会返回null, 而是空集合
    log.info(children.toString()); //  []
    // 查询节点状态 ls -s
    Stat stat = new Stat();
    log.info(stat.toString());
    client.getData().storingStatIn(stat).forPath("/");
    log.info(stat.toString());
    // 这么麻烦是历史遗留问题, 是因为远古版本的Zookeeper的get命令会返回状态信息
}
```

### 修改节点数据

```java
@Test
public void testSet() throws Exception {
    Stat stat = new Stat();
    // 更新有多线程的线程安全问题, 查询version保证原子性
    for (int i = 0; i < 5; i++) {
        client.getData().storingStatIn(stat).forPath("/app1");
        int version = stat.getVersion();
        log.info("" + version);
        stat = client.setData().withVersion(version) // 用此版本去更新
                .forPath("/app1", ("version is " + version).getBytes(Charset.defaultCharset()));
    }
    // 0 1 2 3 4
}
```

### 删除节点

```java
@Test
public void testDelete() throws Exception {
    // delete
    client.delete().forPath("/app1");
    // deleteall
    client.create().creatingParentsIfNeeded().forPath("/app3/app/ap/a");
    try {
        client.delete().forPath("/app3");
    } catch (Exception e) {
        log.error("{}", e.getClass());
        // org.apache.zookeeper.KeeperException$NotEmptyException
    }
    client.delete().deletingChildrenIfNeeded().forPath("/app3");
    // 多次重试删除
    client.delete().guaranteed().forPath("/app2");
    client.create().creatingParentsIfNeeded().forPath("/app");
    // 回调
    client.delete().guaranteed()
            .inBackground((client, event) -> {
                log.warn(event.toString());
                log.warn("为0表示成功: {}", event.getResultCode());
            }) // 回调, 一般guaranteed()函数都会附带回调
            .forPath("/app");
}
```



##Watch事件监听机制

### 概念

Watcher 监听器(不是listener吗😓)

ZooKeeper允许用户在指定节点上注册一些Watcher, 并且在一些特定时间触发的时候,ZooKeeper服务端会将时间通知到感兴趣的客户端上去, 该机制是ZooKeeper实现分布式协调服务的重要特性

Watcher机制实现了发布订阅功能, 能够让多个订阅者同时监听某一对象(节点), 当一个对象自身状态变化时, 会通知所有订阅者

Curator引入了Cache来实现对ZooKeeper服务端时间监听

三种Watcher

-   **`NodeCache`**
    -   对一个特定节点的监听
    -   **已弃用 replace by `CuratorCache`**
-   **`PathChidrenCache`**
    -   监控一个ZNode的子节点
-   **`TreeCache`**
    -   监听一个节点和这个节点的所有子节点

问: 孙子怎么说?

### NodeCache

```java
@Test
public void testNodeCache() throws Exception {
    String nodePath = "/app1";
    NodeCache nodeCache = new NodeCache(client, nodePath, false);
    // dataIsCompressed: 数据是否压缩
    // 压缩了还要解压缩, 麻烦
    // 获取可用的监听器
    Listenable<NodeCacheListener> listenable = nodeCache.getListenable();

    // 加入监听器
    listenable.addListener(() -> {
        ChildData currentData = nodeCache.getCurrentData();
        if (currentData == null) {
            System.out.println("[" + nodePath + "]被删除");
            return;
        }

        String path = currentData.getPath();
        System.out.println("[" + path + "]" + "变化了");

        byte[] data = currentData.getData();
        if (data == null) {
            data = new byte[0];
        }

        System.out.println("data = `" + new String(data) + "`");

        Stat stat = currentData.getStat();
        System.out.println("version = " + stat.getVersion());
    });

    // 开启监听器, 如果设为true, 则开启监听是加载缓存数据
    nodeCache.start(true);
    while (true) {
        Thread.sleep(50);
    }

}
```

### PathChildrenCache

如果删除了父节点, 马上会被监听器补上父节点

孙子不会触发该监听器

```java
@Test
public void testPathChildrenCache() throws Exception {
    String nodePath = "/app1";
    PathChildrenCache pathChildrenCache = new PathChildrenCache(client, nodePath, true);
    // cacheData: 数据是否缓存

    // 获取可用的监听器
    Listenable<PathChildrenCacheListener> listenable = pathChildrenCache.getListenable();

    // 加入监听器
    listenable.addListener((client,event) -> {
        printType(event.getType());
        ChildData childData = event.getData();
        if (childData == null) {
            System.out.println("?"); // 重新连接时触发
            return;
        }
        String path = childData.getPath();
        System.out.println("[" + path + "]" + "变化了");

        byte[] data = childData.getData();
        if (data == null) {
            data = new byte[0];
        }
        System.out.println("data = `" + new String(data) + "`");

        Stat stat = childData.getStat();
        System.out.println("version = " + stat.getVersion());
    });

    // 开启监听器, 如果设为true, 则开启监听是加载缓存数据
    pathChildrenCache.start(true);
    while (true) {
        Thread.sleep(50);
    }

}

private static void printType(PathChildrenCacheEvent.Type type) {
    switch (type){
        case CONNECTION_RECONNECTED:
            System.out.println("重新链接");
            break;
        case CHILD_ADDED:
            System.out.println("新增子节点");
            break;
        case CHILD_UPDATED:
            System.out.println("子节点更新");
            break;
        case CHILD_REMOVED:
            System.out.println("子节点被删除");
            break;
        case CONNECTION_SUSPENDED:
            System.out.println("连接被暂停?");
            break;
        case CONNECTION_LOST:
            System.out.println("连接丢失?");
            break;
        case INITIALIZED:
            System.out.println("初始化?");
            break;
    }
}
```



### TreeCache

孙子啥的都能监听

```java
public void testTreeCache() throws Exception {
    String nodePath = "/app";
    TreeCache treeCache = new TreeCache(client, nodePath);
    // cacheData: 数据是否缓存

    // 获取可用的监听器
    Listenable<TreeCacheListener> listenable = treeCache.getListenable();

    // 加入监听器
    listenable.addListener((client,event) -> {
        printType(event.getType());
        ChildData oldData = event.getOldData();
        ChildData childData = event.getData();
        if (childData == null&&oldData==null) {
            System.out.println("childData==null&oldData==null?"); // 初始化时触发
            return;
        }else if (childData == null) {
            System.out.println("childData==null?"); // 一直不被触发
            printData(oldData);
            return;
        }else if (oldData==null){
            // 总被触发
            System.out.println("oldData==null?");
            printData(childData);
            return;
        }
        // 也就是说, oldData一直为null?
        printData(oldData);
        printData(childData);
    });

    // 开启监听器
    treeCache.start();
    while (true) {
        Thread.sleep(50);
    }

}

private static void printData(ChildData childData) {
    System.out.println("[" + childData.getPath() + "]" + "变化了");
    byte[] data = childData.getData() == null ? new byte[0] : childData.getData();
    System.out.println("data = `" + new String(data) + "`");
    System.out.println("version = " + childData.getStat().getVersion());
}

private static void printType(TreeCacheEvent.Type type) {
    switch (type){
        case CONNECTION_RECONNECTED:
            System.out.println("重新链接");
            break;
        case NODE_ADDED:
            System.out.println("新增节点");
            break;
        case NODE_UPDATED:
            System.out.println("节点更新");
            break;
        case NODE_REMOVED:
            System.out.println("节点被删除");
            break;
        case CONNECTION_SUSPENDED:
            System.out.println("连接被暂停?");
            break;
        case CONNECTION_LOST:
            System.out.println("连接丢失?");
            break;
        case INITIALIZED:
            System.out.println("初始化?");
            break;
    }
}
```



##分布式锁

Redis锁的不可靠性: Master节点挂掉之后, 其子节点可能让好多人都获取到锁

Redis锁的性能比Zookeeper更好

### Zookeeper分布式锁原理

>   当客户端要获得锁时, 创建节点; 使用完锁, 释放节点

1.  各个客户端获取锁时, 都会在Lock节点下创建**临时顺序**节点, 也就创建了多把锁

    -   临时保证了Java客户端在宕机之后依旧能释放

2.  所有客户端都会获得所有的锁, 如果发现自己创建的子节点最小, 那么就认为该客户端获取到了锁

    该客户端使用完锁之后将该节点删除

3.  如果发现自己创建的锁不是最小的, 说明自己还没获取到锁

4.  此时客户端需要找到那个比自己小的节点, 同时对其注册事件监听器, 监听删除事件

    -   2找1
    -   3找2

5.  监听到删除事件, 再次判断自己创建的节点是否是lock子节点中序号最小的

    -   如果是, 就获取到了锁
    -   如果不是, 则重复以上步骤就获取比自己小的一个节点并注册监听

### API

-   `InterProcessSemphoreMutex` 分布式排他锁
-   `InterProcessMutex` 分布式可重入排他锁
-   `InterProcessReadWriteLock` 分布式读写锁
-   `InterProcessMultiLock` 将多个锁作为单个实体管理的容器
-   `InterProcessSemaphoreV2` 共享信号量



```java
package com.harvey.dubbo.api;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryOneTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-04-07 21:08
 */
public class ZookeeperLockTest {
    public static final int POOL_NUM = 10;
    private static final ExecutorService POOL = Executors.newFixedThreadPool(POOL_NUM);
    static class Command implements Runnable {
        private int value = 100;
        private final InterProcessMutex lock;
        public Command(CuratorFramework client){
            lock  = new InterProcessMutex(client,"/lock");
        }
        public final List<String> list = new ArrayList<>();
        @Override
        public void run() {
            while (true) {
                // 加锁
                try {
                    lock.acquire(3, TimeUnit.SECONDS); // 等待锁三秒钟, 三秒再获取不到锁就再试
                    if (value > 0){
                        list.add(Thread.currentThread() + ":" + value);
                        value--;
                    }else {
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("业务错误: " + e.getMessage());
                } finally {
                    // 释放锁
                    try {
                        lock.release();
                    } catch (Exception e) {
                        System.err.println("释放错误: " + e);
                    }
                }

            }
        }
    };

    public static void main(String[] args) {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("centos:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(new RetryOneTime(2 * 1000))
                .namespace("test")
                .build();
        client.start();
        Command runnable = new Command(client);
        for (int i = 0; i < POOL_NUM; i++) {
            POOL.execute(runnable);
        }
        try {
            // 日志打印太多, 无奈出此下策
            Thread.sleep(10000);
            for (String s : runnable.list) {
                System.out.println(s);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

```

完成时间1.737s

