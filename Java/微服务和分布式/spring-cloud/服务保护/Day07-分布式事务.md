# 分布式事务

-   一个业务需要多个服务合作完成
-   每一个(有必要每一个吗?不是存在就行了吗)服务都有事务
-   多个事务必须同时成功或失败
-   其中, 每个服务的事务就是一个**分支事务**, 整个事务称为**全局事务**

## 需求

-   创建订单和清理购物车, 扣减库存, 应该同时成功, 同时失败
-   但是不在同一个服务,这要咋搞?

```java
@Transactional
public Long createOrder(OrderFormDTO orderFormDTO) {
    // 1.订单数据

    // 1.1.查询商品

    // 2.保存订单详情
		// 事务
    // 3.清理购物车商品
		// 事务
    // 4.扣减库存
		// 事务
    return order.getId();
}
```

-   涉及商品,购物车,订单, 交易服务

## 一致性协议

[事务的ACID特性](..\..\..\..\MySQL\Blog\SQL基础\事务\Day06-事务的特性.md)

事务需要跨多个分布式节点时,  要遵守ACID, 就需要选举一个协调者来协调分布式各个节点的调度

基于这种思想衍生出了一致性协议



### 2PC二阶段提交

#### 流程

1.  提交事务请求
    1.  参与者将各自的SQL交给协调者, 协调者进行SQL执行(意义不明)
    2.  协调者向参与者发送SQL内容, 询问是否可以执行SQL事务操作, 并等待参与者节点的反馈
    3.  各参与者执行SQL事务操作
    4.  将SQL的执行结果返回协调者, 若存在参与者没有将结果返回协调者, 进入3
    5.  所有执行结果成功, 进入2, 否则, 进入3
2.  事务提交
    1.  协调者向参与者发送事务提交(commit)请求
    2.  参与者接收到请求开始进行提交
    3.  参与者将提交结果上报协调者, 所有事务提交完毕, 协调者也提交事务
    4.  完成事务提交
3.  中断事务
    1.  协调者发送回滚请求
    2.  各个参与者节点回滚事务
    3.  协调者接收各参与者节点的回滚结果
    4.  协调者接收到所有节点的回滚结果后, 协调者也回滚事务
    5.  完成回滚

#### 缺点

-   同步阻塞
-   单点问题
    -   协调者出现问题就不能保证数据一致
-   脑裂导致的数据不一致
    -   若参与者与协调者失去联系, 参与者就会重新选取Leader
    -   此刻, 一个分布式事务却有两个协调者, 各自掌握着这个事务是否提交的生杀大权
    -   一个协调者的事务全部能ACK,全提交了,  一个不能, 全回滚了

### 三阶段提交

引入超时时间, 解决同步阻塞

参与者超时时间后自行提交事物, 解决单点问题

#### 流程

1.  `CanCommit`

    1.  协调者向参与者发起请求, 能否进行事务操作
    2.  算是协调者和参与者之间的网络协调测试

2.  `PreCommit`

    1.  协调者向所有参与者节点发送`preCommit`请求

        执行事务预提交 -> 

        1.  各参与者阶段接收到请求后执行SQL事务操作
        2.  参与者反馈给协调者事务执行结果

        中断事务 -> 

        1.  任意一个参与者阶段反馈给协调者响应No/超时, 协调者中断事务
        2.  协调者向各个参与者节点发送`abort`请求
        3.  参与者接收到`abort`请求, 或者等待草是时间后, 中断响应

3.  `DoCommit`

    事务提交

    1.  协调者向所有参与者发送`doCommit`请求
    2.  各参与者接收到`doCommit`请求后, 执行事务提交操作
    3.  各参与者完成事务提交结果后, 向发送者反馈ACK, 完成事务

    事务

    1.  各参与者接收到`abort`请求后, 执行事务回滚操作
    2.  各参与者完成事务回滚后, 向发送者反馈ACK, 完成事务回滚

####







