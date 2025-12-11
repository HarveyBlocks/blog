# 哨兵

>   Sentinel

slave宕机之后, 只要再次连接master, 就能恢复数据

但是master宕机之后, 系统就只能做读操作, 不能做写操作

因此需要一个哨兵监控Redis的各个节点的健康状态, 

当发现Master宕机的哪一刻, 立刻选一个Slave成为新的Mater

![image-20240210192209884](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Redis&Cache/分布式缓存/Day06-哨兵机制/image-20240210192209884.png)

## 作用

1.  监控

    监控每一个节点的健康状态

2.  故障自动恢复

    如果master故障, Sentinel会将一个Slave提升为master, 老master恢复后会做Slave

3.  通知

    例如通知Java客户端, 不要再向老的Master发送请求了, 要向新的Mater发送请求

## 原理

### 监控

-   基于**心跳机制**检测服务状态, 每隔1s向集群的每个实例发送**ping命令**
-   主观下线
    -   如果某sentinel节点发现某实例未在规定时间响应
    -   如果是Sentinel这边的网络阻塞导致无响应, 依旧有可能导致对Redis下线的误判
-   客观下线
    -   如果超过指定数量(`quorum`) 的sentinel都认为改实例主观下线
    -   配置的`quorum`最好超过Sentinel的一半

### 选举

1.  判断Slave节点和Master节点断开时间的长短

    -   如果超过指定值(`down-after-milliseconds` \* 10)

        意味着和原master相异的数据太多

        则排除该slave节点(失去选举权)

2.  判断`slave-priority`值

    -   **越小优先级越高**
    -   默认优先级为1
    -   为0则用不选举
    -   一般配置的时候, 性能好的, 设置的值小些

3.  `slave-priority`一样, 判断slave的`offset`

    -   越大说明数据越新, 优先级越高

4.  判断Slave节点的运行ID(`run ID`)大小

    -   是Slave刚启动时, Redis分配的一个ID
    -   分配的值不重要, 走到这一步基本可以看作随便让一个Slave做Master
    -   越小选举的优先级越高

### 故障转移

1.  向选举出的Slave发送命令`SlaveOf no one`, Slave执行该命令, 不再为Slave,成为master

2.  向其他Slave节点发送广播, 告诉他们新的Master是谁, 要执行`SlaveOf HOST PORT`命令

    -   这时候不是百分百要做一个全量同步吗?

        而且这时候做的全量同步不是会有大部分数据是本来就有的数据吗? 现在做这个全量同步不是要清空原有的已经同步的数据, 再次同步一致的数据吗? 这不是做无用功吗?

        不能让新的Master继承原来那个Master的`Replid`吗

        欸! 巧了, 这个`Replid`真的会继承, 所以这个新的Master和这几个Slave(他的老伙伴们)使用的是一样的`Replid`

        所以不会去做这个无用功

        嗨嗨嗨

    -   还有一件事......是不是还要告诉Sentinel这个集群里每个节点的密码啊?

        不告诉Sentinel, 他不久没办法让其他Slave换主了吗???

    -   网上查了说: 一个集群里好像只能用同一个密码qwq,否则会出问题

3.  更改原来Master的配置文件, 让他成为新的Master的Slave

    -   经测试, Docker的话会将原来的Master标记为Slave, 自动的, 但不会改配置文件好像

## 搭建哨兵集群

```bash
docker run \
	-p 6380:6379\
	-p 27001:27001\
	-p 27002:27002\
	-p 27003:27003\
    --name redis-6380\
    --privileged=true\
    -v /etc/docker/redis-6380/conf:/etc/redis\
    -v /etc/docker/redis-6380/data:/var/lib/redis\
    -v /etc/docker/redis-6380/logs:/logs\
    --network=redises\
    --restart=always\
    -d redis:latest\
    redis-server /etc/redis/redis.config
```

### Sentinel配置

`sentinel.conf`

```ini
# 哨兵实例端口,要不一样
port 27001
# 启用解析主机名
sentinel resolve-hostnames yes
# 哨兵实例Host
sentinel announce-ip ????????
# 哨兵监控的集群, 集群名自己取, 需要用主节点的信息来代指集群信息, 然后依据Master得到其Slave的信息
# QUORUM, 客观下线的数量
sentinel monitor myMasterName HOST PORT QUORUM
# 主节点密码
sentinel auth-pass myMasterName 123456
# Slave和Master断开的最长超时时间,默认五秒
sentinel down-after-milliseconds myMasterName 5000
# Slave故障恢复的超时时间, 默认一分钟
sentinel failover-timeout myMasterName 60000
# 工作目录
dir "/etc/redis/s1"
```

```
port 27001
sentinel resolve-hostnames yes
sentinel announce-ip "redis-6380"
sentinel monitor redis-6379 redis-6379 6379 2
sentinel auth-pass redis-6379 123456
sentinel down-after-milliseconds redis-6379 5000
sentinel failover-timeout redis-6379 60000
dir "/etc/redis/s1"
```

### 启动Sentinel

```bash
redis-sentinel s1/sentinel.conf
```

日志

集群信息

```log
195:X 10 Feb 2024 13:16:56.988 # Sentinel ID is e66f5df29ae7144702e01eb8e23d2572a8b5c365
```

集群信息(主节点信息)

```log
195:X 10 Feb 2024 13:16:56.988 # +monitor master redis-6379 172.19.0.2 6379 quorum 2
```

