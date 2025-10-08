# 基准测试

1.  创建一个topic: benchmask
2.  同时运行生产者, 消费者基准测试程序
3.  观察结果

```shell
./kafka-topics.sh --zookeeper localhost:2181 --create --topic benchmark --partitions 1 --replication-factor 1
```

replication-factor 1 表示一个分区

## 测试生产者

```shell
./kafka-producer-perf-test.sh --topic benchmark --num-records 500000 --throughput -1 --record-size 1024 --producer-props bootstrap.servers=localhost:9092 acks=1
```

```
bin/kafka-producer-perf-test.sh
--topic topic的名字
--num-records	总共指定生产数据量（默认5000W）
--throughput	指定吞吐量——限流（-1不指定）
--record-size   record数据大小（字节）
--producer-props bootstrap.servers=指定Kafka集群地址，ACK模式

```

测试结果: 

![image-20240408194146415](../assets/Day05-%E5%9F%BA%E5%87%86%E6%B5%8B%E8%AF%95/image-20240408194146415.png)

```log
1126 records sent, 219.6 records/sec (0.21 MB/sec), 1524.1 ms avg latency, 2914.0 ms max latency.
14505 records sent, 2901.0 records/sec (2.83 MB/sec), 5055.5 ms avg latency, 6420.0 ms max latency.
36000 records sent, 7200.0 records/sec (7.03 MB/sec), 5983.3 ms avg latency, 7501.0 ms max latency.
48165 records sent, 9347.0 records/sec (9.13 MB/sec), 3318.2 ms avg latency, 3755.0 ms max latency.
44475 records sent, 8887.9 records/sec (8.68 MB/sec), 3255.1 ms avg latency, 3974.0 ms max latency.
31830 records sent, 6362.2 records/sec (6.21 MB/sec), 4650.1 ms avg latency, 5295.0 ms max latency.
39735 records sent, 7820.3 records/sec (7.64 MB/sec), 3969.0 ms avg latency, 4985.0 ms max latency.
28050 records sent, 5605.5 records/sec (5.47 MB/sec), 4975.9 ms avg latency, 5855.0 ms max latency.
39867 records sent, 7973.4 records/sec (7.79 MB/sec), 4734.1 ms avg latency, 6301.0 ms max latency.
48993 records sent, 9781.0 records/sec (9.55 MB/sec), 3122.6 ms avg latency, 3581.0 ms max latency.
47100 records sent, 9420.0 records/sec (9.20 MB/sec), 3197.8 ms avg latency, 3391.0 ms max latency.
45255 records sent, 8608.5 records/sec (8.41 MB/sec), 3278.4 ms avg latency, 3748.0 ms max latency.
53745 records sent, 10749.0 records/sec (10.50 MB/sec), 3404.0 ms avg latency, 3995.0 ms max latency.
500000 records sent, 7462.129692 records/sec (7.29 MB/sec), 3817.28 ms avg latency, 7501.00 ms max latency, 3434 ms 50th, 6041 ms 95th, 7194 ms 99th, 7379 ms 99.9th.

```

## 测试消费者

```shell
./kafka-consumer-perf-test.sh --broker-list localhost:9092 --topic benchmark --fetch-size 1048576 --messages 500000
```

```shell
bin/kafka-consumer-perf-test.sh
--broker-list 指定kafka集群地址
--topic 指定topic的名称
--fetch-size 每次拉取的数据大小
--messages 总共要消费的消息个数

```

