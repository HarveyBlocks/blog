# 点赞



## 一人一赞

由前端限制, 点赞之后, 这篇文章和点赞用户建立联系, 此后, 该用户访问此文章, 文章的isLiked=true,将不能点赞

就好像一人一单, 一样使用Set集合

### 业务流程

1.  点赞

2.  查询Blog(数据库, hot缓存)

3.  是否已点赞

    已点赞

    -   点赞数-1, Set集合里取出记录

    未点赞

    -   点赞数+1,Set集合里添加记录

4.  blog的ikLiked字段为`!isMember`



### isLiked字段

```java
/**
 * 是否点赞过了
 */
@TableField(exist = false)
private Boolean isLike;
```



```java
    private void setIsLiked(Blog blog) {
        try {
            Boolean isMember = stringRedisTemplate.opsForSet()
                .isMember(likedSetKey(blog.getId()), currentUserId());
            blog.setIsLike(isMember == null ? Boolean.FALSE : isMember);
        }catch (NullPointerException ignored){
            // 用户未登录,不登陆就做高亮显示
        }
    }

    private static String currentUserId() {
        return UserHolder.getUser().getId().toString();
    }
    private static String likedSetKey(Long blogId) {
        return RedisConstants.BLOG_LIKED_KEY + blogId;
    }
```

### 业务逻辑

```java
@Override
@Transactional
public void likeBlog(Long blogId) {
    String userId = currentUserId();
    String likedSetKey = likedSetKey(blogId);
    Boolean liked = stringRedisTemplate.opsForSet()
            .isMember(likedSetKey, userId);
    if (liked == null) {
        return;
    }
    IBlogService blogService = (IBlogService) AopContext.currentProxy();
    if (liked) {
        // 减少点赞数量
        boolean updateSuccess = blogService.update()
                .setSql("liked = liked - 1").eq("id", blogId).update();
        if (updateSuccess) {
            stringRedisTemplate.opsForSet().remove(likedSetKey, userId);
        }
    } else {
        // 增加点赞数量
        boolean updateSuccess = blogService.update()
                .setSql("liked = liked + 1").eq("id", blogId).update();
        if (updateSuccess) {
            stringRedisTemplate.opsForSet().add(likedSetKey, userId);
        }
    }
}
```

## 点赞排行

点赞越早越靠前

-   使用SortedSet





### 添加元素

```bash
redis(pc2):0>zadd sortedKey 5 value5
"1"
redis(pc2):0>zScore sortedKey value5
"5"
redis(pc2):0>zScore sortedKey value2
"2"
```



```java
stringRedisTemplate.opsForZSet().add(likedSetKey, userId,System.currentTimeMillis());
```

### 查找元素是否存在

```bash
redis(pc2):0>zScore sortedKey value2
"2"
```



```java
private Boolean checkIsMember(String setKey,String userId) {
    return stringRedisTemplate.opsForZSet().score(setKey, userId)!=null;
}
```



### 元素排序输出

```bash
redis(pc2):0>zRange sortedKey 1 2
1) "value2"
2) "value3"

redis(pc2):0>zRange sortedKey 0 2
1) "value1"
2) "value2"
3) "value3"

redis(pc2):0>zRange sortedKey 0 2
1) "value1"
2) "value2"
3) "value3"
```

```java
@Override
public List<UserDTO> topUser(Long blogId) {
    Set<String> ids = stringRedisTemplate.opsForZSet()
            .range(IBlogService.likedSetKey(blogId), 0, 5-1);
    if(ids==null||ids.isEmpty()){
        return Collections.emptyList();
    }
    List<User> top5 = userService.listByIds(ids);// JDBC使用in(id1,id2)查询, 返回结果无序
    if(top5==null||top5.isEmpty()){
        return Collections.emptyList();
    }
    return top5.stream()
            .map((user) -> new UserDTO(user.getId(), user.getNickName(), user.getIcon()))
            .collect(Collectors.toList());
}
```

-   `in`导致返回结果无序的解决

    ```mysql
    select * from `tb_user` where `id` in (1011,1,3)
        order by field(`id`,1011,1,3);-- 字段,id1,id2,id3
    -- 返回结果 id1,id2,id3
    ```

-   ```java
    @Override
    public List<UserDTO> topUser(Long blogId) {
        Set<String> ids = stringRedisTemplate.opsForZSet()
                .range(IBlogService.likedSetKey(blogId), 0, 5-1);
        log.debug(String.valueOf(ids));
        if(ids==null||ids.isEmpty()){
            return Collections.emptyList();
        }
        String idsStr = String.join(",", ids);
        log.debug(idsStr);
        List<User> top5 = userService.query().in("id",ids)
                .last("order by field(id,"+idsStr+")").list();
        if(top5==null||top5.isEmpty()){
            return Collections.emptyList();
        }
        return top5.stream()
                .map((user) -> new UserDTO(user.getId(), user.getNickName(), user.getIcon()))
                .collect(Collectors.toList());
    }
    ```

-   疑惑: `stringRedisTemplate.opsForZSet().range(...);`的返回值是无规则的Set, 为什么总是可以查询出SortedSet里的顺序呢? 看源码: 

    ```java
    Set<TypedTuple<V>> set = new LinkedHashSet<>(rawValues.size());
    for (Tuple rawValue : rawValues) {
        set.add(deserializeTuple(rawValue));
    }
    ```

    `LinkedHashSet`,顺序和加入顺序一致

