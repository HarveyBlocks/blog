# 缓存

>   Cache 数据交换缓冲区 临时存储数据, 读写性能比较高

在web应用的每一层, 从浏览器到tomcat到应用层到CPU到磁盘, 都可以搭建缓存

## 缓存的作用

-   降低后端负载
-   提高读写效率, 降低响应时间

## 缓存的成本

-   数据的一致性成本
    -   数据库的数据发生变化, 不能很及时的放入缓存, 就会发生读写不一致
-   代码的维护成本
    -   为了数据的一致性
    -   缓存的穿透, 击穿问题
-   运维成本
    -   缓存往往需要集群
    -   部署和维护就需要大量的人力成本
-   硬件成本

## 用Redis作为MySQL的缓存

```java
@GetMapping("/{id}")
public Result queryShopById(@PathVariable("id") Long id) {
    return Result.ok(shopService.getById(id));
}
```

直接在数据库中查找了

### 缓存模型分析

-   客户端在Redis中查找
    -   找到数据
        -   命中返回数据
    -   未找到数据
        1.  在数MySQL数据库中查找
        2.  将数据写到缓存中
        3.  将新写入的数据设置时效

![image-20240103210133106](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/查询缓存/Day03-缓存/image-20240103210133106.png)

### 实现商铺的存储

`com.harvey.review_system.controller.ShopController`

```java
@GetMapping("/{id}")
public Result queryShopById(@PathVariable("id") Long id, HttpServletResponse response) {
    Shop shop;
    try {
        shop = shopService.queryById(id);
    } catch (JsonProcessingException e) {
        shop = null;
    }
    if (shop == null) {
        response.setStatus(404);
        return Result.fail("店铺不存在");
    }
    return Result.ok(shopService.getById(id));
}
```

`service`

```java
@Override
public Shop queryById(Long id) throws JsonProcessingException {

    Shop shop = null;
    String shopKey = RedisConstants.CACHE_SHOP_KEY + id;
    ObjectMapper mapper = new ObjectMapper();
    // 从缓存查
    String json = stringRedisTemplate.opsForValue().get(shopKey);
    if (json != null) {
        try {
            shop = mapper.readValue(json, Shop.class);
        } catch (JsonProcessingException e) {
            // 时间类型的Json转换问题
            e.printStackTrace();
            throw e;
        }
    }

    if (shop == null) {
        // 不存在
        // 数据库查
        shop = this.getById(id);
        if (shop == null) {
            // 不存在则404
            return null;
        }
        // 存在,写入Redis,设置TTL
        String shopJson = null;
        shopJson = mapper.writeValueAsString(shop);
        stringRedisTemplate.opsForValue().set(shopKey, shopJson);
    }
    stringRedisTemplate.expire(shopKey, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
    // 返回
    return shop;
}
```

#### 解决ObjectMapper来序列化LocalDateTime时的异常

使用Hutool工具包的JSON工具

```java
shop = JSONUtil.toBean(json, Shop.class);
shopJson = JSONUtil.toJsonStr(shop);
```

### 测试

![image-20240104015028762](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/查询缓存/Day03-缓存/image-20240104015028762.png)

![image-20240104015056013](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/查询缓存/Day03-缓存/image-20240104015056013.png)

### ShopType的业务实现

```java
public List<ShopType> sortShopList() {
    List<ShopType> typeList;

    Set<String> shopTypeKeys = stringRedisTemplate.keys(
        RedisConstants.CACHE_SHOP_TYPE_KEY + "*");
    if (shopTypeKeys == null||shopTypeKeys.isEmpty()) {
        typeList = this.query().orderByAsc("sort").list();
        for (ShopType shopType : typeList) {
            String shopTypeKey = RedisConstants.CACHE_SHOP_TYPE_KEY + shopType.getId();
            String shopJson = JSONUtil.toJsonStr(shopType);
            stringRedisTemplate.opsForValue().set(shopTypeKey, shopJson);
        }
    } else {
        typeList = new ArrayList<>();
        for (String shopTypeKey : shopTypeKeys) {
            String json = stringRedisTemplate.opsForValue().get(shopTypeKey);
            ShopType shopType = JSONUtil.toBean(json, ShopType.class);
            typeList.add(shopType);
        }
        typeList.sort(Comparator.comparingInt(ShopType::getSort));
    }
    return typeList;
}
```

