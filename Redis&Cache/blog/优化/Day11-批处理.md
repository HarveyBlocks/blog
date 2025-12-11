# 批处理优化

>   将数据库里的海量数据导入到Redis

少发请求

一次请求的数据量过多可能导致网络阻塞

## Pipeline

### 作用

大数据量的导入

$$
向Redis发送一次请求并完成批处理的时间=完成一次处理时间和数据量的映射关系(一次数据量)\\
总消耗时间=\lceil\frac{总数据量}{一次数据量}\rceil\times向Redis发送一次请求并完成批处理的时间\\
$$

### Redis原生的批出理命令

`mXXX`命令

```shell
127.0.0.1:6379> mset a 1 b 2 c 3
OK
127.0.0.1:6379> hmset stu name jack age 12 sex male
OK
```

有局限性, 无法进行复杂的批处理, 例如批处理不同的数据类型或批处理不同的Key

### PipeLine的使用

#### Jedis

```java
Pipeline pieline = jedis.pipelined();
// 像使用Jedis一样使用pipeline
// ...
pipline.sync();// 发送请求执行命令,将管道中的数据清空
```



#### RedisTemplate

```java
RedisCallback<Object> objectRedisCallback = connection -> {
    //connection.set(argv)
    return null;// 返回Null就行,别管
};
stringRedisTemplate.executePipelined(objectRedisCallback);
```

### Pipeline和Redis原生命令的区别和选择

Redis的原生命令具有**原子性**

而Pipeline的命令只是一起发送, 到了Redis之后, 不一定具有**原子性**

如果在执行Pipeline时被插队, Pipeline的所有命令执行完毕的耗时就会长一些了

所以PineLine的执行时一般比原生命令要长一些



## 集群模式下的批处理

在集群下, 批处理的多个key必须处于**同一个插槽**内, 否则就会执行失败

![image-20240220162600174](../../assetss/Day11-%E6%89%B9%E5%A4%84%E7%90%86/image-20240220162600174.png)

### 在客户端计算key的slot

```java
System.out.println(CRC16.crc16("key".getBytes()) % 16384);
```

### 并行slot

```java
public class JedisClusterTest {

    private JedisCluster jedisCluster;

    @BeforeEach
    void setUp() {
        // 配置连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWaitMillis(1000);
        HashSet<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.150.101", 7001));
        nodes.add(new HostAndPort("192.168.150.101", 7002));
        nodes.add(new HostAndPort("192.168.150.101", 7003));
        nodes.add(new HostAndPort("192.168.150.101", 8001));
        nodes.add(new HostAndPort("192.168.150.101", 8002));
        nodes.add(new HostAndPort("192.168.150.101", 8003));
        jedisCluster = new JedisCluster(nodes, poolConfig);
    }

    /**
     * 执行失败
     */
    @Test
    void testMSet() {
        jedisCluster.mset("name", "Jack", "age", "21", "sex", "male");

    }

    @Test
    void testMSet2() {
        Map<String, String> map = new HashMap<>(3);
        map.put("name", "Jack");
        map.put("age", "21");
        map.put("sex", "Male");
        //对Map数据进行分组。根据相同的slot放在一个分组
        //key就是slot，value就是一个组
        Map<Integer, List<Map.Entry<String, String>>> result = map.entrySet()
                .stream()
                .collect(Collectors.groupingBy(
                    // 自己的工具类, 计算插槽
                        entry -> ClusterSlotHashUtil.calculateSlot(entry.getKey()))
                );
        //串行的去执行mset的逻辑
        for (List<Map.Entry<String, String>> list : result.values()) {
            String[] arr = new String[list.size() * 2];
            int j = 0;
            for (int i = 0; i < list.size(); i++) {
                j = i<<2;
                Map.Entry<String, String> e = list.get(0);
                arr[j] = e.getKey();
                arr[j + 1] = e.getValue();
            }
            jedisCluster.mset(arr);
        }
    }

    @AfterEach
    void tearDown() {
        if (jedisCluster != null) {
            jedisCluster.close();
        }
    }
}
```

### Spring下的集群批处理

```java
@Test
void testMSetInCluster() {
    Map<String, String> map = new HashMap<>(3);
    map.put("name", "Rose");
    map.put("age", "21");
    map.put("sex", "Female");
    stringRedisTemplate.opsForValue().multiSet(map);
    List<String> strings = stringRedisTemplate.opsForValue()
        .multiGet(Arrays.asList("name", "age", "sex"));
    strings.forEach(System.out::println);
}
```

Spring做了集群批处理的预备

