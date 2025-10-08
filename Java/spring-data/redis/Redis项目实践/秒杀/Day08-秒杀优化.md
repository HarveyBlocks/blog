# 优化

>   高并发, 一致性, 效率,线程安全问题, Redis集群



## 思路



数据库读写太慢. 选择在Redis内存储**库存信息**和**一人一单**的业务

-   库存信息需要**业务:商品编号为键, 库存为值**
  
    -   过期时间也是有必要的啊
-   一人一单需要**业务:商品编号为键, 订单为值**
    -   订单号为不重复的, 适合使用Set集合
    -   只消到一人一单的Set集合中查找是否存在用户订单, 存在不允许购买, 不存在则
        -   扣减库存

        -   加入Set集合

        -   判断某用户订单是否是Set集合中的成员

            ```bash
            redis(pc2):1>sadd mySet 1 2 3
            "3"
            redis(pc2):1>sIsMember mySet 1 
            "1"
            redis(pc2):1>sIsMember mySet 5
            "0"
            ```
-   为保证以上过程的原子性, 选择使用Lua脚本
-   记录用户ID,订单ID, 优惠券ID
-   数据库中的数据的变更, 放在新的线程中去执行( 异步 )
  
    -   可以累计到一定数量, 然后执行批量增加的操作\



## 需求

原来的对时间的判断还有必要吗?没必要, 前端会做判断

1.  新增秒杀优惠券的同时, 将优惠全小写保存到Redis中
2.  基于Lua脚本, 判断秒杀库存, 一人一单, 决定用户是否抢购成功
3.  如果抢购成功, 不断从阻塞队列中获取信息, 实现异步下单
4.  开启线程任务, 不断阻塞队列中获取信息, 实现异步下单功能


## Redis保存优惠券

```java
@Resource
private StringRedisTemplate stringRedisTemplate;

@Override
@Transactional
public void addSeckillVoucher(Voucher voucher) {
    // 保存优惠券
    ...
    // 保存秒杀信息到数据库
    ...

    // 保存秒杀库到Redis中
    Map<String, String> voucherMap = new HashMap<>();
    voucherMap.put(RedisConstants.SECKILL_VOUCHER_STOCK_FIELD,
                   seckillVoucher.getStock().toString());
    voucherMap.put(RedisConstants.SECKILL_VOUCHER_BEGIN_FIELD,
                   toMillion(seckillVoucher.getBeginTime()));
    voucherMap.put(RedisConstants.SECKILL_VOUCHER_END_FIELD, 
                   toMillion(seckillVoucher.getEndTime()));
    stringRedisTemplate.opsForHash().putAll(
            RedisConstants.SECKILL_VOUCHER_KEY +voucher.getId(),
            voucherMap);
}

private static String toMillion(LocalDateTime time) {
    return String.valueOf(time
            .toInstant(ZoneOffset.of("+8")).toEpochMilli());
}
```

-   过期时间暂不设置

## Lua脚本

### 脚本流程

![image-20240126193708610](../../../assert/Day08-%E7%A7%92%E6%9D%80%E4%BC%98%E5%8C%96/image-20240126193708610.png)



### 脚本

-   增加对事件的判断

```lua
-- 1. 参数列表
-- 1.1 需要得到库存,基于Redis的key,判断value是否大于0
-- 也就是需要key, 即voucher的id
local voucherId = ARGV[1];
-- 1.2 需要得到用户,即用户ID
local userId = ARGV[2];
-- 1.3 需要得到当前时间
local currentTime = tonumber(ARGV[3]);

-- 2. 对参数voucherId进行修饰,获取Redis的key
-- 2.1 库存Key,用`..`来拼接字符串
local voucherKey = 'seckill:voucher:' .. voucherId;
-- 2.2 订单key
local orderKey = 'seckill:order:' .. voucherId;

-- 3. 判断库存是否充足
-- 3.1 取出Redis中stcok的值,是一个字符串, 不能直接比较
local stockValue = tonumber(redis.call('hGet', voucherKey, 'stock'));
local beginTime = tonumber(redis.call('hGet', voucherKey, 'beginTime'));
local endTime = tonumber(redis.call('hGet', voucherKey, 'endTime'));
if(stockValue == nil) then 
    -- 不存在优惠券
    return -1;
end;
-- 3.2 用tonumber()转成数值然后去比较
if (stockValue <= 0) then
    -- 库存不足
    return -1;
end ;
if (beginTime > currentTime) then
    -- 秒杀未开始
    return -3;
end
if (currentTime > endTime) then
    -- 秒杀已结束
    return -4;
end

-- 4. 判断用户是否下单,即判断一个元素是否是set集合里的一个成员
local hasOrdered = redis.call('sIsMember', orderKey, userId);
if (hasOrdered == 1) then
    -- 如果已经下单, 就返回2
    return -2;
end ;

-- 5. 扣减库存,hIncrBy seckill:voucher:17 stock -1
redis.call('hIncrBy', voucherKey,'stock', -1);

-- 6. 下单(保存用户) sAdd
redis.call('sAdd', orderKey, userId);

return 0;
```



