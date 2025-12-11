# 导入地理坐标

## 数据库分析

![image-20240131113439577](../../../assets/Day11-%E5%AF%BC%E5%85%A5%E5%9C%B0%E7%90%86%E5%9D%90%E6%A0%87/image-20240131113439577.png)





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

