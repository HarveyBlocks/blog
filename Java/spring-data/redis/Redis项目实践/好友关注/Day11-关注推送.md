# 关注推送

-   关注了的用户发送新文章时, 立刻通知所有关注的用户(啊?webSocket?)
-   也叫做Feed流, 通过**无限下拉刷新**(看来不用WebSocket了)获取新的信息

要文章,要新的文章, 要没看过的, 要排在前面

## Feed流

分为`TimeLine`和`智能排序`

-   `TimeLine`
    -   不做内容筛选, 简单按照内容发布时间排序
    -   常用于好友或关注, 例如朋友圈, 动态
    -   优点: 
        -   信息全面
        -   不会有缺失
        -   实现简单
    -   缺点:
        -   信息噪音较多, 用户不一定感兴趣, 内容获取效率低
-   `智能排序`
    -   利用智能算法屏蔽掉违规的, 用户不感兴趣的内容, 推送用户感兴趣的信息来吸引用户
    -   优点
        -   头尾用户感兴趣的信息, 用户黏度高, 容易成谜
    -   缺点
        -   算法不准确, 可能会有反效果
        -   实现复杂 

### Timeline实现方式

本例中是在关注列表下接收推送, 因此使用Timeline

![image-20240130093522726](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/好友关注/Day11-关注推送/image-20240130093522726.png)

#### 拉模式

>   读扩散

1.  Writer发送自己的消息到**发件箱**
    -   一个Writer对应一个发件箱
    -   消息=消息体/笔记+时间戳
    -   时间戳是为了消息按照发送时间排序
2.  关注者Fan持有收件箱
    -   收件箱平常是空的
    -   当Fan需要读消息的时候(例如进入关注界面时), 发送消息到收件箱
3.  查询Fan关注了哪些Writer
4.  从关注的Writer里查看发件箱, 将消息拉到Fan的收件箱
5.  将拉来的消息按照时间排序后展示给用户
6.  用户退出关注页面后立刻清除收件箱里的消息, 减少内存的占用

![image-20240130094717893](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/好友关注/Day11-关注推送/image-20240130094717893.png)

-   优点
    -   占用内存空间少
-   缺点
    -   每次来请求都要拉取消息,一系列动作耗时较高
    -   对于关注成百上千Writer的Fan, 这种拉模式执行就太慢了

#### 推模式

>   写扩散

1.  有消息了,马上把消息推送到所有Fan的收件箱里去并再收件箱里按时间排序
2.  Fan来请求了, 就直接到他的收件箱拿到消息就行

-   缺点:
    -   每个Fan都有一份消息, 造成消息的冗余

![image-20240130095101766](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/好友关注/Day11-关注推送/image-20240130095101766.png)

#### 推拉结合

>   读写混合

将Writer分成**大V**和**普通人**

将Fan分成**普通粉丝**和**活跃粉丝**

-   大V,Fan多
-   普通人Fan少
-   普通粉丝人数多,请求次数少
-   活跃粉丝人数少,请求次数多

-   普通人粉丝少, 可以采取推模式, 数据的冗余很少
-   大V对活跃粉丝, 采用推模式, 请求怎么多, 都只有一次数据传输; 活跃粉丝人数少, 不会有太多数据冗余
-   大V对普通粉丝, 采用拉模式

![image-20240130100148485](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/好友关注/Day11-关注推送/image-20240130100148485.png)

#### 三种模式的总结

![image-20240130100212214](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/好友关注/Day11-关注推送/image-20240130100212214.png)

千万以下用户量都算少

### 推模式的分析

1.  保存blog到数据库的同时, 推送到粉丝的收件箱

    -   草, 要维护一张粉丝表

        ```mysql
        select `tf`.`user_id` as `fan_id` from
            (select * from `tb_follow` where `follow_user_id` = 1) as `tf`
        ```

2.  收件箱满足可以根据时间戳排序

    -   Sorted Set,没有isMember的需求, 还要维护一个Timestamp的score, 这里不合适
    -   List

