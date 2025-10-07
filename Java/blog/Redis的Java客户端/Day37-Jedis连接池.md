# 创建连接池

```java
class JedisConnectionFactory{
    private static final JedisPool JEDIS_POOL;
    static {
        //配置连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(8);//最大连接数,最多允许几个连接
        poolConfig.setMaxIdle(8);//最大空闲连接
        poolConfig.setMinIdle(1);//最小空闲连接,一直没人访问,里面的内容就会被释放,直到为1
        poolConfig.setMaxWaitMillis(1000);//等待时间,参数为-1时表示无限制等待

        //创建连接池对象
        JEDIS_POOL = new JedisPool(
                poolConfig,
                "0.0.0.0",//主机名?不是主机IP吗?
                6379,
                1000//超时时间
                //,"123456"//密码
        );


    }
    public static Jedis getJedis(){
        return JEDIS_POOL.getResource();
    }
}
```
## 此时的Jedis创建语句应该这么写

```java
@Before
public void testGetJedis(){
    jedis = JedisConnectionFactory.getJedis();
    LOGGER.info("Connect Succeed.");
}

//下面这个还是要写的哟 !
@After
public void tearDown() {
    if (jedis != null) {
        jedis.close();
    }
    LOGGER.info("Close Succeed");
}
```