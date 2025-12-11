# 进程本地缓存

如HashMap,GuavaCache

## 特点

-   优点: 读取本地内存, 没有网络开销, 速度比分布式缓存快
-   缺点: 存储容量有限, 可靠性较低(Tomcat服务器重启数据就丢失), 无法共享
-   场景: 性能要求较小, 缓存数据量较小

## Caffeine

>   JVM进程缓存

基于Java8开发, 提供了近乎嘴角命中率的高性能本地缓存

目前Spring内部使用的缓存就是Caffeine

### 简述

[Caffeine](https://github.com/ben-manes/caffeine/wiki/Home-zh-CN)

### 依赖引入

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

### 基本用法

```java
@Test
void testBasicOps() {
    // 创建缓存对象
    Cache<String, String> cache = Caffeine.newBuilder().build();

    // 存数据,键值对的结构
    cache.put("key", "value");

    // 取数据，不存在则返回null
    String value = cache.getIfPresent("key");
    System.out.println("value = " + value);

    // 取数据，不存在则去数据库查询
    String defaultKey = cache.get("defaultKey", key -> {
        // 这里可以去数据库根据 key查询value
        return "value in db";
        // 并会自动加入Cache
    });
    System.out.println("defaultKey = " + defaultKey);

    System.out.println("defaultKey = " + cache.getIfPresent("defaultKey"));
    cache.invalidate(defaultKey); // 删除
}
```

测试结构

```text
value = value
defaultKey = value in db
value = value in db
```

### Caffeine驱逐策略

>   需要通过设置一个过期时间等方法,来防止缓存爆满, 

-   默认情况下, 当一个缓存过期的时候, Caffeine的驱逐策略不是立即驱逐, 而是在一次读或写操作后, 或者在空闲时间完成对失效数据的驱逐

#### 基于容量

设置缓存的数量上限

覆盖老旧数据

```java
@Test
void testEvictByNum() throws InterruptedException {
    // 创建缓存对象
    Cache<String, String> cache = Caffeine.newBuilder()
            // 设置缓存大小上限为 1
            .maximumSize(1)
            .build();
    // 存数据
    cache.put("key1", "value1");
    cache.put("key2", "value2");
    cache.put("key3", "value3");
    System.out.println("key1: " + cache.getIfPresent("key1"));
    System.out.println("key2: " + cache.getIfPresent("key2"));
    System.out.println("key3: " + cache.getIfPresent("key3"));
    System.out.println("--------------------------------------");
    // 延迟10ms，给清理线程一点时间
    Thread.sleep(10L);
    // 获取数据
    System.out.println("key1: " + cache.getIfPresent("key1"));
    System.out.println("key2: " + cache.getIfPresent("key2"));
    System.out.println("key3: " + cache.getIfPresent("key3"));
}
```

测试结果

```text
key1: value1
key2: value2
key3: value3
--------------------------------------
key1: null
key2: null
key3: value3
```

#### 基于时间

设置缓存的有效时间

```java
@Test
void testEvictByTime() throws InterruptedException {
    // 创建缓存对象
    Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(1)) // 设置缓存有效期为 1 秒
            .build();
    // 存数据
    cache.put("key", "value");
    // 获取数据
    System.out.println("key: " + cache.getIfPresent("key"));
    // 休眠一会儿
    Thread.sleep(1200L);
    System.out.println("key: " + cache.getIfPresent("key"));
}
```

测试结果

```bash
key: value
key: null
```

#### 基于引用

设置缓存为软引用或弱引用, 利用GC来挥手缓存数据. 

性能较差, 不建议使用

### Caffeine的使用

#### 在配置类创建缓存

将Item信息分为Item表和ItemStock表(itemStock是库存和售出数据), item表的数据是不常变的, itemStock的数据是常变的, 分开存储, 有利于频繁读写ItemStock的缓存

```java
@Configuration
public class CacheConfig {
    @Bean
    public Cache<Long, Item> itemCache(){
        return Caffeine.newBuilder()
                .initialCapacity(100) // 初始大小
                .maximumSize(1_0000) // 最大上限
                .build();
    }
    @Bean
    public Cache<Long, ItemStock> itemStockCache(){
        return Caffeine.newBuilder()
                .initialCapacity(100) // 初始大小
                .maximumSize(1_0000) // 最大上限
                .build();
    }
}
```

#### 配合缓存的查询

```java
@GetMapping("/{id}")
public Item findById(@PathVariable("id") Long id) {
    return itemCache.get(
            id,// 优先在缓存查
            key -> itemService.query() // 未命中在数据库查
                    .ne("status", 3).eq("id", key)
                    .one()
    );
}
```