从节点信息

```log
195:X 10 Feb 2024 13:16:56.991 * +slave slave 172.19.0.5:6379 172.19.0.5 6379 @ redis-6379 172.19.0.2 6379
195:X 10 Feb 2024 13:16:56.996 * +slave slave 172.19.0.4:6379 172.19.0.4 6379 @ redis-6379 172.19.0.2 6379
```

哨兵集群中其他节点信息

```log
195:X 10 Feb 2024 13:17:07.962 * +sentinel sentinel 2484169a9e64362d602b0c118a6c37837103c5b2 172.19.0.4 27002 @ redis-6379 172.19.0.2 6379
195:X 10 Feb 2024 13:17:12.761 * +sentinel sentinel eddc6d04c20bf1f798f072419fd4b024bf9d7322 172.19.0.4 27003 @ redis-6379 172.19.0.2 6379
```

## 测试

日志

关闭主节点之后,`sdown`,主观认为下线了

```log
195:X 10 Feb 2024 13:17:30.818 # +sdown master redis-6379 172.19.0.2 6379
```

客观认为下线了

```log
200:X 10 Feb 2024 13:17:30.901 # +odown master redis-6379 172.19.0.2 6379 #quorum 3/2
```

三个哨兵投票选举一个去做故障转移(故障转移只需要一个哨兵就够了)

谁先发现宕机谁就能先选上

```log
195:X 10 Feb 2024 13:17:30.919 # +vote-for-leader 2484169a9e64362d602b0c118a6c37837103c5b2 1
```

故障转移

```log
200:X 10 Feb 2024 13:17:30.998 # +failover-state-select-slave master redis-6379 172.19.0.2 6379
200:X 10 Feb 2024 13:17:31.064 # +selected-slave slave 172.19.0.5:6379 172.19.0.5 6379 @ redis-6379 172.19.0.2 6379
200:X 10 Feb 2024 13:17:31.064 * +failover-state-send-slaveof-noone slave 172.19.0.5:6379 172.19.0.5 6379 @ redis-6379 172.19.0.2 6379
200:X 10 Feb 2024 13:17:31.127 * +failover-state-wait-promotion slave 172.19.0.5:6379 172.19.0.5 6379 @ redis-6379 172.19.0.2 6379
```

广播

```log
200:X 10 Feb 2024 13:17:31.382 * +slave-reconf-sent slave 172.19.0.4:6379 172.19.0.4 6379 @ redis-6379 172.19.0.2 6379
```

故障转移, 易主完成了

```log
195:X 10 Feb 2024 13:17:31.385 # +switch-master redis-6379 172.19.0.2 6379 172.19.0.5 6379
```

故障转移, 原先集群中的Slave要换Master

```bash
200:X 10 Feb 2024 13:17:32.422 * +slave slave 172.19.0.4:6379 172.19.0.4 6379 @ redis-6379 172.19.0.5 6379
```

原来的节点修复之后,成为了slave(这一步还挺花时间的)

```bash
205:X 10 Feb 2024 13:42:24.461 * +fix-slave-config slave 172.19.0.5:6379 172.19.0.5 6379 @ redis-6379 172.19.0.2 6379
```

## RedisTemplate的哨兵模式

>   Redis客户端对哨兵的通知的接收处理

Spring 的RedisTemplate底层使用的Lettuce实现了节点的感知和自动切换(通过pool)

### 配置哨兵模式

1.  配置文件

    ```yaml
    spring:
      redis:
        sentinel:
          master: redis-6379 # 也就是MyMasterName
          nodes:
            - redis:27001
            - redis:27002
            - redis:27003
    #    host: redis
    #    port: 6379
    #    password: 123456
        lettuce:
          pool:
            max-active: 10
            max-idle: 10
            min-idle: 1
            time-between-eviction-runs: 10s
    ```

    配置的是哨兵的地址, 而不需要具体的节点的地址

2.  配置类

    ```java
    /**
     * Redis集群的配置,配置读写分离
     * ReadFrom.MASTER 从主节点读
     * ReadFrom.MASTER_PREFERRED 优先从Master节点读,Master不可用从Slave读
     * ReadFrom.REPLICA 从Slave读
     * ReadFrom.REPLICA_PREFERRED 优先从Slave读,Slave都不可用从Master读
     */
    @Bean
    public LettuceClientConfigurationBuilderCustomizer clientConfigurationBuilderCustomizer() 
    {
        return configBuilder->configBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }
    ```

产生问题: Redis的Sentinel会将Host解析, 如果结合Docker就会产生问题:

Docker的网络中, 如若使用容器名做为 Host配置到`sentinel.conf`,那么这个容器名就会被解析

例如`redis-6379`将被解析为`127.19.0.2`

(当然可以配置不被解析, 这样的话,Sentinel甚至不能找到Master节点,别说用Java客户端了)

在Java客户端使用了`spring.redis.sentinel.nodes: ...`的配置, 

连接到sentinel的nodes之后, 将会返回解析后的Redis节点的Host和Post信息给Java客户端这些信息

可是`127.19.0.2`是虚拟的, 所以Java客户端无法连接到真实的Redis实例

当然如果在dev环境下, 只要在同一docker网络下, 就无关什么端口映射, 这个docker的虚拟的Host也是能用的, 就能解决这一问题

但是Local环境下要怎么办?qwq

