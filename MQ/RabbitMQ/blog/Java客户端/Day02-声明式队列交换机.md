# 声明队列交换机

>   想要用代码生成队列和交换机🙁

## 手敲队列交换机的坏处

1.  容易敲错
2.  来回看又累又慢



##声明

重复创建好像也没什么问题

### 基于对象

#### 有关类

-   `Queue`

    -   可以new
    -   可以用`QueueBuilder`

-   `Exchange`

    -   是接口

        <img src="../../assert/Day02-%E5%A3%B0%E6%98%8E%E5%BC%8F%E9%98%9F%E5%88%97%E4%BA%A4%E6%8D%A2%E6%9C%BA/image-20240112210804877.png" alt="image-20240112210804877" style="zoom:50%;" />

    -   可以用`ExchangeBuilder`

-   `Binding`

    -   可以new(看过了, 不如`BindingBuilder`的可读性)
    -   可以用`BindingBuilder`

####创建

```java
package com.itheima.consumer.config;// 在消费者这里

@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("hmall.direct");
    }
    @Bean
    public Queue directQueue1(){
        Queue queueName = QueueBuilder.durable("queueName").build();
        //durable,持久化
        return new Queue("direct,queue1",true/*durable默认为true,可省略*/);
    }
    @Bean
    public Binding directBindingQueue1Error(Queue directQueue1){
        return BindingBuilder.bind(directQueue1)
                .to(directExchange())
                // 你在这里调用了方法,new出来的对象不是和Bean不一样吗?
                // 然而并不是这样. 此方法被Spring 代理了,若调用了这个方法, 
            	// Spring会先检查有没有这个Bean;
                .with("Error");
    }
}
```

```java
@Configuration
public class RabbitConfig {

    @Bean
    public Object aaa(){
        return new Object();
    }
    @Bean
    public String string(Object aaa){
        System.out.println(aaa.equals(aaa()));// false
        return aaa.toString();
    }
}
```

翻了源码, 确实代理了, 也会去判断, 但是没有被认定是存在aaa的confusing

![image-20240112214832017](../../assert/Day02-%E5%A3%B0%E6%98%8E%E5%BC%8F%E9%98%9F%E5%88%97%E4%BA%A4%E6%8D%A2%E6%9C%BA/image-20240112214832017.png)

往深里翻了翻源码, 虽然在beanDefinitionMap和singletonObjects里有aaa, 但实际上Spring是依据`ref`里存在的Bean来判断是否存在bean的, 而`ref=null`(啊??????????)



为false了????在使用测试类做单元测试的时候, 返回的是true



### 基于注解

####有关注解

```java
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "topic.queue0"),
        exchange = @Exchange(name = "hmall.topic0", type = ExchangeTypes.TOPIC),
        key = {"#.news", "China.#"}
))
public void listenTopicQueue0(String msg) {
    System.out.print("\t2:" + msg);
}
```