### 执行脚本

```java
public class LuaScript {
    public static <T> DefaultRedisScript<T> get(String classPath,Class<T> returnType){
        DefaultRedisScript<T>  script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource(classPath));
        script.setResultType(returnType);
        System.out.println(returnType);
        return script;
    }
}
```

抽象成一个类

`VoucherOrderServiceImpl.java`

```java
@Resource
private StringRedisTemplate stringRedisTemplate;
@Override
public Long executeSeckillLua(Long voucherId) {
    DefaultRedisScript<Long> seckillScript = LuaScript.get("seckill.lua", Long.class);
    Long userId = UserHolder.getUser().getId();
    return stringRedisTemplate.execute(
        seckillScript, 
        Collections.EMPTY_LIST/*不要传null*/, 
        voucherId.toString(), userId.toString());
}
```



```java
// 1. 执行Lua脚本
int result = voucherOrderService.executeSeckillLua(voucherId).intValue();
String errMsg ;
switch (result){
    case -1: errMsg = "已经没有库存啦";break;
    case -2: errMsg = "您已到购买上限. 别再重复购买啦";break;
    case -3: errMsg = "秒杀活动未开始";break;
    case -4: errMsg = "秒杀活动已结束";break;
    default: errMsg = null;
}
// 2. 判断
if (errMsg != null) {
    return Result.fail(errMsg);
}
```

## 保存到阻塞队列

>   当一个线程想从队列中获取元素的时候,如果没有元素, 线程就会被阻塞, 直到队列里有元素了

### 保存内容

-   优惠券ID
-   用户ID
-   订单ID

### 保存

```java
// 这是数组式的队列, 出队了的元素依旧占据队列的空间,想要循环队列, 可以使用Deque系列
private final BlockingQueue<VoucherOrder> queue = 
    new ArrayBlockingQueue<>(1024*1024/*指定队列长度*/);
@Override
public Long saveOrder(Long voucherId) {
    Long userId = UserHolder.getUser().getId();
    // 6. 保存订单信息
    VoucherOrder voucherOrder = new VoucherOrder();
    long orderId = redisIdWorker.nextId("order");
    voucherOrder.setId(orderId);
    voucherOrder.setUserId(userId);
    voucherOrder.setVoucherId(voucherId);
    boolean saved = queue.add(voucherOrder);
    // 获取代理对象,很不优雅,经测试@Resource能成功获取代理, 不会循环注入
    // proxy==this,false
    // proxy =(IVoucherOrderService) AopContext.currentProxy();
    // return orderId;
    return saved?orderId:SAVE_FAIL_ID;
}
```

## 异步下单

>   以上可以概括为**抢单**

### 在项目启动时执行

```java
// 线程池
private static final ExecutorService SECKILL_ORDER_EXECUTOR = 
    Executors.newSingleThreadExecutor();
/**
 * 异步下单<br>
 * 由于这一任务就好像垃圾处理机制一样, 需要时刻准备下单, 需要在服务器一起动时就开启<br>
 * 故使用了@PostConstruct
 *
 * @see javax.annotation.PostConstruct
 */
@PostConstruct
private void asynchronousVoucherOrder() {
    SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
}
```

### 异步任务

