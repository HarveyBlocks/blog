# 主从集群

-   master
-   slave(5.0以前)/replica(5.0以后)
-   读写分离
-   数据一致性

## 声明主从关系

-   暂时的

    ```bash
    redis-6380:0>slaveOf redis 6379
    "OK"
    ```

    -   `redis`是docker中的容器名当作IP
    -   `replicaOf` 和`slaveOf`是一样的

    ```bash
    redis-6380:0>replicaOf 127.0.0.1 6379
    "OK"
    ```

-   永久的(修改配置文件)

    ```ini
    # 声明永久的主从关系
    slaveOf redis 6379
    # 主机密码
    masterAuth 123456
    ```

-   查看主从关系

    ```bash
    redis-6380:0>info replication
    "# Replication\
    role:master\
    connected_slaves:0\
    master_failover_state:no-failover\
    master_replid:440a7905787d577ee3468435a6206b195b1fb686\
    master_replid2:0000000000000000000000000000000000000000\
    master_repl_offset:0\
    second_repl_offset:-1\
    repl_backlog_active:0\
    repl_backlog_size:1048576\
    repl_backlog_first_byte_offset:0\
    repl_backlog_histlen:0\
    "
    ```

    



## 创建Redis(6.2.6)集群







```shell
mkdir /etc/docker/redis-6381
mkdir /etc/docker/redis-6381/conf
touch /etc/docker/redis-6381/conf/redis.config
vim /etc/docker/redis-6381/conf/redis.config
```



```ini
# 解除本地限制
bind 0.0.0.0

# 配置密码
requirepass 123456

# 是否压缩RDB文件
# 不建议开启,压缩会消耗CPU,磁盘容量不值钱
rdbcompression no

# RDB文件名
dbfilename dump.rdb

# 文件保存的路径目录
dir /var/lib/redis

# 声明redis实例的 IP地址
replica-announce-ip redis-6381

# 声明永久的主从关系
slaveOf redis 6379
# 主机密码
masterAuth 123456
```









```shell
docker run \
	-p 6381:6379\
    --name redis-6381\
    --privileged=true\ # 挂载的数据卷是否有权限
    -v /etc/docker/redis-6381/conf/redis.config:/etc/redis/redis.config\
    -v /etc/docker/redis-6381/data:/var/lib/redis\
    -v /etc/docker/redis-6381/logs:/logs\
    --network=redises\
    --restart=always\
    -d redis:latest\
    redis-server /etc/redis/redis.config
```

[`./redis.config`](../redis.conf)

↑这个不好

↓这个好

```shell
docker run \
	-p 6381:6379\
    --name redis-6381\
    --privileged=true\
    -v /etc/docker/redis-6381/conf:/etc/redis\
    -v /etc/docker/redis-6381/data:/var/lib/redis\
    -v /etc/docker/redis-6381/logs:/logs\
    --network=redises\
    --restart=always\
    -d redis:latest\
    redis-server /etc/redis/redis.config
```



| HOST  | PORT | ROLE   |
| ----- | ---- | ------ |
| redis | 6379 | Master |
| redis | 6380 | Slave  |
| redis | 6381 | Slave  |



## 测试读写



```bash
redis-6380:0>get key
"value"
redis-6380:0>del key
"READONLY You can't write against a read only replica."
```

-   从机就失去了写的权力

## 数据同步原理



### 全量同步

>   主从的**第一次同步**是全量同步

1.  从节点向主节点请求数据同步
2.  发现是第一次连接主机, 主节点需要给从机**所有主机的数据**
3.  复制数据依靠的是RDB, 主节点执行`bgsave`, 生成RDB文件, 然后将**RDB文件发送给从节点**
4.  从节点**清空本地数据**, 加载RDB文件
5.  在主节点上`bgSave`执行时需要时间, 期间有其他的数据时, Redis将会把**命令**保存在`repl_baklog`的缓冲区
6.  然后主节点将`repl_baklog`的**命令**发送给从节点, 从节点执行发送过来的`repl_baklog`命令, 完成数据同步

-   `bgsave`动作慢, 效率低, 只适合在第一次同步的时候使用





### Redis判断是否是第一次同步数据的方法

#### Replication ID

>   用于判断从节点Offset是否是对应本台主节点的

-   简称`replid`
-   是数据集的标记
-   id一致则说明是同一数据集
-   每一个master都有唯一的replid
-   slave会继承master节点的replid
-   从节点在请求时发送给主节点的replid
    -   主节点会**尝试**用从节点发来的`replid`做*增量同步*
    -   不是主节点的reolid, 则认为是第一次同步, 增量同步失败, 选择使用全量同步
    -   否则依据Offset做增量同步





#### Offset

>   用于判断是否是最新数据, 从哪儿更新

-   偏移量
-   随着记录在`repl_baklog`中的数据增多而逐渐增大
-   slave完成同步时也会记录当前同步的offset
-   如果**slave的offset小于master的offset,** 说明**slave数据落后于master**, 需要更新

### 增量同步

>   slave重启后做的数据同步

1.  依据replid确认Redis不是第一次同步

2.  依据从节点的Offset和主节点的Offset从`repl_baklog`获取从节点缺失的数据

3.  将从节点缺失的数据发送给从节点, 然后从节点执行`repl_baklog`的命令完成同步

4.  `repl_baklog`的存储方式是**环形的**, 是有上限的, 当未同步的数据超出上限(**超过上一次从节点记录的Offset**), **增量同步将会失败**, 将**转为全量同步**

    ![image-20240210185241834](../../assets/Day05-%E4%B8%BB%E4%BB%8E%E9%9B%86%E7%BE%A4/image-20240210185241834.png)



## 主从同步优化

全量同步效率低, 从这方面考虑

-   减少全量同步的可能
-   提高全量同步的效率

### 无磁盘复制

配置: 启用无磁盘复制

```ini
# sync 同步
repl-diskless-sync yes
```

避免全量同步时的磁盘IO

此配置开启之后, 将不再生成同步用的RDB文件, 而是**直接使用网络传输数据**

适合使用在磁盘读写慢但是网络带宽很快的情况下

### 单节点内存上限

Dev环境下10G, 20G就差不多了

```ini
# 设置Redis最大占用内存大小为1G
maxmemory 1gb
```

### 提高`repl_baklog`的大小

```ini
repl-backlog-size 10mb
```



### 其他

-   发现slave宕机尽快实现故障修复, 尽可能避免全量同步

-   限制一个master上的slave数量, 如果太多slave, 可以采用`主-从-从`链式结构, 减少master压力

    ![image-20240210190538330](../../assets/Day05-%E4%B8%BB%E4%BB%8E%E9%9B%86%E7%BE%A4/image-20240210190538330.png)

