# 整合Redis

[Redis整合](..\..\blog\Redis的Java客户端\Day37-Jedis简介.md)

## 步骤

1.  搭建SpringBoot工程

    ![image-20231206135642864](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-boot/整合/Day03-整合Redis/image-20231206135642864.png)

2.  引入redis起步依赖

    ![image-20231206135846568](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-boot/整合/Day03-整合Redis/image-20231206135846568.png)

3.  配置redis相关属性

    -   本机的帮你自动配置

    -   记得先把服务器开起来哟

    -   application.yml里配置

        ```yaml
        spring:
          redis:
            host: 127.0.0.1
            port: 6379
        ```

    ![image-20231206141746043](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-boot/整合/Day03-整合Redis/image-20231206141746043.png)

4.  注入RedisTemplate模板

    ```java
    @Autowired
    private RedisTemplate redisTemplate;
    ```

5.  编写测试方法 , 测试

    ```java
    import org.springframework.data.redis.core.RedisTemplate;

    @SpringBootTest
    class BootRedisApplicationTests {
        @Autowired
        private RedisTemplate redisTemplate;

        @Test
        void testSet() {
            //存入一个数据
            redisTemplate.boundValueOps("name").set("张三");
        }

        @Test
        void testGet() {
            //存入一个数据
            Object name = redisTemplate.boundValueOps("name").get();
            System.out.println(name);
        }
    }
    ```

