# 序列化

上回说到

```java
// 获取键值对的操作
ValueOperations valueOperations = redisTemplate.opsForValue();
// 底层由自动的序列化机制
valueOperations.set("key","value");
valueOperations.set("id",12);// 两个参数都可以是Object

logger.info(String.valueOf(valueOperations.get("key")));
logger.info(String.valueOf(valueOperations.get("id")));
```
我们返回控制台

```bash
centos-redis:0>keys *
1) "\xAC\xED\x00\x05t\x00\x02id"
2) "\xAC\xED\x00\x05t\x00\x03key"

centos-redis:0>get "\xAC\xED\x00\x05t\x00\x02id"
"\xAC\xED\x00\x05sr\x00\x11java.lang.Integer\x12\xE2\xA0\xA4\xF7\x81\x878\x02\x00\x01I\x00\x05valuexr\x00\x10java.lang.Number\x86\xAC\x95\x1D\x0B\x94\xE0\x8B\x02\x00\x00xp\x00\x00\x00\x0C"
centos-redis:0>get "\xAC\xED\x00\x05t\x00\x03key"
"\xAC\xED\x00\x05t\x00\x05value"
```

这些看不懂的, 就是序列化的结果

使用的就是JDK的序列化工具`ObjectOutputStream`

## 序列化器



`org.springframework.data.redis.core.RedisTemplate`

![image-20240102135851906](../../typora-user-images/Day02-rt%E7%9A%84%E5%BA%8F%E5%88%97%E5%8C%96/image-20240102135851906.png)

### 默认序列化器

默认序列化器`ObjectOutputStream`

```java
@Override
public void afterPropertiesSet() {

    super.afterPropertiesSet();

    boolean defaultUsed = false;

    if (defaultSerializer == null) {

       defaultSerializer = new JdkSerializationRedisSerializer(// 定义了一个序列化器
             classLoader != null ? classLoader : this.getClass().getClassLoader());
    }
    ...
}
```

看源码, 从set入

```java
@Override
public void set(K key, V value) {

    byte[] rawValue = rawValue(value);
    ...
}
```

到rawValue

```java
byte[] rawValue(Object value) {

    if (valueSerializer() == null && value instanceof byte[]) {
       return (byte[]) value;
    }

    return valueSerializer().serialize(value);// 这里使用了序列化器的序列化
}
```

从`序列化`进去

经过好几层的源码

```java
public void serialize(Object object, OutputStream outputStream) throws IOException {
    if (!(object instanceof Serializable)) {
       throw new IllegalArgumentException(getClass().getSimpleName() + 
                                          " requires a Serializable payload " +
             "but received an object of type [" + 
                                          object.getClass().getName() + "]");
    }
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
    // 就在这儿
    objectOutputStream.writeObject(object);
    objectOutputStream.flush();
}
```

### 默认序列化器的缺点

1.  可读性差

    ```bash
    centos-redis:0>get "\xAC\xED\x00\x05t\x00\x02id"
    "\xAC\xED\x00\x05sr\x00\x11java.lang.Integer\x12\xE2\xA0\xA4\xF7\x81\x878\x02\x00\x01I\x00\x05valuexr\x00\x10java.lang.Number\x86\xAC\x95\x1D\x0B\x94\xE0\x8B\x02\x00\x00xp\x00\x00\x00\x0C"
    ```

2.  可能出现问题

    例如在查询控制台想改key, **但此key非彼key**, 该改不改, 不该改的改了

3.  内存占用空间大

### 配置序列化器

#### 选择序列化器

可选择的序列化器

![image-20240102141054093](../../typora-user-images/Day02-rt%E7%9A%84%E5%BA%8F%E5%88%97%E5%8C%96/image-20240102141054093.png)

**key一般是字符串. 用`StringRedisSeralizer`**

**Value可能是Object. 用`GenericJackson2JsonRedisSerlizer`**

