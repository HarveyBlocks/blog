## Topic交换机

>   Topic 话题

![Topic Exchange illustration, which is all explained in the following text.](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-Topic交换机/python-five.png)

多个单词, 以`.`分隔

![image-20240112203043906](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-Topic交换机/image-20240112203043906.png)

## 通配符

-   `#` 代指0个或多个单词
-   `*`代指1个单词

### 规则指定

-   每一个**Queue**都要Exchange设置一个**Bindingkey**

    <img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MQ/RabbitMQ/Java客户端/Day02-Topic交换机/image-20240112203757111.png" alt="image-20240112203757111" style="zoom:50%;" />

-   **发布者**发送消息时, **指定消息的RoutingKey**

    ```java
    @Test
    void testTopicExchange() {
        String exchangeName = "hmall.topic";
        rabbitTemplate.convertAndSend(exchangeName, "China","China");
        rabbitTemplate.convertAndSend(exchangeName, "weather","weather");
        rabbitTemplate.convertAndSend(exchangeName, "China.weather","China.weather");
        rabbitTemplate.convertAndSend(exchangeName, "British.weather","British.weather");
        rabbitTemplate.convertAndSend(
            exchangeName, "China.Shanghai.weather","China.Shanghai.weather");
        rabbitTemplate.convertAndSend(
            exchangeName, "British.London.weather","British.London.weather");
    }
    ```

-   Exchange将消息路由到**BindingKey**与**消息Rounting**一致的队列

    ```
    	2:China	1:China.weather	2:China.weather	1:British.weather	2:China.Shanghai.weather
    ```

Topic最灵活, 遇事不决Topic

