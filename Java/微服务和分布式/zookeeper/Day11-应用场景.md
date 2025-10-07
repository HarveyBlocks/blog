# 应用场景

## 配置中心

-   存储系统通用全局配置, 集群中所有机器共享配置信息

1.  将配置信息存在zk中的一个节点
2.  给该节点注册一个数据节点变更的watcher的监听
3.  一旦节点数据发生变更, 所有的 订阅该节点的客户端都可以获取数据变更通知

## 负载均衡

对于zk, 

1.  创建services
2.  serverices子节点为各个服务器节点, 存放各种信息
3.  根据服务器列表, 选择负载均衡算法
4.  新增服务节点, 通过监听, 监听到服务器列表增加, 就可以更新一个新的负载均衡算法
5.  ? 怎么做一个负载均衡算法的更行?监听?然后呢? 能监听的东西千千万, 凭什么用zk?

## 命名服务

-   被命名的实体通常可以是集群中的节点信息
-   命名服务即通过一个资源引用的方式来实现对资源的定位和使用
-   上层应用仅仅需要一个全局名称?????????????????←什么屁话?↑
-   使用Key-value的形式, 定位节点资源(好没用的功能)

对于zk

1.  zk的顺序创建, 就能有一个id, `node-00000001`





## DNS服务

域名配置

本地Hsot

开发阶段但需要随时修改域名和IP映射

集群比较大的时候, 伤一发而动全身到处该就很烦

1.  节点名-应用名

    -   子节点`域名`
    -   IP
    -   端口

2.  域名解析时, 首先从zk域名节点但中获取域名映射的IP和端口

3.  每个应用在注册自己的域名, IP, 端口等信息, 并监听保存所有服务节点域名信息的父节. 

4.  点域名变更时, 监听到数据变更, zk会像所有订阅的客户端发送域名变更的通知

    ```java
    @Resource
    privite DruidDataScource dataSource;
    
    public void reconn(){
        String username = userMapper.getUserNameById(1); // 使用连接到的数据库
    	
        (/*锁*/){
            dataSource.restart(); // 重启的一个API
    
            // 如果执行restart后，有程序执行了mapper，则下方会报错.
            // 在重启连接的时候需要确认不会进行其他操作(锁)
            String username = userMapper.getUserNameById(1);
    
    
            //由于上方执行了mapper，导致dataSource被init，所以这里会报错；详情见源码
            //执行set时必须在dataSource刚restart后才行(此时还没有被init)
            dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/nobase?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true");
        }
    
    }
    ```

    

    

## 集群管理

-   集群控制(注册中心)
-   集群监控(心跳?zk是否存在这种功能)
-   节点上下限(用Agent在上下线的时候在zk上增删节点)







## 分布式锁

略





















