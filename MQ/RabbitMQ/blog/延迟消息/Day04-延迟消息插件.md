# 延迟消息插件

-   原来好好的让你处理过期消息的机制, 被你拿来玩延时😓
-   死信交换机太麻烦了啦

## 安装延迟消息插件

```bash
docker inspect mq
```

![image-20240114120911576](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/延迟消息/Day04-延迟消息插件/image-20240114120911576.png)

执行命令,启动插件

```bash
docker exec -it mq rabbitmq-plugins enable rabbitmq_delayed_message_exchange
```

![image-20240114121318904](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/延迟消息/Day04-延迟消息插件/image-20240114121318904.png)

## 使用延迟消息插件

《一种支持延迟消息功能的交换机》

我的意见是, 不如死信交换机, 死信交换机好歹是坚守了死信交换机的原则的

### 创建延迟交换机

注解的方式

```java
@Exchange(name = "delay.exchange",delayed = "true"),
```

```java
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "delay.queue"),
        exchange = @Exchange(name = "delay.exchange",
                type = ExchangeTypes.DIRECT,
                delayed = "true"),
        key = "delay"
))
public void delayListener(String msg) {}
```

Bean 的方式

```java
@Bean
public DirectExchange directExchange(){
    return ExchangeBuilder.directExchange("delay.exchange")
            .delayed()// 设置delay的属性为true
            .durable(true)// 持久化
            .build();
}
```

### 发送延迟消息

```java
String message = "message";
String delayExchange = "delay.exchange";
String routingKey = "delay";
rabbitTemplate.convertAndSend(delayExchange,routingKey,message,(msg)->{
    msg.getMessageProperties().setDelay(5000);
    return msg;
});
```

## 测试运行

![image-20240114125856807](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/延迟消息/Day04-延迟消息插件/image-20240114125856807.png)

![image-20240114125925064](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/延迟消息/Day04-延迟消息插件/image-20240114125925064.png)

## 缺陷

Redis以外的定时功能都会有性能损耗

Spring和Rabbit这种需要维护一个时钟, 每一个定时都要维护一个时钟. 对精度的要求越高, 对CPU的资源占用越高

如果延迟时间很长(如一天, 一小时),就很容易造成延迟任务的堆积

所以MQ的延时只适合延迟时间短的

-   关于Redis的定时功能的损耗为何小的思考

    -   Redis的TTL损耗小, 是由其业务功能决定的

    -   Redis的是TTL,也就是过期时间. 而Rabbit是延迟时间delay其之间存在区别

    -   Redis使用TTL,判断一个键是否过期,它不需要时刻去问CPU,现在几点了?

        只需要在用户去问Redis: 这个key还在吗?的时候,把当前时间, 注册key的时间, 以及TTL做一个对比即可

    -   所以Redis对CPU的损耗是一下子的, 不需要"维护"时钟

    -   那么这种说法存在漏洞: 

        -   如果是这样子的话, 如果我不访问key,我只是一个劲地加TTL为1s的key, 那么内存应该总有一天被爆满才对, 因为只要我不去查, Key就不会去和当前时间去检验,key就不会过期被删除
        -   也就是说, 不通过命令去查询key,而是观察内存的话, 是可以发现key没有被删除的才对
        -   然后, 在内存饱满的情况下,查询key,可能会出现, 明明内存爆满了,但是一个key也查不出来的情况,然后一查,内存刷的一下就被清空了

    所以我只能去网上查

    ```
    Of course this is not enough as there are expired keys that will never be accessed again. These keys should be expired anyway, so periodically Redis tests a few keys at random among keys with an expire set. All the keys that are already expired are deleted from the keyspace.

    Specifically this is what Redis does 10 times per second:

    Test 20 random keys from the set of keys with an associated expire.
    Delete all the keys found expired.
    If more than 25% of keys were expired, start again from step 1.
    ```

    所以还是要时不时地让CPU搞一下qwq,但是这样压力也不大就是了(在不高并发地增加大量有长时间TTL的key的情况下啦)

-   所以与其听我废话, 不如去看[官网](https://redis.io/commands/expire/)

