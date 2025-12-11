# 负载均衡

## 策略

![image-20240406220436331](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/Dubbo/Day08-负载均衡/image-20240406220436331.png)

### Random(Default)

$$
p_i = \frac{weight_i}{\sum_{i=1}^{n}{weight_i}}
$$

-   默认权重一样,都是100

配置权重

```java
@com.alibaba.dubbo.config.annotation.Service(weight = 100)
public class HelloServiceImpl implements HelloService {
}
```

开启多台机器

<img src="https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/Dubbo/Day08-负载均衡/image-20240406214809791.png" alt="image-20240406214809791" style="zoom: 45%;" />

测试用比例1:10:100

```java
public class HelloController {

    @com.alibaba.dubbo.config.annotation.Reference()
    private HelloService helloService;
    private static final int[] COUNT = {0,0,0};
    @ApiOperation("测试")
    @GetMapping("/{name}")
    public Result<User> sayHello(
            @PathVariable("name") String name) {
        User user = helloService.sayHello("");
        COUNT[user.getUsername().trim().length()] += 1; // 办法总比困难多
        log.info(Arrays.toString(COUNT));
        return new Result<>(user);
    }
}
```

```java
21:55:52:261  INFO 11612 --- [nio-8082-exec-3] com.harvey.dubbo.web.HelloController     : [0, 1, 20]
21:55:52:559  INFO 11612 --- [nio-8082-exec-2] com.harvey.dubbo.web.HelloController     : [0, 1, 21]
21:55:52:883  INFO 11612 --- [nio-8082-exec-4] com.harvey.dubbo.web.HelloController     : [0, 1, 22]
21:55:53:177  INFO 11612 --- [nio-8082-exec-5] com.harvey.dubbo.web.HelloController     : [0, 1, 23]
21:55:53:583  INFO 11612 --- [nio-8082-exec-6] com.harvey.dubbo.web.HelloController     : [0, 1, 24]
21:56:02:210  INFO 11612 --- [nio-8082-exec-9] com.harvey.dubbo.web.HelloController     : [0, 1, 25]
21:56:05:231  INFO 11612 --- [nio-8082-exec-8] com.harvey.dubbo.web.HelloController     : [0, 1, 26]
21:56:07:337  INFO 11612 --- [io-8082-exec-10] com.harvey.dubbo.web.HelloController     : [0, 1, 27]
21:56:07:798  INFO 11612 --- [nio-8082-exec-1] com.harvey.dubbo.web.HelloController     : [0, 1, 28]
21:56:08:188  INFO 11612 --- [nio-8082-exec-3] com.harvey.dubbo.web.HelloController     : [0, 1, 29]

21:56:28:832  INFO 11612 --- [nio-8082-exec-6] com.harvey.dubbo.web.HelloController     : [0, 4, 65]
21:56:29:140  INFO 11612 --- [nio-8082-exec-7] com.harvey.dubbo.web.HelloController     : [0, 4, 66]
21:56:29:457  INFO 11612 --- [nio-8082-exec-9] com.harvey.dubbo.web.HelloController     : [0, 5, 66]
21:56:29:785  INFO 11612 --- [nio-8082-exec-8] com.harvey.dubbo.web.HelloController     : [0, 5, 67]
21:56:30:105  INFO 11612 --- [nio-8082-exec-1] com.harvey.dubbo.web.HelloController     : [0, 5, 68]
21:56:30:453  INFO 11612 --- [io-8082-exec-10] com.harvey.dubbo.web.HelloController     : [1, 5, 68]
21:56:30:764  INFO 11612 --- [nio-8082-exec-3] com.harvey.dubbo.web.HelloController     : [1, 5, 69]
21:56:31:086  INFO 11612 --- [nio-8082-exec-2] com.harvey.dubbo.web.HelloController     : [1, 5, 70]

21:56:40:556  INFO 11612 --- [nio-8082-exec-4] com.harvey.dubbo.web.HelloController     : [2, 9, 93]
21:56:40:946  INFO 11612 --- [nio-8082-exec-2] com.harvey.dubbo.web.HelloController     : [2, 10, 93]
21:56:41:244  INFO 11612 --- [nio-8082-exec-5] com.harvey.dubbo.web.HelloController     : [2, 10, 94]
21:56:41:608  INFO 11612 --- [nio-8082-exec-6] com.harvey.dubbo.web.HelloController     : [2, 11, 94]
21:56:41:954  INFO 11612 --- [nio-8082-exec-7] com.harvey.dubbo.web.HelloController     : [2, 11, 95]
21:56:42:301  INFO 11612 --- [nio-8082-exec-9] com.harvey.dubbo.web.HelloController     : [2, 11, 96]

21:56:45:864  INFO 11612 --- [nio-8082-exec-1] com.harvey.dubbo.web.HelloController     : [2, 12, 105]
21:56:46:214  INFO 11612 --- [io-8082-exec-10] com.harvey.dubbo.web.HelloController     : [2, 12, 106]
21:56:46:572  INFO 11612 --- [nio-8082-exec-3] com.harvey.dubbo.web.HelloController     : [2, 12, 107]
21:56:46:928  INFO 11612 --- [nio-8082-exec-4] com.harvey.dubbo.web.HelloController     : [2, 12, 108]
21:56:47:285  INFO 11612 --- [nio-8082-exec-2] com.harvey.dubbo.web.HelloController     : [2, 12, 109]
21:56:47:655  INFO 11612 --- [nio-8082-exec-5] com.harvey.dubbo.web.HelloController     : [2, 12, 110]
21:56:48:011  INFO 11612 --- [nio-8082-exec-6] com.harvey.dubbo.web.HelloController     : [2, 12, 111]
21:56:48:349  INFO 11612 --- [nio-8082-exec-7] com.harvey.dubbo.web.HelloController     : [2, 12, 112]
21:56:48:682  INFO 11612 --- [nio-8082-exec-9] com.harvey.dubbo.web.HelloController     : [2, 13, 112]
21:56:49:030  INFO 11612 --- [nio-8082-exec-8] com.harvey.dubbo.web.HelloController     : [2, 13, 113]
```

### RoundRobin(轮询)

权重轮询

10:20:30的顺序:

1->2->3->2->3->3

### LeastActive(最小活跃调用次数)

相同活跃次数的随机

**活跃次数**: 记录上一次请求消耗的时间, 找到最快的

### ConsistentHash(一致性Hash)

对于相同参数的请求, 总是发给同一个请求者(好!)

### 选择策略

![image-20240406220512732](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/微服务和分布式/Dubbo/Day08-负载均衡/image-20240406220512732.png)

```java
@com.alibaba.dubbo.config.annotation.Reference(loadbalance = RandomLoadBalance.NAME)
private HelloService helloService;
```

