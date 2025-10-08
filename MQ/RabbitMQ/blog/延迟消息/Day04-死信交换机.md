# 死信

>   Dead Letter

## 概念

-   当队列中的消息满足下列条件之一的, 就会成为死刑
-   消费者使用nasic.reject或basic.nack声明消费失效, 并且消息的requeue参数设置为false
-   消息是一个过期消息(达到了队列或消息本身设置的过期时间), 超时无人消费
-   要投递队列的消息队积满了(被设置了上限), 最早的消息可能成为死信



## 结局

-   死信要么丢失
-   **配置死信交换机**,由其处理死信



## 死信交换机

>   **D**ead **L**etter **Ex**change
>
>   DLX

只是一种称呼, 和其他交换机一样, 只是生产者从原来的客户端换成了队列罢了

### 绑定

队列通过属性`dead-letter-exchange`绑定死信交换机, 那么队列中的死信就会投递到这个交换机中

```properties
x-dead-letter-exchange=交换机名
```

### 用处

利用死信交换机作为中间交换机实现延迟消息

### 实现流程

1.  生产者发送定了过期时间的消息给普通交换机

    -   过期时间指定

        ```java
        Message msg = MessageBuilder.withBody("Message".getBytes())
                .setExpiration("3000")//ms
                .build();
        String queueName = "simple.queue";
        rabbitTemplate.convertAndSend(queueName, msg);
        ```

        或者

        ```java
        rabbitTemplate.convertAndSend("queueName","routingKey","Msg",(msg)->{
            MessageProperties properties = msg.getMessageProperties();
            properties.setExpiration("3000");
            return msg;
        });
        ```

2.  普通交换机路由消息给(指定了死信的)队列

    -   如果没有死信交换机, 队列里的消息就会被消费, 就不能被延时了

    -   指定死信交换机

        ![image-20240114115815113](../../assert/Day04-%E6%AD%BB%E4%BF%A1%E4%BA%A4%E6%8D%A2%E6%9C%BA/image-20240114115815113.png)

3.  等待, 消息过期

4.  过期的消息发送给了死信交换机

5.  过期消息路由给了普通队列

6.  消息给了普通消费者

