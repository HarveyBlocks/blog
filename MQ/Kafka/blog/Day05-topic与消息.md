# Kafka基本操作

## topic

Kafka的消息都基于topic

![image-20240408133524862](../assetss/Day06-topic/image-20240408133524862.png)



kafka的消息保存在topic中

### 创建

```shell
kafka-topics.sh --create --bootstrap-server 创建topic的地址 ---topic topic名
```

```shell
kafka-topics.sh --create --bootstrap-server localhost:9092 --topic test
```

```
Created topic test.
```



### 查看创建的topic

```shell
kafka-topics.sh --list --bootstrap-server localhost:9092
```

```
test
```





## 消息

### 创建生产者

利用kafka内置的测试程序, 生产一些消息到kafka的test topic中

```shell
kafka-console-producer.sh --broker-list localhost:9092 --topic test1
```

创建了生产者, 进入控制台

-   `--bootstrap-server HOST:PORT`是一个服务器, 发送消息的目标对象
-   `--boker-list`可以填多组服务器, 发送消息的目标群体



### 创建消费者

```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test
```

监听一个生产者的消息

```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
```

`--from-beginning`从头拉取, 缺省的话, 是监听自打开console之后的消息

### 发送消息

-   生产者

    ![image-20240408135745449](../assetss/Day05-topic%E4%B8%8E%E6%B6%88%E6%81%AF/image-20240408135745449.png)

-   消费者

    缺省`--from-beginning`

    ![image-20240408135836011](../assetss/Day05-topic%E4%B8%8E%E6%B6%88%E6%81%AF/image-20240408135836011.png)

    `--from-beginning`

    ![image-20240408140112296](../assetss/Day05-topic%E4%B8%8E%E6%B6%88%E6%81%AF/image-20240408140112296.png)

## Kafka tools

可以连接kafka集群, 查看里面的一些数据

