# 业务幂等性

确保消息至少被消费一次

同一个消息会被传递多次, 就会重复消费

## 幂等

$$
f(x)=f(f(x))\\
幂等的概念
$$

-   数学中的概念
-   在程序开发中, 指**同一个业务**, **执行一次或多次对业务状态的影响是一致的**
    -   不能在新增一个A之后, 发生异常, 重新消费,  重新新增, 最总导致新增多个A(非常糟糕)

## 幂等业务与非幂等业务

>   所有的业务都应该具有幂等性

-   天生幂等
    -   查询
    -   删除
    -   但是会有效率的影响
-   非幂等
    -   下单, **扣减库存**
    -   退款, **恢复余额**

## 幂等方案

### 唯一ID

#### 流程分析

1.  每一条消息生成一个**唯一ID**
2.  将**唯一ID与消息一起**投递给消费者
3.  消费者接收到消息后处理自己的业务(这个业务具有事务性,能回滚)
4.  业务处理完成后将消息ID保存到数据库
5.  如果下次收到相同的消息, 去数据库检查消息是否存在(警觉), 存在则为重复消息放弃处理

#### 实现

-   **在消息Json中带入唯一标识**

    ```java
    @Bean
    public MessageConverter jacksonMessageConverter(){
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
    }
    ```

    源码

    ```java
    if (this.createMessageIds && messageProperties.getMessageId() == null) {
        messageProperties.setMessageId(UUID.randomUUID().toString());
    }
    ```

    测试

    ![image-20240113204435988](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/可靠性/Day03-业务幂等/image-20240113204435988.png)

-   使用其他唯一ID(自己配置)

    ```java
    rabbitTemplate.convertAndSend(exchangeName, "China", name, (msg)->{
        MessageProperties properties = msg.getMessageProperties();
        properties.setMessageId("雪花处理算法");
        return msg;
    },cd);
    ```

#### 弊端

1.  要拿 id ,存id 等等等等,对业务有侵入
2.  对性能有影响

### 基于业务本身判断

在业务中加一个判断

-   下单, **扣减库存**

    -   先判断是否已下单
    -   已下单不做逻辑

-   退款, **恢复余额**

    -   先判断是否已退款
    -   已退款不做逻辑

-   存在并发安全

-   完成幂等并解决并发安全用LambdaQuery

    ```sql
    update order set status = 2 where id =? and status = 1;
    ```

    乐观锁的思想