#### 设置序列化器

```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(
        RedisConnectionFactory rcf
    ){
        // 创建Template
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置连接工厂
        redisTemplate.setConnectionFactory(rcf);

        // 设置key的序列化
        redisTemplate.setKeySerializer(
                RedisSerializer.string()
        /*
        省的去new了
        static RedisSerializer<String> string() {
		    return StringRedisSerializer.UTF_8;
	    }*/);
        // 创建JSON序列化工具. 等价于RedisSerializer.json();
        GenericJackson2JsonRedisSerializer jsonSerializer
                = new GenericJackson2JsonRedisSerializer();
        // 设置value的序列化
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashValueSerializer(RedisSerializer.json());// 一样的
        return redisTemplate;

    }
}

```

此时去用, 就会快快乐乐地报错qwq

原因是缺少Jackson依赖

```
nested exception is java.lang.NoClassDefFoundError: com/fasterxml/jackson/core/JsonProcessingException
```

SpringMVC里头自带Jackson-databind依赖, 不需要在意这个

```xml
<!--Jackson依赖-->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

### 测试

#### 对值是字符串的测试

```java
@SpringBootTest
class RedisApplicationTests {
    private final Logger logger = LoggerFactory.getLogger("name");
    
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Test
    void testRedisTemplate(){
        redisTemplate.opsForValue().set("key","你好");
    }
}
```



查询控制台

```bash
centos-redis:0>get key
""你好""
```



#### 对值是实体的测试

```java
@Test
void testSaveEntity(){
    String userKsy = "user:001";
    // 存入User
    redisTemplate.opsForValue().set(userKsy, new User("小明",12));
    // 获取
    User userValue = (User) redisTemplate.opsForValue().get(userKsy);
    System.out.println(userValue);
    //User{name='小明', age=12}
}
```



查询控制台

```bash
centos-redis:0>keys *
1) "user:001"

centos-redis:0>get user:001
"{"@class":"com.harvey.spring.data.redis.pojo.entity.User","name":"小明","age":12}"
```

![image-20240102150646694](../../typora-user-images/Day02-rt%E7%9A%84%E5%BA%8F%E5%88%97%E5%8C%96/image-20240102150646694.png)

### Jackson2序列化的缺点

如上图, 自带的`"@class"`所占有的空间甚至比本事的大小还要大

而且这种数据是高度重复的, 一万个用户, 他们的都是User类的

**为了节省空间, 我们统一使用字符串的序列化工具, 手动序列化和反序列化**

-   序列化:

    ```
    实体-手动->Json字符串-RedisTemplate序列化->字符串
    ```

-   反序列化

    ```
    字符串-RedisTemplate反序列化->Json字符串-手动->实体
    ```

-   Spring提供了**`StringRedisTemolate`**的类, key和value都是String的序列化方式



#### 手动序列化

-   待封装

```java
@Autowired
private StringRedisTemplate stringRedisTemplate;
@Test
void testStringRedisTemplate() throws JsonProcessingException {


    // ObjectMapper,SpringMVC中使用的默认制动序列化工具
    ObjectMapper mapper = new ObjectMapper();

    // 手动序列化
    String json = mapper.writeValueAsString(new User("小明", 12));

    //有变化的部分↓
    stringRedisTemplate.opsForValue();
    String userKsy = "user:001";
    
    // 存入User
    stringRedisTemplate.opsForValue().set(userKsy, json);
    String jsonUser = stringRedisTemplate.opsForValue().get(userKsy);
	//有变化的部分↑
    
    
    // 手动反序列化
    User userValue = mapper.readValue(json, User.class);
    System.out.println(userValue);
    //User{name='小明', age=12}
}
```

![image-20240102150613999](../../typora-user-images/Day02-rt%E7%9A%84%E5%BA%8F%E5%88%97%E5%8C%96/image-20240102150613999.png)

**关注一下前后的键值大小**

