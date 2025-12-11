# 消息代理的可靠性

>消息代理宕机--> 会产生消息丢失

-   MQ默认将消息保存在内存之中
    -   降低收发消息的延迟
    -   导致数据无法持久化
    -   内存空间有限, 当消费者故障或处理过慢, 会导致**消息积压**, 引发**MQ阻塞**

## 数据持久化

>   Durable持久化. Transient 暂时



### 交换机持久化

>   Spring默认

![image-20240113151832936](../../assets/Day03-%E6%B6%88%E6%81%AF%E4%BB%A3%E7%90%86%E7%9A%84%E5%8F%AF%E9%9D%A0%E6%80%A7/image-20240113151832936.png)

### 队列持久化

>   Spring默认

![image-20240113151904982](../../assets/Day03-%E6%B6%88%E6%81%AF%E4%BB%A3%E7%90%86%E7%9A%84%E5%8F%AF%E9%9D%A0%E6%80%A7/image-20240113151904982.png)

### 消息持久化

>   Spring默认

-   配置消息持久化**`delivery_mode=2`**

    -   queue

        ![image-20240113152330222](../../assets/Day03-%E6%B6%88%E6%81%AF%E4%BB%A3%E7%90%86%E7%9A%84%E5%8F%AF%E9%9D%A0%E6%80%A7/image-20240113152330222.png)

    -   exchange

        ![image-20240113152141125](../../assets/Day03-%E6%B6%88%E6%81%AF%E4%BB%A3%E7%90%86%E7%9A%84%E5%8F%AF%E9%9D%A0%E6%80%A7/image-20240113152141125.png)

    



### 测试PageOut的场景

>   PageOut, 内存溢出

-   创建非持久化msg

    ```java
    Message msg = MessageBuilder.withBody("Message".getBytes())
            .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
            .build();
    ```

-   狂发

    ```java
    String queueName = "simple.queue";
    for (int i = 0; i < 1000000; i++) {
    	rabbitTemplate.convertAndSend(queueName, msg);
    }
    ```

    

-   测试统计

    持久化与否, 结果是一样的,全部丢完

    他妈的傻逼东西,给爷爬

```java
@Test
void testPageOut() throws InterruptedException {
    Message msg = MessageBuilder.withBody("Message".getBytes())
            .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
            .build();
    String queueName = "simple.queue";
    // 创建非持久化msg
    for (int i = 0; i < 1; i++) {
        rabbitTemplate.convertAndSend(queueName, msg);
    }
}
```

![image-20240113175421939](../../assets/Day03-%E6%B6%88%E6%81%AF%E4%BB%A3%E7%90%86%E7%9A%84%E5%8F%AF%E9%9D%A0%E6%80%A7/image-20240113175421939.png)

```java
@Test
void testPageOut() throws InterruptedException {
    Message msg = MessageBuilder.withBody("Message".getBytes())
            .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
            .build();
    String queueName = "simple.queue";
    // 创建非持久化msg
    for (int i = 0; i < 1; i++) {
        rabbitTemplate.convertAndSend(queueName, msg);
    }

}
```

![image-20240113175551169](../../assets/Day03-%E6%B6%88%E6%81%AF%E4%BB%A3%E7%90%86%E7%9A%84%E5%8F%AF%E9%9D%A0%E6%80%A7/image-20240113175551169.png)



## 改变队列模式

>   Lazy Queue 惰性队列



### 惰性队列

#### 特性

-   接收到消息后直接存入磁盘而非内存
    -   内存中只保留最近的消息, 默认2048条
-   消费者要消费消息时才会从磁盘中读取并加载到内存
-   支持百万级消息存储

3.12版本后所有队列都是LazyQueue,无法更改(没有纯内存模式了qwq)



#### 指定

![image-20240113163404482](../../assets/Day03-%E6%B6%88%E6%81%AF%E4%BB%A3%E7%90%86%E7%9A%84%E5%8F%AF%E9%9D%A0%E6%80%A7/image-20240113163404482.png)

```java
@Bean
public Queue lazyQueue(){
    return QueueBuilder
            .durable("lazy.queue")
            .lazy()// 指定为lazy
            .build();
}
```

```java
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(
            name = "topic.queue0",
            arguments = @Argument(
                name="x-queue-mode",value = "lazy"
            )
        )
))
public void lazyQueue(String msg) {
    System.out.print("\t2:" + msg);
}
```

![image-20240113164315620](../../assets/Day03-%E6%B6%88%E6%81%AF%E4%BB%A3%E7%90%86%E7%9A%84%E5%8F%AF%E9%9D%A0%E6%80%A7/image-20240113164315620.png)

不经过memory直接写硬盘

lazyque性能更好

