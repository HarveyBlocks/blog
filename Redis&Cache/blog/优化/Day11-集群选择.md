# 集群选择

## 集群完整性问题

集群是否需要插槽全覆盖

如果在集群里存在插槽**不可用**了, 整个集群都将不对外服务

建议配置为false (当然每个节点都要配置qwq)

这样健康插槽照常使用, 宕机的插槽就会阻止连

```properties
# By default Redis Cluster nodes stop accepting queries if they detect there
# is at least a hash slot uncovered (no available node is serving it).
# This way if the cluster is partially down (for example a range of hash slots
# are no longer covered) all the cluster becomes, eventually, unavailable.
# It automatically returns available as soon as all the slots are covered again.
#
# However sometimes you want the subset of the cluster which is working,
# to continue to accept queries for the part of the key space that is still
# covered. In order to do so, just set the cluster-require-full-coverage
# option to no.
#
# cluster-require-full-coverage yes
cluster-require-full-coverage no
```

但是如果存在插槽不可用了, 依旧对外服务, 可能会造成数据的不完整的问题

但为了**可用性**, 我们不能将整个集群停用, 

可以使用熔断或降级人工介入等保证数据完整性



## 集群带宽问题

>   节点之间会不断的互相ping来确定其他节点的状态

每次ping携带的信息包括:

-   插槽信息
-   集群状态信息

集群中节点越多, 集群的状态信息就越大, 10个节点的相关信息可能达到1kb, 

此时每次集群互通需要的带宽就会非常高

### 解决途径

1.  避免大集群
    -   集群节点数最好小于1000
    -   如果业务庞大,建议建立多个集群
2.  避免在单个物理机运行太多Redis实例
3.  配置合适的`cluster-node-timeout`值, 改变节点之间互相ping的频率
    -   间隔太短, 带宽占用就多
    -   间隔太长, 无法接收节点的健康状态, 集群的可用性就越低

## 数据倾斜问题

-   出现BigKey
-   往一个插槽使用批处理

## 客户端性能问题

无论是Jedis还是Lettuce, 对于集群都需要做节点的选择和插槽的判断, 会对客户端性能造成影响

## 命令的集群兼容性问题

批处理命令(`mset`,`mget`)涉及多个插槽引起的无法执行的问题

## lua和事务问题

集群模式下, 不再同一个节点, 就无法保证事务的原子性





## 集群?

单体的Redis(主从Redis+哨兵)已经能达到**万级别的QPS**了, 并且具备很强的高可用特性

如果主从能满足业务需求的情况下, 尽量不搭建Redis集群

