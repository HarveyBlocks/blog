# 多版本

## 灰度发布

当服务的提供者发生更改(升级), 并部署在其他地址上时

我们应该选择让少量消费者先尝试连接新版本的服务提供者, 

确认无误后然后再把其他消费者去使用这个新版本的服务提供者



## version属性

dubbo使用version属性来设置和调用同一个接口的不同版本



### 生产者

```java
@com.alibaba.dubbo.config.annotation.Service(version = "v1.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public User sayHello(String name){
        return new User("old "+name);
    }
}
```

```java
@com.alibaba.dubbo.config.annotation.Service(version = "v2.0")
public class HelloServiceImpl2 implements HelloService {
    @Override
    public User sayHello(String name){
        return new User("new "+name);
    }
}
```



### 消费者

```java
public class HelloController {

    @com.alibaba.dubbo.config.annotation.Reference(version = "v1.0")
    private HelloService helloService;

    @ApiOperation("测试")
    @GetMapping("/{name}")
    public Result<User> sayHello(
            @PathVariable("name") String name) {
        User user = helloService.sayHello(name);
        log.info(user.toString());
        return new Result<>(user);
    }
}
```

手动重启(悲)

