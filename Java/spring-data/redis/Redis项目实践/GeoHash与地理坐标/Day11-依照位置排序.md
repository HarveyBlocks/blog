# 依照位置排序

## 前端交互

![image-20240131121346926](../../../assets/Day11-%E4%BE%9D%E7%85%A7%E4%BD%8D%E7%BD%AE%E6%8E%92%E5%BA%8F/image-20240131121346926.png)



## 修改Redis依赖版本

-   原来的Redis依赖是不支持GeoSearch的

```xml
<!--redis-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-redis</artifactId>
        </exclusion>
        <exclusion>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>2.6.2</version>
</dependency>
<dependency>
    <groupId>io.lettuce</groupId>
    <artifactId>lettuce-core</artifactId>
    <version>6.1.6.RELEASE</version>
</dependency>
```

## 代码逻辑



### 分页数据

```java
int from = (current - 1) * DEFAULT_PAGE_SIZE;
int end = current * DEFAULT_PAGE_SIZE;
int size = DEFAULT_PAGE_SIZE;
```



### 从Redis查询ShopId和Distance



```java
/**
 * 从Redis里查询Geo信息
 * @param count 查询数量
 */
private GeoResults<RedisGeoCommands.GeoLocation<String>> getGeoResultsFromRedis(
        Integer typeId, Point point, int count) {
    return stringRedisTemplate.opsForGeo()
                    .search(RedisConstants.SHOP_GEO_KEY + typeId,
                            GeoReference.fromCoordinate(point),
                            /*跟产品经理商量,结果的单位也会是下面指定的单位,单位缺省是m*/
                            new Distance(5000, RedisGeoCommands.DistanceUnit.METERS),
                            /*includeDistance()带上距离,limit()只能从开始查*/
                            RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
                                    .includeDistance()
                                    .limit(count)
                    );
}
```

### 从Redis的查询结果中获取有效的数据

```java
if (results == null) {
    return null;
}
List<String> shopIds = new ArrayList<>(DEFAULT_PAGE_SIZE);
Map<String, Double> shopDistance = new HashMap<>(DEFAULT_PAGE_SIZE);
/*从result获取真正的数据*/
results.getContent()
        /*stream流截取from-end的部分,且不会拷贝集合, 只是跳过, 节省内存*/
        .stream().skip(from)
        .forEach(result -> {
            String shopId = result.getContent().getName();
            double distance = result.getDistance().getValue();
            shopIds.add(shopId);
            shopDistance.put(shopId, distance);
        });
```



### 从数据库中依据ShopID查询Shop完整信息

```java
private List<Shop> queryShopsFromDbOrdered(List<String> shopIds) {
    if (shopIds==null||shopIds.isEmpty()){
        return Collections.emptyList();
    }
    String shopIdsStr = String.join(",", shopIds);
    return this.query()
            .in("id", shopIds)
            .last("order by field(id," + shopIdsStr + ")").list();
}
```



### 将Redis的距离存入Shop的Distance字段

-   Shop的Distance字段

    ```java
    @Getter
    @TableField(exist = false)
    private Double distance;
    ```

-   存入

    ```bash
    shops.forEach(
            (shop) -> shop.setDistance(shopDistance.get(shop.getId().toString()))
    );
    ```

    

### 完整逻辑

```java
@Override
public List<Shop> queryShopByTypeOrderByGeo(Integer typeId, Integer current, Point point) {
    int size = DEFAULT_PAGE_SIZE;
    int from = (current - 1) * size;
    int end = current * size;
    // 根据类型在Redis分页查询Member
    GeoResults<RedisGeoCommands.GeoLocation<String>> results = 
        getGeoResultsFromRedis(typeId, point, end);
    if (results == null) {
        return null;
    }
    List<String> shopIds = new ArrayList<>(size);
    Map<String, Double> shopDistance = new HashMap<>(size);
    /*从result获取真正的数据*/
    results.getContent()
            /*stream流截取from-end的部分,且不会拷贝集合, 只是跳过, 节省内存*/
            .stream().skip(from)
            .forEach(result -> {
                String shopId = result.getContent().getName();
                double distance = result.getDistance().getValue();
                shopIds.add(shopId);
                shopDistance.put(shopId, distance);
            });

    List<Shop> shops = queryShopsFromDbOrdered(shopIds);

    shops.forEach(
            (shop) -> shop.setDistance(shopDistance.get(shop.getId().toString()))
    );
    return shops;
}
```

