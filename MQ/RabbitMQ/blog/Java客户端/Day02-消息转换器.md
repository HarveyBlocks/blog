# 消息转换器

(不启动消费者)

```java
@Test
void testRabbitTemplate() {
    String queueName = "simple.queue";
    Map<String, Object> user = Map.of("name", "Harvey", "age", 20);
    rabbitTemplate.convertAndSend(queueName, user);// 类型不限
}
```

![image-20240112224116222](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-消息转换器/image-20240112224116222.png)

使用了JDK的序列化方式(Redis: 这该死的熟悉感)

## 消息序列化

![image-20240112225409617](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-消息转换器/image-20240112225409617.png)

```java
protected Message convertMessageIfNecessary(final Object object) {
    if (object instanceof Message) {
       return (Message) object;
    }
    return getRequiredMessageConverter().toMessage(object, new MessageProperties());
}
```

-   默认选择的消息转换器

![image-20240112225605070](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-消息转换器/image-20240112225605070.png)

```java
protected Message createMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
    byte[] bytes = null;
    if (object instanceof byte[]) {
        bytes = (byte[])object;
        messageProperties.setContentType("application/octet-stream");
    } else if (object instanceof String) {
        try {
            bytes = ((String)object).getBytes(this.defaultCharset);
        } catch (UnsupportedEncodingException var6) {
            throw new MessageConversionException("");
        }
        ...
    } else if (object instanceof Serializable) {
        try {
            bytes = SerializationUtils.serialize(object);// 实现序列化Object
        } catch (IllegalArgumentException var5) {
            throw new MessageConversionException("", var5);
        }
		...
    }

    if (bytes != null) {
        messageProperties.setContentLength((long)bytes.length);
        return new Message(bytes, messageProperties);
    } else {
        throw new IllegalArgumentException(...);
        // 看来真的上User类的化, 可能还不行了qwq
        // 那他为啥还要用Object作为参数啊?
        // 为啥不把convertAndSect的Object限制成Message,byte[],String,和Serializable啊?
        // 大不了早点抛出异常不行啊? 非得到这么后面抛? 每次大段的异常很反人类的qwq
    }
}
```

原罪

![image-20240112230435325](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-消息转换器/image-20240112230435325.png)

说起来`createMessage`是

![image-20240112230521401](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-消息转换器/image-20240112230521401.png)

所以seialize(Object)使用了ObjectOutputStream(见Redis)

**序列化还存在安全问题**:*因为序列化之后的是字节,  可以往里面填非法的代码, 反序列化之后,然后就会运行起来*

所以.....StringRedisTemplete-StringRabbitTemplete?

## StringRabbitTemplete

### 引入依赖

```xml
<!--Jackson-->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```

### 创建替代默认的MessageConverter的Bean

-   Publisher和Consumer都要有

```java
@Bean
public MessageConverter jackson2JsonMessageConverter(){
    return new Jackson2JsonMessageConverter();
}
```

### 运行测试

#### 发送测试

![image-20240112232458686](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-消息转换器/image-20240112232458686.png)

#### 接收测试

![image-20240113124301003](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-消息转换器/image-20240113124301003.png)