## 分布式事务框架

### Seata

>   蚂蚁金服和阿里巴巴共同开源的分布式事务解决方案

[Apache Seata™](https://seata.io/)

#### 原理





![img](../../assets/Day07-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1/solution-1bdadb80e54074aa3088372c17f0244b.png)



##### 思想

找个管理员盯着

-   **`TC `** 事务的协调者
    -   通知每一个服务回滚

##### 角色

<img src="../../assert/Day07-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1/TB1hSpccIVl614jSZKPXXaGjpXa-1330-924.png" alt="Overview of a global transaction" style="zoom:67%;" />

-   TC    `Transaction Coordinator`
    -   事务协调者
    -   维护全局和分支事务的状态
-   TM    `Transaction Manager`
    -   事务管理器
    -   定义全局事务的范围
    -   开始全局事务
    -   提交或回滚事务
-   RM    `Resource Manager`
    -   资源管理器
    -   管理分支事务
    -   与TC交谈以注册分支事务和报告分支事务的状态

![image-20240116163219427](../../assets/Day07-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1/image-20240116163219427.png)

## Seata的使用

### 部署TC服务

#### 基于数据库存储Seata数据

Seata在管理事务的时候需要记录每一个分支事务的状态和全局事务的状态

-   `seata` 库
    -   `global_table` 表
        -   记录全局事务
    -   `branch_table` 表
        -   记录分支事务
    -   `lock_table` 表
        -   实现锁功能
    -   `distributed_lock` 表
        -   实现分布式锁功能

```sql
CREATE DATABASE IF NOT EXISTS `seata` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `seata`;

------------------------------- The script used when storeMode is 'db' --------------------------------
-- the table to store GlobalSession data
CREATE TABLE IF NOT EXISTS `global_table`
(
    `xid`                       VARCHAR(128) NOT NULL,
    `transaction_id`            BIGINT,
    `status`                    TINYINT      NOT NULL,
    `application_id`            VARCHAR(32),
    `transaction_service_group` VARCHAR(32),
    `transaction_name`          VARCHAR(128),
    `timeout`                   INT,
    `begin_time`                BIGINT,
    `application_data`          VARCHAR(2000),
    `gmt_create`                DATETIME,
    `gmt_modified`              DATETIME,
    PRIMARY KEY (`xid`),
    KEY `idx_status_gmt_modified` (`status` , `gmt_modified`),
    KEY `idx_transaction_id` (`transaction_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store BranchSession data
CREATE TABLE IF NOT EXISTS `branch_table`
(
    `branch_id`         BIGINT       NOT NULL,
    `xid`               VARCHAR(128) NOT NULL,
    `transaction_id`    BIGINT,
    `resource_group_id` VARCHAR(32),
    `resource_id`       VARCHAR(256),
    `branch_type`       VARCHAR(8),
    `status`            TINYINT,
    `client_id`         VARCHAR(64),
    `application_data`  VARCHAR(2000),
    `gmt_create`        DATETIME(6),
    `gmt_modified`      DATETIME(6),
    PRIMARY KEY (`branch_id`),
    KEY `idx_xid` (`xid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store lock data
CREATE TABLE IF NOT EXISTS `lock_table`
(
    `row_key`        VARCHAR(128) NOT NULL,
    `xid`            VARCHAR(128),
    `transaction_id` BIGINT,
    `branch_id`      BIGINT       NOT NULL,
    `resource_id`    VARCHAR(256),
    `table_name`     VARCHAR(32),
    `pk`             VARCHAR(36),
    `status`         TINYINT      NOT NULL DEFAULT '0' COMMENT '0:locked ,1:rollbacking',
    `gmt_create`     DATETIME,
    `gmt_modified`   DATETIME,
    PRIMARY KEY (`row_key`),
    KEY `idx_status` (`status`),
    KEY `idx_branch_id` (`branch_id`),
    KEY `idx_xid_and_branch_id` (`xid` , `branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `distributed_lock`
(
    `lock_key`       CHAR(20) NOT NULL,
    `lock_value`     VARCHAR(20) NOT NULL,
    `expire`         BIGINT,
    primary key (`lock_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('AsyncCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryRollbacking', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('TxTimeoutCheck', ' ', 0);
```



#### 准备配置文件

```yaml
server:
  port: 7099

spring:
  application:
    name: seata-server

logging:
  config: classpath:logback-spring.xml
  file:
    path: ${user.home}/logs/seata
  # extend:
  #   logstash-appender:
  #     destination: 127.0.0.1:4560
  #   kafka-appender:
  #     bootstrap-servers: 127.0.0.1:9092
  #     topic: logback_to_logstash

console:
  user:
    username: admin
    password: admin

seata:
  config:
    # support: nacos, consul, apollo, zk, etcd3
    type: nacos
    nacos:
      server-addr: nacos:8848 # 基于容器名访问,应该和Nacos容器在同一个网络当中
      group : "DEFAULT_GROUP"
      namespace: ""
      dataId: "seataServer.properties"
      username: "nacos"
      password: "nacos"
  registry:
    # support: nacos, eureka, redis, zk, consul, etcd3, sofa
    type: nacos
    nacos:
      application: seata-server
      server-addr: nacos:8848
      group : "DEFAULT_GROUP"
      namespace: ""  # 默认
      username: "nacos"
      password: "nacos"
#  server:
#    service-port: 8091 #If not configured, the default is '${server.port} + 1000'
  security:
    secretKey: SeataSecretKey0c382ef121d778043159209298fd40bf3850a017
    tokenValidityInMilliseconds: 1800000
    ignore:
      urls: /,/**/*.css,/**/*.js,/**/*.html,/**/*.map,/**/*.svg,/**/*.png,/**/*.ico,/console-fe/public/**,/api/v1/auth/login
  server:
    # service-port: 8091 #If not configured, the default is '${server.port} + 1000'
    max-commit-retry-timeout: -1
    max-rollback-retry-timeout: -1
    rollback-retry-timeout-unlock-enable: false
    enable-check-auth: true
    enable-parallel-request-handle: true
    retry-dead-threshold: 130000
    xaer-nota-retry-timeout: 60000
    enableParallelRequestHandle: true
    recovery:
      committing-retry-period: 1000
      async-committing-retry-period: 1000
      rollbacking-retry-period: 1000
      timeout-retry-period: 1000
    undo:
      log-save-days: 7
      log-delete-period: 86400000
    session:
      branch-async-queue-size: 5000 #branch async remove queue size
      enable-branch-async-remove: false #enable to asynchronous remove branchSession
  store:
    # support: file 、 db 、 redis
    mode: db
    session:
      mode: db
    lock:
      mode: db
    db:
      datasource: druid
      db-type: mysql
      driver-class-name: com.mysql.jdbc.Driver
      url: jdbc:mysql://mysql:3306/seata?rewriteBatchedStatements=true 
      # 这里的"mysql"也使用了容器名代替IP地址
      user: root
      password: 123
      min-conn: 10
      max-conn: 100
      global-table: global_table
      branch-table: branch_table
      lock-table: lock_table
      distributed-lock-table: distributed_lock
      query-limit: 1000
      max-wait: 5000
    redis: # 上面配置了db, 下面的配置不会被启用
      mode: single
      database: 0
      min-conn: 10
      max-conn: 100
      password:
      max-total: 100
      query-limit: 1000
      single:
        host: redis # redis的地址
        port: 6379
  metrics:
    enabled: false
    registry-type: compact
    exporter-list: prometheus
    exporter-prometheus-port: 9898
  transport:
    rpc-tc-request-timeout: 15000
    enable-tc-server-batch-send-response: false
    shutdown:
      wait: 3
    thread-factory:
      boss-thread-prefix: NettyBoss
      worker-thread-prefix: NettyServerNIOWorker
      boss-thread-size: 1
```

#### 在Docker上部署

-   拷贝seata目录到虚拟机的/root目录
-   拷贝seata.tar文件于**统一目录**



-   启动镜像

    ```bash
    docker load -i seata.tar
    ```

-   创建容器

    ```bash
    docker run --name seata \
    -p 8099:8099 \
    -p 7099:7099 \
    -e SEATA_IP=自己虚拟机的IP地址 \
    -v ./seata:/seata-server/resources \
    --privileged=true \
    --network hmall \
    -d \
    seataio/seata-server:1.5.2
    ```

    -   `-v ./seata:/seata-server/resources \`把当前seata目录挂载到resources目录下使配置文件生效

    -   `--privileged=true`文件读取权限

    -   `--network hmall`一开始我就没有hmall网络(大意了,一定要在一个网下)

        ```bash
        docker network create hmall
        ```

        ```bash
        docker network connect hmall seata
        ```

-   启动成功

    ```txt
    ███████╗███████╗ █████╗ ████████╗ █████╗
    ██╔════╝██╔════╝██╔══██╗╚══██╔══╝██╔══██╗
    ███████╗█████╗  ███████║   ██║   ███████║
    ╚════██║██╔══╝  ██╔══██║   ██║   ██╔══██║
    ███████║███████╗██║  ██║   ██║   ██║  ██║
    ╚══════╝╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝
    ```

    2.2.2版本没有登录模仿(听不清)

    ![image-20240116172633874](../../assets/Day07-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1/image-20240116172633874.png)
    
    用户名`admin`密码`admin`登录网页控制台

### 微服务集成Seata

#### 引入依赖

```xml
<!--seata-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
</dependency>
```

#### 配置

-   配置到Nacos中, 避免重复配置

```yml
seata:
  registry: # 注册中心的配置, 让微服务根据这些配置找到TC服务地址
    type: nacos # 注册中心类型
    nacos:
      server-addr: centos
      namespace: ""
      group: DEFAULT_GROUP
      application: seata-server # seata服务名称
#      username: nacos
#      password: nacos 要去掉?why?上面不去这边去?
  tx-service-group: hmall # 事务组名, 自己取
  service:
    vgroup-mapping: # 事务组与tc集群的映射关系
      hmall: default # 这里的hmall取决与上面的配置,default是集群名称
```



## 事务模式

[Seata XA 模式](https://seata.io/zh-cn/docs/dev/mode/xa-mode)

[Seata AT 模式](https://seata.io/zh-cn/docs/dev/mode/at-mode)

[Seata TCC 模式](https://seata.io/zh-cn/docs/dev/mode/tcc-mode)

[Seata SAGA 模式](https://seata.io/zh-cn/docs/dev/mode/saga-mode)

### XA模式

-   X/Open组织定义的分布式事务处理(DTP, Distributed Transaction Processing)标准
-   XA规范描述了全局的TM与局部的RM之间的接口,
-   几乎所有的主流数据库都对XA规范提供了支持
-   也被称作两阶段提交

#### 流程

1.  TM向TC声明开启全局事务
2.  调用分支微服务一, 二
3.  分支微服务一, 二向TC注册
4.  执行微服务一, 二业务(sql语句)
    -   执行后==不提交, 处于悬挂状态==
    -   事务的资源被锁定
5.  微服务一, 二向TC报告自己的执行状态
6.  当所有业务执行完毕, TM向TC发出结束全局事务的请求
7.  TC检查之前微服务一, 二报告给自己的执行状态
    -   全部执行成功, 提交
    -   存在执行失败, 回滚

#### 优缺点

-   优点
    -   强一致性, 满足ACID
    -   大多数数据库提供支持, 实现简单, 没有代码入侵
-   缺点
    -   效率低, 业务由于是串行, 前一个干不完, 后一个别想干
    -   依赖于关系型数据库

#### 启用XA模式

1.  修改配置

    ```yaml
    seata:
      data-source-proxy-mode: XA
    ```

2.  给发起全局事务的入口方法添加`@GlobalTransactional`注解,

    ```java
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        // 1.订单数据
    
        // 1.1.查询商品
    
        // 2.保存订单详情
    		// 事务
        // 3.清理购物车商品
    		// 事务
        // 4.扣减库存
    		// 事务
        return order.getId();
    }
    ```

3.  其余的加上`@Transational`注解

#### 测试运行

![](../../assets/Day07-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1/image-20240116222538583.png)

成功地失败了

### AT模式

-   针对XA模式下**"效率低, 业务由于是串行, 前一个干不完, 后一个别想干"**的缺点而出现的模式
-   是Seata主推的模式



#### 流程

1.  TM向TC声明开启全局事务
2.  调用分支微服务一, 二
3.  分支微服务一, 二向TC注册
4.  执行微服务一, 二业务(sql语句)
    -   **记录更新数据库前的(涉及的数据的)json快照(日志)**
    -   执行后==提交==
    -   事务的资源==不会被锁定==<=性能增强
5.  微服务一, 二向TC报告自己的执行状态
6.  当所有业务执行完毕, TM向TC发出结束全局事务的请求
7.  TC检查之前微服务一, 二报告给自己的执行状态
    -   全部执行成功, ==删除快照(日志)==
    -   存在执行失败, 用==快照恢复数据==

#### 优缺点

-   优点
  
    -   效率高, 事务资源不会被锁定
-   缺点
    -   **可能出现中间不一致的状态**
        -   在数据提交到所有数据执行完毕之间的状态下
        -   由于数据已经提交
        -   其他线程能乘虚而入来做CRUD
        -   此非强一致, 是==最终一致==,不符合事务的ICID

        ```
        2-(-1,记录日志2)-(1--+---------------+-(0--失败--恢复日志2
        					↓				↑
        					\--(-1---成功----/
        ```

        \=\=>商家看见6,认为没人买;插入的用户买了1,花了钱,却没有扣减库存

        不过这个例子, 完整的业务要在相同的业务的间隙里完成, 不太可能吧?

#### 启用AT模式

1.  准备用于存储中间日志的表`undo_log`

    **每一个微服务都有可能用到事务, 就需要给每个微服务的数据库都准备一个`undo_log`**

    ```sql
    -- for AT mode you must to init this sql for you business database. the seata server not need it.
    CREATE TABLE IF NOT EXISTS `undo_log`
    (
        `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
        `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
        `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
        `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
        `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
        `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
        `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
        UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
    ) ENGINE = InnoDB
      AUTO_INCREMENT = 1
      DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
    ```

2.  修改配置

    ```yaml
    seata:
      data-source-proxy-mode: AT # 可以缺省, 
    ```

2.  给发起全局事务的入口方法添加`@GlobalTransactional`注解,

    ```java
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {...}
    ```

3.  其余的加上`@Transational`注解

#### 测试运行

---


![image-20240116231509942](../../assets/Day07-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1/image-20240116231509942.png)

---


![image-20240116231907335](../../assets/Day07-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1/image-20240116231907335.png)



---



![image-20240116231355051](../../assets/Day07-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1/image-20240116231355051.png)



---



![image-20240116231345817](../../assets/Day07-%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1/image-20240116231345817.png)

==**成功地失败了**==

## 箴言

解决分布式事务的最好方式是不要出现分布式事务

分布式事务总是要造成性能损耗的,即使是AT模式, 在生成快照的时候也是会损耗性能的

不要用分布式框架,用消息队列,发送消息尝试重试



## 如何避免大事务

Mybatis-plus在进行写操作的时候, 往往有一个condition, 利用好这个condition, 用condition作为标记来**人为判断**, 而不是让数据库直接上全局锁

也可以使用where, 先排除一大部分的记录, 然后再进行操作

