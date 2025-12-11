# Spring-Data-Redis

SpringData里啥数据库都有, es, sql, nosql

-   整合Lettuce和Jedis客户端
-   提供RedisTemplate同一API来操作Redis
    -   这些API是由Lettuce和Jedis来实现的

## RedisTemolate

提供RedisTemplate同一API来操作Redis

-   这些API是由Lettuce和Jedis来实现的
-   支持Redis的发布订阅模型
-   支持Redis哨兵和Redis集群
-   支持基于Lettuce的响应式编程
-   支持基于JDK, JSON, 字符串, Spring对象的数据序列化和反序列化
-   支持基于Redis的JDKCollection的再实现

### API

>   RedisTemplate中封装了各色对Redis的操作, 并且将不同数据类型的操作API封装到了不同的类型中

![image-20240102130147837](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Day01-Spring-Data-Redis/image-20240102130147837.png)

原来, jedis一个类就相当于一个控制台, jedis里由redis的所有几乎方法, 没有层次感 (?)

SpringData的redisTemplate就相当于给把Jedis分层次了,操作更方便了

