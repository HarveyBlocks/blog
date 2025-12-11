# 简介

消息队列

## Kafka应用场景

-   异步处理
-   系统解耦
-   流量消峰
    -   消息队列是低延迟, 高可靠, 高吞吐的
-   日志处理

## 生产者-消费者模型

## Kafka

-   开源的流平台
-   由Scala(大部分)和Java编写
-   Apache
-   关键词
    -   Publish & Subscribe 发布订阅
    -   Producers & Consumers 生产者和消费者 
    -   Store 存储
    -   Process 处理
    -   Connectors 连接器可以将数据库中的数据导入到Kafka,也可以将Kafka中的数据导入到数据库
    -   Stream Processors 流处理器, 可以从Kafka拉取数据, 也可以将数据写入Kafka

![image-20240207131536764](../assetss/Kafka%E7%AE%80%E4%BB%8B/image-20240207131536764.png)

### 安装与解压

1.  去官网下安装包

2.  放到虚拟机

3.  解压

    ```shell
    tar -xvzf kafka_2.12-2.4.1.tgz -C ./server/
    ```

4.  修改配置文件`./server/kafka_2.12-2.4.1/config/server.properties`

    每一个kafka节点的broker.id都应该是不一样的

    ```properties
    # The id of the broker. This must be set to a unique integer for each broker.
    broker.id=0
    ```

    修改日志存放地址

    ```properties
    # A comma separated list of directories under which to store log files
    log.dirs=/root/kafka/server/logs
    ```

5.  将安装好的kafka复制到另外两台服务器

    ```bash
    scp -r kafka_2.12-2.4.1/ node2:$PWD
    scp -r kafka_2.12-2.4.1/ node3:$PWD
    ```

6.  启动

    ```shell
    ./kafka-server-start.sh ../config/server.properties
    
    ```

    

### Docker安装

```bash
docker run -d \
	--name kafka2 \
	-p 9094:9092 \
	--link zookeeper \
	--env KAFKA_ZOOKEEPER_CONNECT=192.168.88.130:2181 \
	--env KAFKA_ADVERTISED_HOST_NAME=192.168.88.130 \
	--env KAFKA_ADVERTISED_PORT=9094  \
	--env KAFKA_LOG_DIRS=/kafka/logs \
	--env KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.88.130:9094  \
	--env KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 \
	-v /root/docker-data/kafka2/logs:/kafka  \
	--network zoo \
	--restart always \
	wurstmeister/kafka
```

```
docker run  -d --name kafka -p 9092:9092 -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=192.168.88.130:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.88.130:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t wurstmeister/kafka
```



集群配置

```bash
docker run -d \
	--name kafka \
	-p 9092:9092 \
	--link zookeeper \
	--env KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
	--env KAFKA_ADVERTISED_HOST_NAME=localhost \
	--env KAFKA_ADVERTISED_PORT=9092  \
	--env KAFKA_LOG_DIRS=/kafka/logs \
	--env KAFKA_BROKER_ID=1 \
	--env KAFKA_NUM_PARTITIONS=3 \ # topic的分区数
	--env KAFKA_DEFAULT_REPLICATION_FACTOR=2 \ # 分区的副本数
	-v /root/docker-data/kafka:/kafka  \
	-v /root/docker-data/kafka/run/docker.sock:/var/run/docker.sock \
	--network zoo \
	--restart always \
	wurstmeister/kafka
```

## 命令



```bash
docker exec -it kafka bash
```

```bash
/opt/kafka/bin
ls -l
```

查看命令

查看当前集群id

```shell
kafka-cluster.sh cluster-id -b localhost:9092 
```

