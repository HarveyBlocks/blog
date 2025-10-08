# 全局唯一ID

-   UUID
    -   非单调递增
    -   字符串
-   Reis自增
-   数据库自增
    -   是特指专门用一张表用来自增
    -   为了提高性能会批量的获取ID并存储到缓存
-   snowflaske雪花算法
    -   需要维护机器内部时钟
    -   很快

## 自增长ID引发的问题

1.  id的规律性过于明显, 会暴露信息给用户
2.  收单表数据量的限制
    -   订单的数据量很大, 迫切需要分表, 但是自增长在分表里会造成ID重复

## 全局ID生成器

>   一种在**分布式系统**下用来生成全局唯一ID的工具

-   唯一性
-   高可用
-   高性能
-   递增(有利于创建索引)
-   安全性(不简单,规律不能太明显)



## 用Redis做全局ID生成器

1.  独立于数据库, 不存在分表时ID重复的情况
2.  Redis集群, 主从, 哨兵, 解决高可用问题
3.  Redis自身就是高性能的
4.  Redis有Incr命令, 有利于自增

### 设计ID

![image-20240120091229673](../../../assert/Day06-%E5%85%A8%E5%B1%80%E5%94%AF%E4%B8%80ID/image-20240120091229673.png)

-   符号位
    -    `0`表示正数
-   时间戳
    -   以秒为单位, 从某个时间为起点
    -   31位约是69年

## 实现

```java
@Component
public class RedisIdWorker {
    private static final int COUNT_BITS = 32;

    // 基础时间
    public static void main(String[] args) {
        LocalDateTime nowTime = LocalDateTimeUtil.now();

        System.out.println();
    }

    public static final long BEGIN_TIMESTAMP = 1705742462L;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * id生成器
     *
     * @param servicePrefix 用于生成Id的分层, 不同的业务使用不同的分层
     * @return id.
     */
    public long nextId(String servicePrefix) {
        // 生成时间戳

        // 当前时间
        LocalDateTime now = LocalDateTimeUtil.now();
        long nowTimestamp = now.toEpochSecond(ZoneOffset.UTC);
        // 时间差
        long timestamp =  nowTimestamp - BEGIN_TIMESTAMP;


        // 生成序列号,利用Redis的自增长,由于序列号定义了下2^32,一月一key,还有统计效果
        String icrKey = String.format("icr:%s:%d:%02d",
                servicePrefix, now.getYear() % 100, now.getMonth().getValue());
        // 默认加一, 报黄是因为icr不存在, 但Redis会对不存在的key自动创建并加一(之后为1),所以不必担心
        long count = stringRedisTemplate.opsForValue().increment(icrKey);
        // 拼接
        return  timestamp << COUNT_BITS | count ; // 使用或运算
    }

}
```

### 测试

```java
@Autowired
private RedisIdWorker redisIdWorker;
private ExecutorService es = Executors.newFixedThreadPool(500);

@Test
void testId() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(300);
    Runnable task = () -> {
        for (int i = 0; i < 100; i++) {
            ;
            long id = redisIdWorker.nextId("order");
            System.out.println(Thread.currentThread().getName() + ":" + id);
        }
        latch.countDown();
    };
    long begin = System.currentTimeMillis();
    for (int i = 0; i < 300; i++) {
        es.submit(task);
    }
    latch.await();
    long end = System.currentTimeMillis();
    System.out.println(end-begin);
}
```
