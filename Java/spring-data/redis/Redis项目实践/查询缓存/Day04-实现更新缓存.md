# 实现缓存更新

## 缓存策略分析

由**缓存的调用者**,在**更新**数据库的同时更新缓存

利用事务的一致性(本实践是单体的项目)

先在数据库中更改

再删除缓存

## 为查询的数据增加过期实践

```java
stringRedisTemplate.expire(shopKey, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
```

## 实现更新逻辑

```java
@PutMapping
public Result updateShop(@RequestBody Shop shop) {
    // 写入数据库
    shopService.updateById(shop);
    return Result.ok();
}
```

```java
@Override
@Transactional// 完全忘了有这么一会儿事
public boolean updateCache(Shop shop) {
    if (shop.getId()==null){
        return false;
    }
    // 1. 更新数据库
    if(!updateById(shop)){
        return false;
    }
    // 2. 删除缓存
    stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY +shop.getId());
    return true;
}
```

## 测试

记得关闭用户登录权限啥都

![image-20240104130207986](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/查询缓存/Day04-实现更新缓存/image-20240104130207986.png)

![image-20240104130343610](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/查询缓存/Day04-实现更新缓存/image-20240104130343610.png)

成功删除缓存

![image-20240104130227962](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/查询缓存/Day04-实现更新缓存/image-20240104130227962.png)

成功更新

记得把用户权限啥的开起来

