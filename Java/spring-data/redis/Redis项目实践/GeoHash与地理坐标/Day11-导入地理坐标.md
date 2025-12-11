# 导入地理坐标

## 数据库分析

![image-20240131113439577](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/GeoHash与地理坐标/Day11-导入地理坐标/image-20240131113439577.png)

## Redis数据结构设计

-   使用Geo数据结构

-   key为shopTypeId
-   member为shopId
-   LonLat为x,y

## 代码逻辑

```java
@Resource
private IShopService shopService;
@Resource
private StringRedisTemplate stringRedisTemplate;

public void moveGeoFromDb2Redis() {
    Map<Long, List<Shop>> shopGroupByType = shopService.list()
            .stream().collect(Collectors.groupingBy(Shop::getTypeId));

    shopGroupByType.forEach((typeId,shops)->{
        Map<String, Point> shopCoordinateMap = shops.stream().collect(Collectors.toMap(
                (shop)-> shop.getId().toString(),
                (shop)-> new Point(shop.getX(),shop.getY())
        ));
        stringRedisTemplate.opsForGeo()
                .add(RedisConstants.SHOP_GEO_KEY +typeId,shopCoordinateMap);
    });

}
```

