# 数据同步

可以在MySQL进行增删改的时候,让ES也增删改--> **如果MySQL的服务和ES的服务时独立的呢**?

也就是在微服务下, 如何实现两个数据库(Mysql和Es, mysql和redis)

## 方案

### 同步调用

![image-20231229214114839](../assets/Day05-%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5/image-20231229214114839.png)

#### 流程

当有人在mysqll做**新增修改**的操作时

1.  让es的服务暴露CRUD的相关接口
2.  把数据写到mysql数据库里
3.  调用es的对应服务
4.  使用es的服务的**更新**索引库接口

#### 问题

-   业务耦合 ,写完mysql的数据库代码之后, 不得不调用**别的服务**去做和**本服务无关**的操作
-   同步调用的各步操作依次执行, 性能降低
-   其中一步出现异常, 导致全部业务全部执行失败

### 异步通知

![image-20240114160733141](../../../java/%E5%BE%AE%E6%9C%8D%E5%8A%A1%E5%92%8C%E5%88%86%E5%B8%83%E5%BC%8F/assets/Day05-%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5/image-20240114160733141.png)

解除业务的耦合

依赖于MQ的可靠性

实现复杂度上升

### 监听Binlog

MySQL的用于主从同步的Binlog, 每当MySQL做增删改时,都会改变Binlog

利用canal中间件监听Binlog, 发送消息给微服务

![image-20240114161124813](../../../Java/%E5%BE%AE%E6%9C%8D%E5%8A%A1%E5%92%8C%E5%88%86%E5%B8%83%E5%BC%8F/assets/Day05-%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5/image-20240114161124813.png)

完全解除了服务间的耦合

但要开启MySQL的Binlog增加了MySQL的压力

## 利用MQ实现数据同步

### 交换机和队列

#### 分析

>   对于ES来说. 增和改是一致的
>
>   所以消息可以有两类: 增改一类,删一类

topic类型的交换机, 

绑定queue, 一条为hotel.insert.另一条为hotel.delete

#### 引入依赖

```xml
<!--amqp-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

#### 配置

```yml
spring:
  rabbitmq:
    host: centos
    port: 5672
    username: harvey
    password: 123456
    virtual-host: /
```

#### 客户端创建交换机和队列与接收消息

```java
@Component
public class HotelListener {

    @Autowired
    private IHotelService hotelService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = HotelMqConstants.INSERT_QUEUE_NAME),
            exchange = @Exchange(
                name = HotelMqConstants.EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = HotelMqConstants.INSERT_KEY
    ))
    public void listenHotelInsert(Long hotelId){
        // 新增
        hotelService.saveById(hotelId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = HotelMqConstants.DELETE_QUEUE_NAME),
            exchange = @Exchange(
                name = HotelMqConstants.EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = HotelMqConstants.DELETE_KEY
    ))
    public void listenHotelDelete(Long hotelId){
        // 删除
        hotelService.deleteById(hotelId);
    }
}
```



### 发送消息

>   在对数据库做增删改除增加消息发送

#### 引入依赖和配置

一致

#### 发送消息

注入RabbitTemplate

```java
@Autowired
private RabbitTemplate rabbitTemplate;
```

删除消息(Long HotelId)

```java
rabbitTemplate.convertAndSend(
    HotelMqConstants.EXCHANGE_NAME, 
    HotelMqConstants.DELETE_KEY, //routingKey不同
    hotel.getId());
```



更改/增加消息(Long HotelId)

```java
rabbitTemplate.convertAndSend(
    HotelMqConstants.EXCHANGE_NAME, 
    HotelMqConstants.INSERT_KEY, //routingKey不同
    hotel.getId());
```

### 对ES做增删改的逻辑

#### 删

-   根据ID删, easy

    ```java
    // 创建request
    DeleteRequest request = new DeleteRequest(INDEX, hotelId.toString());
    // 发送请求
    restHighLevelClient.delete(request, RequestOptions.DEFAULT);
    ```

#### 增/改

1.  依据ID从数据库查询新数据

    ```java
    // 查询酒店数据，应该基于Feign远程调用hotel-admin，根据id查询酒店数据（现在直接去数据库查）
    Hotel hotel = getById(hotelId);
    ```

2.  转换数据格式, MySQL数据转为ES内数据

    ```java
    // 转换
    HotelDoc hotelDoc = new HotelDoc(hotel);
    ```

3.  将新数据覆盖原先的ES内数据

    ```java
    // 1.创建Request
    IndexRequest request = new IndexRequest(INDEX).id(hotelId.toString());
    // 2.准备参数
    request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
    // 3.发送请求
    restHighLevelClient.index(request, RequestOptions.DEFAULT);
    ```

