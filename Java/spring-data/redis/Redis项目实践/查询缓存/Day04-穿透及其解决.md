# 缓存穿透

>   客户端所请求的数据在缓存和数据库都不存在, 那样缓存永远不会生效(真的吗?), 且这些请求都会打到数据库

## 危险

不怀好意的人, 每次请求都请求些不存在的数据, 开了巨多线程疯狂输出,且这些输出直击我们的数据库,把我们的数据库搞垮了 

## 解决方案

### 缓存空对象

-   暴力的处理方案

#### 描述

查到缓存和数据库都不存在的值, 就给一个空的值缓存, 让你每次对着缓存输出

#### 弊端

额外的内存消耗, 

-   解决: 设置短的TTL

造成数据不一致

-   到时候真的注册了这个数据,  要把原来的null数据顶替掉
-   缓解: 设置短的TTL, 不必去看数据是否放在缓存, 只需要等缓存中数据失效, 到时候自然会拿取新数据
-   尽管有短期的数据不一致, 也是可以接收(你不接受, 就写更多的代码把原来的null数据顶替掉)



###布隆过滤

在缓存和客户端之间,设置布隆过滤器

1.  客户端请求数据
    -   不存在=>拦截数据
2.  存在则放行

#### 布隆过滤器原理

依据Hash算法, 把数据算成Hash值,存到布隆过滤器(节省存储空间)

所以布隆过滤器说不存在的数据, 一定不存在; **说存在的数据, 不一定存在**=>再一次产生穿透风险



那每次修改数据, 都要到布隆过滤器去注册????

#### 好处

内存占用少,没有多余的Key

#### 坏处

-   实现复杂(但是Redis有准备)
-   存在误判可能

### 我的YY

1.  缓存专门有个List(黑名单), 存放数据的ID(或者是其他的查数据的依据)
2.  先去查不存在的数据, 缓存和数据库都没有
3.  将查的不存在数据的ID存入专门List(黑名单),
4.  以后每次查数据,发现ID在黑名单里, 就拒绝请求, 不在, 就往下走
5.  给这个List设置时效(或容量限制), 依据队列, 一定时间/容量后, 先入队的ID会出队, say goodbye, 减少内存消耗, 也数据不一致也只会是短期的(要么增加数据的时候删除黑名单里的数据)
6.  如果继续作妖就封禁用户ID

##代码实现缓存空对象

查到缓存和数据库都不存在的值, 就返回一个空的值到缓存

到时候真的注册了这个数据,  要把原来的null数据顶替掉

```java
@Override
public Shop queryById(Long id) throws JsonProcessingException {
    Shop shop = null;

    // 从缓存查
    if (shop == null) {
        // 缓存不存在
        // 数据库查
        if (shop == null) {
            // 数据库里也不存在
            // 缓存空对象的逻辑
        }
        ...
    }
    if( shop = "我们设定的空值" ){
        // 404
    }
    ...
}
```



原来的代码存在缺陷, 现已经改进

```java
@Override
public Shop queryById(Long id) throws JsonProcessingException {

    Shop shop = null;
    String shopKey = RedisConstants.CACHE_SHOP_KEY + id;

    // 从缓存查
    String json = stringRedisTemplate.opsForValue().get(shopKey);
    if (json != null) {
        // 缓存存在
        if (json.isEmpty()) {
            // 我们的假数据
            return null;
        }
        shop = JSONUtil.toBean(json, Shop.class);
        return shop;
    }
    // 缓存不存在
    // 使用缓存空对象的逻辑
    Long ttl = RedisConstants.CACHE_NULL_TTL;
    String shopJson = "";
    // 数据库查
    shop = this.getById(id);
    if (shop != null)  {
        // 存在,写入Redis,更改TTL
        shopJson = JSONUtil.toJsonStr(shop);
        ttl = RedisConstants.CACHE_SHOP_TTL;
    }
    stringRedisTemplate.opsForValue().set(shopKey, shopJson);
    stringRedisTemplate.expire(shopKey, ttl, TimeUnit.MINUTES);
    // 返回
    return shop;
}
```

![image-20240104135629680](../../../../typora-user-images/Day04-%E7%BC%93%E5%AD%98%E7%A9%BF%E9%80%8F/image-20240104135629680.png)

## 其他防止缓存穿透的方案

增加ID的复杂度, 避免被猜到ID的规律

准备有规范的ID, 不符合规范的ID直接pass

加强用户权限校验

对热点参数做限流





## 穿透的问题

第一次访问不存在, 然后真的一直不存在, 永远是无效的访问吗?

如果有一个坏人(A), 一直对一个不存在的数据虚空索敌, 然后系统一直保持了一个虚假的缓存

然后这个数据真的存在了, 真的加到数据库里去了

然后那个添加数据的人(B)高高兴兴地去检查有没有数据, 他就会走缓存

系统就会认定, 那个B也是来虚空索敌的

然后系统就把空的假数据给B了

然后B就很困惑

然后B重新增加了一遍一样的数据

然后依旧失败

虽然这种刚好猜到ID的应该是小概率事件

更何况用的是雪花

那如果坏人(A)对着一段ID大范围地做虚空索敌呢?

一般来说这种虚假的数据TTL做个5s是吧, 这5s内对10万个数据进行虚空索敌

那么这种情况有什么损失呢?

如果是支付ID, 系统查询支付ID, 发现没有支付ID, 以为是没有支付, 就让用户反复支付, 造成了用户的损失?



当然, 这是我的杞人忧天, 因为我们又没有每次虚空索敌之后更新假数据的TTL, 所以还是会定期删除, So, 呆胶布

吗? 如果失效之后重新虚空索敌, 那么就可以拿到真数据, 那就不是虚空索敌了, 没事了

