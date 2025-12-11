# 分布式缓存

## 单点Redis存在的问题

-   基于内存, 服务重启肯会丢失数据
    -   实现Redis数据持久化
-   基于内存, 存储容量有上限
    -   基于分片集群, 利用插槽机制实现动态扩容
-   并发能力有限
    -   搭建主从集群,实现读写分离
-   故障恢复问题
    -   Redis宕机, 则服务不可用
    -   需要哨兵机制, 实现健康检测和自动恢复

## 创建Redis(6.2.6)集群

```shell
docker run \
	-p 6380:6379\
    --name redis-6380\
    -v /etc/docker/redis-6380/conf/redis.config:/etc/redis/redis.config\
    -v /etc/docker/redis-6380/data:/var/lib/redis\
    -v /etc/docker/redis-6380/logs:/logs\
    --privileged=true\
    --restart=always\
    -d redis:latest\
    redis-server /etc/redis/redis.config
```

[`./redis.config`](../redis.conf)

```ini
# 解除本地限制
bind 0.0.0.0

# 配置密码
requirepass 123456
```

下面是一些docker部署普通redis

```bash
docker run \
	-p 6379:6379\
    --name redis\
    -v $PWD/conf/redis.config:/etc/redis/redis.config\
    -v $PWD/data:/var/lib/redis\
    -v $PWD/logs:/logs\
    --privileged=true\
    --restart=always\
    -d redis:latest\
    redis-server ./redis.config
```

```
docker run 
	-p 6379:6379
    --name redis
    -v $PWD/conf/redis.conf:/etc/redis/redis.conf
    -v $PWD/data:/var/lib/redis
    -v $PWD/logs:/logs
    --privileged=true
    --restart=always
    -d redis:latest
    redis-server /etc/redis/redis.conf
```

## 分布式Redis

Redis持久化

Redis主从

Redis哨兵

Redis分片集群