```java
// 为了事务, 需要获取代理对象.
@Resource
private IVoucherOrderService proxy;

// 由于Reddis已经判断,能走到保存数据这一步的,已经是保证是一旦的,其实不需要锁
private final RedissonLock redissonLock;

public VoucherOrderServiceImpl(RedissonLock redissonLock) {
    this.redissonLock = redissonLock;
    redissonLock.setErrorResult(IVoucherOrderService.OVER_PURCHASES_ID);
}
/**
 * 优惠券订单任务处理
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-01-05 17:23
 */
private class VoucherOrderHandler implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                // 获取队列中的订单信息
                VoucherOrder order = ORDER_QUEUE.take();// 没有元素马上等待
                Long userId = order.getUserId();// 新线程用UserHolder是取不到的User的,特别注意
                Long orderId = redissonLock
                        .asynchronousLock("lock:voucher:order:" + userId,
                                () -> proxy.saveOrderDb(order)
                        );
                if(orderId.equals(SAVE_FAIL_ID)){
                    log.error("保存队列失败,order信息:{}",order);
                }else if(orderId.equals(OVER_PURCHASES_ID)){
                    // 理论上不可能存在的情况
                    log.error("获取锁失败,不允许重复下单,但不应该啊?");
                }
            } catch (InterruptedException ie) {
                log.error("处理订单发生线程异常", ie);
            } catch (Exception e) {
                log.error("处理订单发生其他异常", e);
            }
        }
    }
}
```



### 秒杀订单存储到数据库

```java
/**
 * 秒杀购买业务
 *
 * @param order 订单
 * @return 返回订单ID, STOCK_SHORTAGE_ID表示库存不足, SAVE_FAIL_ID表示存储失败????
 */
@Override
@Transactional
public long saveOrderDb(VoucherOrder order) {
    // 5. 查询是否存在订单
    Long voucherId = order.getVoucherId();
    Long userId = order.getUserId();
    if (voucherUserExit(userId, voucherId)) {
        return OVER_PURCHASES_ID;
    }
    // 4.  扣减库存
    if (!seckillVoucherService.decrStock(voucherId)) {
        return STOCK_SHORTAGE_ID;
    }
    // 6. 保存订单信息
    boolean saved = this.save(order);
    if (!saved) {
        return SAVE_FAIL_ID;
    }
    return order.getId();
}
```



## Controller逻辑

![image-20240126200614368](../../../assert/Day08-%E7%A7%92%E6%9D%80%E4%BC%98%E5%8C%96/image-20240126200614368.png)



Controller逻辑

```java
@PostMapping("seckill/{id}")
public Result seckillVoucher(@PathVariable("id") Long voucherId,
                             HttpServletResponse response) {

    // 1. 执行Lua脚本
    int result = voucherOrderService.executeSeckillLua(voucherId).intValue();
    String errMsg ;
    switch (result){
        case -1: errMsg = "已经没有库存啦";break;
        case -2: errMsg = "您已到购买上限. 别再重复购买啦";break;
        case -3: errMsg = "秒杀活动未开始";break;
        case -4: errMsg = "秒杀活动已结束";break;
        default: errMsg = null;
    }
    // 2. 判断
    if (errMsg != null) {
        return Result.fail(errMsg);
    }
    // 3. 将下单信息保存到阻塞队列中
    Long orderId = voucherOrderService.saveOrder(voucherId);
    // 返回订单ID,以下判断其实不会怎么遇到
    if (orderId == IVoucherOrderService.STOCK_SHORTAGE_ID) {
        return Result.fail("已经没有库存啦");
    }
    if (orderId == IVoucherOrderService.OVER_PURCHASES_ID) {
        return Result.fail("您已到购买上限. 别再重复购买啦");
    }
    if (orderId == IVoucherOrderService.SAVE_FAIL_ID) {
        return Result.fail("您的订单保存失败, 请重试");
    }
    return Result.ok(orderId);
}
```


## 测试

![image-20240127011420541](../../../assert/Day08-%E7%A7%92%E6%9D%80%E4%BC%98%E5%8C%96/image-20240127011420541.png)

36ms, 很快

## 存在问题

-   阻塞队列的内存限制
-   当服务宕机, 阻塞队列的数据丢失, 引发数据安全问题安全
-   Redis和数据库没有形成一致的事务, 可能出现数据不一致的情况
