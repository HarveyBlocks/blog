# Fanout交换机

>   Fanout 广播

![img](../../assert/Day02-Fanout%E4%BA%A4%E6%8D%A2%E6%9C%BA/python-three-overall.png)

一服务, 一队列

## 创建Fanout交换机

![image-20240112193143733](../../assert/Day02-Fanout%E4%BA%A4%E6%8D%A2%E6%9C%BA/image-20240112193143733.png)

## 发送到消息交换机

```java
@Test
void testFanoutExchange() throws InterruptedException {
    String exchangeName = "hmall.fanout";
    rabbitTemplate.convertAndSend(exchangeName, ""/*routingKey,给null也行*/,"Hello everyone");// 类型不限
}
```

## 测试结果

```
	1:Hello everyone	2:Hello everyone
```

