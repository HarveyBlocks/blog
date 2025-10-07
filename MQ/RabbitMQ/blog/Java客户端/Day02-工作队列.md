# 工作队列

>   WorkQueue 任务模型

-   让多个消费者绑定到一个队列, 共同消费队列中的消息

## 平均投递

1.  创建队列`work.queue`
2.  1秒产生50条消息
3.  多个消费者监听`work.queue`

###消费能力一致

每条消息只会被消费一次(我使用了五个消费者,更具说服力)

轮询消费

```text
5:count0
2:count1
4:count2
1:count3
3:count4
5:count5
2:count6
4:count7
1:count8
3:count9
5:count10
2:count11
4:count12
1:count13
3:count14
5:count15
2:count16
4:count17
1:count18
3:count19
5:count20
2:count21
4:count22
1:count23
3:count24
5:count25
2:count26
4:count27
1:count28
3:count29
5:count30
2:count31
4:count32
1:count33
3:count34
5:count35
2:count36
4:count37
1:count38
3:count39
5:count40
2:count41
4:count42
1:count43
3:count44
5:count45
2:count46
4:count47
1:count48
3:count49
```

52413......

### 消费能力不一致

俩消费者, 消费者1每秒处理50条, 消费者2每秒处理5条

```text
2:count0
1:count1
1:count3
1:count5
1:count7
2:count2
1:count9
1:count11
1:count13
2:count4
1:count15
1:count17
1:count19
2:count6
1:count21
1:count23
1:count25
2:count8
1:count27
1:count29
1:count31
1:count33
2:count10
1:count35
1:count37
1:count39
2:count12
1:count41
1:count43
1:count45
2:count14
1:count47
1:count49
2:count16
2:count18
2:count20
2:count22
2:count24
2:count26
2:count28
2:count30
2:count32
2:count34
2:count36
2:count38
2:count40
2:count42
2:count44
2:count46
2:count48
```

-   最终还是一人一半





**默认情况下, RabbitMQ会把所有的消息一次轮询投递给绑定在队伍上的每一位消费者** 

不会考虑消费者是否依据处理完消息, 可能出现消息堆积



###修改配置

```yaml
spring:
  rabbitmq:
    listener:
      simple:
        prefetch: 1
```

每次只能获取一条消息, 处理完才能获取下一次消息

```
1:count0
2:count1
1:count2
1:count3
1:count4
1:count5
1:count6
1:count7
1:count8
2:count9
1:count10
1:count11
1:count12
1:count13
1:count14
1:count15
1:count16
1:count17
2:count18
1:count19
1:count20
1:count21
1:count22
1:count23
1:count24
1:count25
2:count26
1:count27
1:count28
1:count29
1:count30
1:count31
1:count32
1:count33
2:count34
1:count35
1:count36
1:count37
1:count38
1:count39
1:count40
1:count41
2:count42
1:count43
1:count44
1:count45
1:count46
1:count47
1:count48
1:count49
```

-   没有堆积
-   能者多劳



