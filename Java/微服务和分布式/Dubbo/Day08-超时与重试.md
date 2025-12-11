# 超时与重复

## 超时

服务消费者在调用服务提供者的时候发生阻塞, 等待的情形, 这个时候, 服务消费者会一直等待下去

在某个峰值时刻, 大量的请求都在同时请求服务消费者, 会造成线程的大量堆积, 势必会造成**雪崩**

dubbo利用超时机制来解决这个问题, 设置一个超时时间, 在这个事件段内, 无法完成服务访问, 则**自动断开连接**

使用`timeout`属性配置超时事件, 默认值1000, 单位ms

使用`retries`属性配置重试事件, 默认值2, 第一次+两次重试(在SpringBoot的版本中默认0次)

服务方配置:

```java
@com.alibaba.dubbo.config.annotation.Service(timeout = 3000, retries = 1) // 重试
public class HelloServiceImpl implements HelloService {

    @Override
    public User sayHello(String name) {
        return new User(name);
    }
}
```

连不上, 消费方就报错

消费方也可以配置

可以单独配置, 都可以生效

也可以同时配置, 重试和超时的优先级都是消费者方的高

```java
public class HelloController {

    @com.alibaba.dubbo.config.annotation.Reference(timeout = 1000,retries = 2)
    private HelloService helloService;

    @ApiOperation("测试")
    @GetMapping("/{name}")
    public Result<User> sayHello(
            @PathVariable("name") String name) {
        User user = helloService.sayHello(name);
        return new Result<>(user);
    }
}
```

那么配置到哪一方呢? 两边可能是不同的人写的, 当然是两边都写咯

服务的提供者要配置的时间尽量长一点

## 重试

防止网络抖动