3.  查询收件箱数据时可以实现分页查询

    -   Sorted Set有score,就有排名的概念
    -   List有脚标
    -   但是我们的需求是新的放在前面, 也就是说, 脚标的0在不断变化, 可这对业务有什么关系呢?
        1.  想象一下, 有消息`1` `2` `3` `4` `5` `6`
        2.  数字越大越新
        3.  三条消息一页
        4.  用户查第一页, `6`,`5`,`4`
        5.  此时新消息加入,`1` `2` `3` `4` `5` `6` `7` `8` `9`
        6.  用户查询第二页, 还是, `6`,`5`,`4`
        7.  这样不妥...................==真的不妥吗?==
        8.  难道第二页查到`3` `2` `1`就合理了吗? 
        9.  然后再回到第一页,你查到啥? `9`,`8`,`7`还是 `6`,`5`,`4`?
        10.  所以List是丝毫没有问题的,是你这智障的分页查询有问题
        11.  而且就在查询的同时就有三篇文章出现, 概率就低
        12.  而且一页三条消息页太少了, B都比这多
    -   采用滚动分页的模式, 记录每次分页的最后一条, 下次从这一条开始查

### 推送实现

```java
/**
 * 将blog推送给粉丝
 * @param blogId blogId
 */
@Override
public void sendBlogToFans(Long blogId) {
    // 查询笔记作者的所有粉丝
    //select `user_id` from `tb_follow` where `follow_user_id` = 2;
    List<Follow> follows = followService.query()
            .select("user_id")
            .eq("follow_user_id",currentUserId()).list();
    for (Follow follow : follows) {
        stringRedisTemplate.opsForZSet().add(
                followedInboxKey(follow.getUserId()),
                String.valueOf(blogId),
                System.currentTimeMillis()
        );
    }
}
```

### 获取关注笔记实现

![image-20240130163927814](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-data/redis/Redis项目实践/好友关注/Day11-关注推送/image-20240130163927814.png)

-   Redis模拟分页

    ```bash
    redis(pc2):0>zRangeByScore sortedKey 4 4 # 4是lastScore
    1) "value4" # 获取这个score的Value(同值怎么办呢?)

    redis(pc2):0>zRank sortedKey value4 # 获取value的排名
    "5"
    redis(pc2):0>zRange sortedKey 6 7 # 排名+1为start, 排名+一页个数为end
    1) "value5"
    2) "value6" # 想要的数据

    redis(pc2):0>zScore sortedKey "value6" # 将上一次查询的lastScore记录, 方便下一次查询
    "6"
    redis(pc2):0>zRangeByScore sortedKey 6 6 # 新的轮回
    1) "value6"

    redis(pc2):0>zRank sortedKey value6
    "7"
    redis(pc2):0>zRangeByScore sortedKey 8 9

    ```

    时间戳的话, Score越大越新, 就需要**`Rev`**倒过来

    ```bash
    redis(pc2):0>zRevRangeByScore sortedKey 4 4
    1) "value4"

    redis(pc2):0>zRevRank sortedKey value4
    "2"
    redis(pc2):0>zRevRange sortedKey 3 4
    1) "value3"
    2) "value2.5"

    redis(pc2):0>zScore sortedKey "value2.5"
    "2" # 小了, 说明是更老的数据

    redis(pc2):0>zRevRangeByScore sortedKey 2 2
    1) "value2.5" # 对于同分的,zRevRangeByScore的Rev就有用了
    2) "value2"  # 上面的才是last Value, 我们取上面的,也就是第一个
    ```

    *参数**WithScores**, 两步并作一步,但没必要,因为score:3是多余的*

    ```bash
    redis(pc2):0>zRevRange sortedKey 3 4 withScores
    1) "value3"
    2) "3"
    3) "value2.5"
    4) "2"
    ```

    一步到位的指令

    ```bash
    zRevRangeByScore sortedKey lastId 0 withScores LIMIT offset count
    ```

    `lastId`, 上一次的最后的Score

    `0`, 时间不会小于0, 0作为最小值, 可以使得查询的范围包含小于`lastId`的所有member

    `lastId`和`0`是左闭右闭的

    `offset`表示偏移量, 若`offset`为1, 就表示lastId对应的member不会被查出来

    `count`表示查出来的页数

    ```bash
    redis(pc2):0>zRevRangeByScore sortedKey 8 0 withScores limit 0 2 # 第一次使用无穷大查,偏移0
    1) "value6"
    2) "6"
    3) "value5"
    4) "5"

    redis(pc2):0>zRevRangeByScore sortedKey 5 0 withScores limit 1 2 # 第二次以后使用lastId,偏移1
    1) "value4"
    2) "4"
    3) "value3"
    4) "3"

    redis(pc2):0>zRevRangeByScore sortedKey 3 0 withScores limit 1 2
    1) "value2.5"
    2) "2"
    3) "value2"
    4) "2"

    redis(pc2):0>zRevRangeByScore sortedKey 2 0 withScores limit 1 2
    1) "value2" # 对于同分,依旧存在重复查这种情况,简而言之就是傻逼需求,只能使用zRevRange(不支持Limit)
    2) "2"
    3) "value1"
    4) "1"

    redis(pc2):0>zRevRangeByScore sortedKey 2 0 withScores limit 2 2
    1) "value1"
    2) "1"
    3) "value0"
    4) "1"
    ```

    Offset取决于上一个查出来的最小值有几个

    -   max : lastId(前端会决定是否给当前时间戳)
    -   min : 0
    -   offset : 上一次结果中与最小值一样的元素的个数,上一次结果是Empty或null,offset就为0
    -   count: 和前端商量好

