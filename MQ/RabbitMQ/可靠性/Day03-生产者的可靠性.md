# 生产者的可靠性

## 生产者重连

>   **注意: ** *此乃连接失败的重试, 非消息发送失败的重试. 消息发送失败了依旧不会重试*

-   当网络不稳定的时候, 利用重试机制可以有效提高消息发送的成功率
-   不过SpringAMQP提供的重试机制是==**阻塞式**的重连== 
    
    -   也就是说, 多次重试等待的过程中, **当前线程是被阻塞的, 会影响业务性能**
-   如果对于业务性能有要求可以**禁用**重试机制
-   如果一定要使用, 请**合理配置等待时长和重试次数** 
-   当然也可以考虑使用**异步线程**来执行发送消息的代码

-   可通过配置开启

    ```yml
    spring:
      rabbitmq:
        connection-timeout: 1s
        template:
          retry:
            enabled: true # 开启超时重试机制,默认false
            initial-interval: 1000ms # 失败后的等待时间,默认10000ms
            multiplier: 3
            # 失败后的下次等待时长倍数, 下次等待时长 = initial-interval * multiplier
            # 3 是为了测试明显.实际使用并不合理
            max-attempts: 3 # 最大重试次数
    ```
-   测试

    ```log
    01-13 13:06:10:757  INFO

    01-13 13:06:11:081  INFO
    01-13 13:06:11:257  WARN

    01-13 13:06:12:269  INFO
    01-13 13:06:12:409  WARN

    01-13 13:06:15:413  INFO
    01-13 13:06:15:554  WARN

    org.springframework.amqp.AmqpIOException: java.io.IOException
    ```

​    

## 生产者确认

>   与生产者确认的区别在于, 更注重的是消息发送失败时的应对策略

生产者确认有两种机制

-   `Publisher Confirm`
-   `Publisher Return`  **路由失败**时返回(一般情况下不需要开启, 因为是开发者自己作出来的妖)
-   开启确认机制后, **在MQ成功收到消息后==会返回确认消息==给生产者**

### 确认消息的几种情况

>   建立在MQ不会有内部机制问题的前提下

返回的情况:	

-   消息投递到了MQ, **但是路由失败**
    -   此时会通过`Publisher Return`**返回路由异常原因** 
    -   然后返回 ***ACK*** , 告知投递成功
-   **临时消息**投递到了MQ, 并且**入队成功**
    -   返回 ***ACK*** , 告知投递成功
-   **持久消息**投递到了MQ, 并且入队**且完成了持久化**
    -   返回 ***ACK*** , 告知投递成功
-   否则返回 ***NACK*** , 告知投递失败, 应该重新投递

### 接收回执

两种思路

-   阻塞式地等待MQ回执
-   异步接收MQ回执

```java
spring:
  rabbitmq:
    publisher-returns: true # 开启返回路由失败消息, 默认false
    publisher-confirm-type: correlated  # 确认方式: 异步
```

-   `publisher-confirm-type`

    -   `none`

        关闭confirm机制

    -   `simple`

        同步阻塞式等待MQ的回执消息

    -   `correlated`

        MQ异步回调方式返回回执消息

### 回调函数

-   `ReturnCallback`

    -   每个RabbitTemplate只能配置一个ReturnCallback => 需要在项目启动过程中完成配置

        ```java
        /**
         * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
         * @version 1.0
         * @date 2024-01-12 21:10
         * @see ApplicationContextAware Spring的有关Aware(通讯)的接口,<br>
         * 当Spring初始化完成, Spring调用继承该接口的类, 实现里面的方法
         */
        @Configuration
        public class ReturnCallbackConfig implements ApplicationContextAware {
            private static final Logger LOG = LoggerFactory.getLogger(ReturnCallbackConfig.class);

            /**
             * 用于注册Spring的Bean(当然也可以为所欲为)
             *
             * @param applicationContext 上下文
             */
            @Override
            public void setApplicationContext(ApplicationContext context) 
                BeansException {
                RabbitTemplate template = context.getBean(RabbitTemplate.class);
                template.setReturnsCallback(
                        new RabbitTemplate.ReturnsCallback() {
                            @Override
                            public void returnedMessage(ReturnedMessage returnedMessage) {
                                // 原先的方法被@Deprecated了, returnedMessage封装了消息
                                LOG.info(returnedMessage.toString());
                            }
                        }
                );

            }
        }
        ```

    -   运行测试

        ```
        01-13 14:23:14:347  INFO 18028 --- [nectionFactory2] c.i.p.config.ReturnCallbackConfig        : ReturnedMessage [message=(Body:'{"news":"Sport","name":"China"}' MessageProperties [headers={__ContentTypeId__=java.lang.Object, __KeyTypeId__=java.lang.Object, __TypeId__=java.util.ImmutableCollections$MapN}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, deliveryTag=0]), replyCode=312, replyText=NO_ROUTE, exchange=hmall.topic, routingKey=British.London.weather]
        ```

-   ConfirmCallback

    -   每次发送消息都需要ConfirmCallback=>准备唯一标识

        ```java
        // CorrelationData有一个uuid,是当前消息的唯一标识
        CorrelationData cd = new CorrelationData(); 
        log.info(cd.getId());
        // Future,JDK提供,异步执行结果
        // 刚拿到Future不能获取结果
        // 只有在异步执行成功之后才能在Future中获取结果
        cd.getFuture().addCallback(new ConfirmCallback(log));
        ```

        ```java
        System.out.println(
            new CorrelationData(
                UUID.randomUUID().toString()//JDK提供
            ).getId()
        );
        ```

    -   准备ConfirmCallback

        ```java
        public class ConfirmCallback implements 
            ListenableFutureCallback<CorrelationData.Confirm> {
            public final Logger log;

            public ConfirmCallback(Logger log){
                this.log = log;
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Future发生异常时的处理逻辑,指Spring内部出现问题,基本不会触发",ex);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                if (result==null){
                    log.error("result==null");
                    return;
                }
                if(result.isAck()){
                    log.info("消息发送成功");
                }else {
                    log.error("发送消息失败:{}",result.getReason());
                    // 这里写重发消息的逻辑(要不要递归呢....)
                }

            }
        }
        ```

    -   将ConfirmCallback添加入rabbitTemplate

        ```java
        rabbitTemplate.convertAndSend(exchangeName, "China", msg,cd);
        ```

    -   运行测试

        ```log
        01-13 14:49:32:998  INFO 11912 --- [168.88.130:5672] 
        com.itheima.publisher.QueueTest          : 消息发送成功
        ```

### 弊端

-   生产者确认需要额外的网络和系统资源开销, 尽量不要使用
-   如果一定要使用, 无需开启**`Publisher-Return`**, 因为一般**路由失败**是自己业务问题
-   对于nack消息可以**有限次数重试**, 依然失败则记录异常消息

