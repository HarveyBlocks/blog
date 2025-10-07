#`RabbitTemplate` 代替 `OpenFeign`

##引入AMQP依赖

-   应为有SpringMVC了就不引`Jackson`了

```xml
<!--AMQP依赖，包含RabbitMQ-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

## 配置RabbitMQ

```yml
spring:
  rabbitmq:
    host: centos
    port: 5672
    virtual-host: /hmall
    username: hmall
    password: 123456
```

## 编写序列化器

```java
@Configuration
public class MqConfig{
  	@Bean
	public MessageConverter jackson2JsonMessageConverter(){
    	return new Jackson2JsonMessageConverter();
	}  
}
```



## 编写监听器

```java
@Component
public class RabbitMqListener {
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "topic.queue0",durable="true"),
        exchange = @Exchange(name = "hmall.topic0", type = ExchangeTypes.TOPIC),
        key = {"#.XXX.#"}
	))
    public void listenTopicQueue(Object o) {
        // 类型与发送方匹配
        // 接收到消息后的相关逻辑
        System.out.println(user);
    }
}
```

## 编写发送方

```java
// xxxClient.method(o);
try{
    // 这一段异步方式的异常不会导致整个事务的回滚
    rabbitTemplate.convertAndSend("topic.queue0","aaa.xxx",o)
}catch(AmqpException e){
    log.error("method执行失败",e);
}
```