-   返回值

    -   List\<Blog\>
    -   minTime(本查询的推送的最小时间错)
    -   offset偏移量

-   Java代码, 从Redis收件箱获取BlogIds

    ```java
    private Set<ZSetOperations.TypedTuple<String>> getBlogIdsWithTimestamp(Long lastTimestamp, Integer offset) {
        return stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(
                        followedInboxKey(UserHolder.currentUserId()),
                        0, lastTimestamp,
                        offset, COUNT
                );
    }
    ```

-   主体代码

    ```java
    @Override
    public ScrollResult followBlogs(Long lastTimestamp, Integer offset) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples
                = getBlogIdsWithTimestamp(lastTimestamp, offset);

        if (typedTuples == null || typedTuples.isEmpty()) {
            log.error("没有");
            return new ScrollResult(null, lastTimestamp, offset);
        }

        int newOffset = 0;
        long minTime = lastTimestamp;

        int size = typedTuples.size();
        List<String> blogIds = new ArrayList<>(size);
        // 获得blogIds,offset
        for (ZSetOperations.TypedTuple<String>  typedTuple: typedTuples) {
            blogIds.add(typedTuple.getValue());
            // 记录offset
            Double score = typedTuple.getScore();
            if (score == null) {
                log.error("score==null:"+typedTuple.getValue());
                continue;//认为score为无穷大
            }
            if (score.longValue()<minTime){
                minTime = score.longValue();
                newOffset = 0;
            }
            newOffset++;
        }

        // 查询完整笔记
        List<Blog> blogs = queryCompleteBlogs(blogIds);
        log.debug("newOffset="+newOffset);
        log.debug("minTime="+ minTime);
        return new ScrollResult(blogs,minTime, newOffset);
    }
    ```

-   作者信息要填入blogs, 是否点赞要填入blogs

    ```java
    private List<Blog> queryCompleteBlogs(List<String> blogIds) {
        String blogIdsStr = String.join(",", blogIds);
        List<Blog> blogs = this.query().in("id", blogIds)
                .last("order by field(id," + blogIdsStr + ")").list();

        // 让blog信息完整
        blogs.forEach(blog->{
            addWriter(blog);
            setIsLiked(blog);
        });
        return blogs;
    }
    ```

### 关注后创建收件箱

-   关注后将Writer之前的所有作品都加到Redis的收件箱里去

    ```java
    static class BlogIdTuple implements ZSetOperations.TypedTuple<String> {
        private final Blog blog;
        public BlogIdTuple(Blog blog){
            this.blog = blog;
        }
        @Override
        public String getValue() {
            return blog.getId().toString();
        }

        @Override
        public Double getScore() {
            return toMillion(blog.getCreateTime()).doubleValue();
        }

        @Override
        public int compareTo(ZSetOperations.TypedTuple<String> o) {
            // 经源码确认, 这个在添加到Redis的逻辑中, 这个方法的实现是无关紧要的
            return 0;
        }
    }
    ```

    ```java
    List<Blog> blogs = blogService.query().eq("user_id", writerId.toString()).list();
    Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>(blogs.size());
    blogs.forEach((blog) -> {tuples.add(new BlogIdTuple(blog));});
    stringRedisTemplate.opsForZSet().add(followedInboxKey(fanId), tuples);
    ```

-   取关后删除收件箱

    ```java
    stringRedisTemplate.delete(followedInboxKey(fanId));
    ```

