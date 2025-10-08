# Java客户端

## 需求

1.  创建Stream类型的消息队列, 名为`stream.orders`
2.  修改之前的秒杀下单Lua脚本, 认定有抢购资格后, 直接向`stream.orders`中添加消息, 内容包含`voucherId`,`userId`.`orderId`
3.  项目启动后, 开启一个线程任务, 尝试获取stream.orders中的消息, 完成下单

## 创建Stream

```八十\
xGroup Create stream.orders g1 0 MkStream
```

完成创建队列和消费者组





## 添加消息

### 修改Lua脚本

1.  添加参数orderId

    ```lua
    -- 1.3 订单ID
    local orderId = ARGV[3];
    ```

2.  发布消息

    ```lua
    -- 7. 发布消息到stream.orders
    -- xAdd stream.orders * k1 v1 k2 v2...
    redis.call('xAdd','stream.orders','*','id',orderId,'voucherId',voucherId,'userId',userId);
    ```

    orderId的Field选择使用`id`是为了和VoucherOrder实体类保持一致

### 修改Java的执行Lua脚本的代码

-   `VoucherOrderController#seckillVoucher`

    ```java
    @Resource
    private RedisIdWorker redisIdWorker;
    
    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {
    
        // 1. 执行Lua脚本
        Long orderId = redisIdWorker.nextId("order");// 将生成OrderId的逻辑提前
        // 传入第二个参数
        int result = voucherOrderService.executeSeckillLua(voucherId,orderId).intValue();
        String errMsg ;
        switch (result){
            case -1: errMsg = "已经没有库存啦";break;
            case -2: errMsg = "您已到购买上限. 别再重复购买啦";break;
            case -3: errMsg = "秒杀活动未开始";break;
            case -4: errMsg = "秒杀活动已结束";break;
            case -5: errMsg = "该优惠券不存在";break;
            default: errMsg = null;
        }
        
        /* 2. 判断
        if (errMsg != null) {
            return Result.fail(errMsg);
        }*/
        /* 3. 将下单信息保存到阻塞队列中
        Long orderId = voucherOrderService.saveOrder(voucherId);
        以下判断不会发送, Lua脚本已解决一切
        ...*/
        
        // 判断并返回订单ID或错误信息
        return errMsg == null?Result.ok(orderId):Result.fail(errMsg);
    }
    ```

-   `VoucherOrderServiceImpl#executeSeckillLua`

    ```java
    @Override
    public Long executeSeckillLua(Long voucherId, Long orderId) {
        DefaultRedisScript<Long> seckillScript = LuaScript.get("seckill.lua", Long.class);
        Long userId = UserHolder.getUser().getId();
        String currentTime = String.valueOf(System.currentTimeMillis());
        return stringRedisTemplate.execute(seckillScript, Collections.EMPTY_LIST,
                voucherId.toString(), userId.toString(),orderId/*传入Lua参数*/,currentTime);
    }
    ```

    





## 获取消息

### 伪代码分析

```java
while(true){
    Object msg = redis.excute("XReadGroup Group g c Count 1 Block 2000 Streams s >");
    // 使用>,取得没有被消费的消息
    if(msg == null){
        // 阻塞两秒之后还是没有消息, 继续阻塞
        continue;
    }
    try{
        handleMsg(msg);// 处理消息
        Ack(msg);// 一定要ACK消息
    } catch(Exception e){
        // 产生异常, 消息没有被ACK
        while(true){
            // 处理未被确认消息的循环
            Object msg = redis.excute("XReadGroup Group g c Count 1 Block 2000 Streams s 0");
            // 使用0, 取得没有被确认的消息
            if(msg == null){
                // 没有未确认的消息,说明所有消息都被确认,可以跳出循环 
                break;
            }
            try{
                handleMsg(msg);// 处理未确认的消息
                Ack(msg);// 再次尝试ACK消息
            } catch(Exception e){
                // 产生异常, 消息再次没有被ACK
				// 记录日志
                continue;// 再次循环尝试ACK消息, 直到消息全部被ACK为止,否则一直记日志,等待人工的介入
            }
        }
    }
}
```

### 代码

`VoucherOrderServiceImpl#asynchronousVoucherOrder`

```java
@PostConstruct
private void asynchronousVoucherOrder() {
    SECKILL_ORDER_EXECUTOR.submit(
            /*this::saveVoucherOrderByQueue*/
            this::saveVoucherOrderByStream
    );
}
```

#### 常量

```java
private static final String STREAM_KEY = "stream.orders";
private static final long COUNT = 1;
private static final String GROUP_NAME = "g1";
private static final String CONSUMER_NAME = "c1";
```

###



#### 从Stream获取

```java
/**
 * @return MapRecord<streamKey,field,value>
 */
private List<MapRecord<String, Object, Object>> getOrdersFromRedisStream( ReadOffset readOffset) {
    // XReadGroup Group g1 c1(消费者由节点决定, 不同JVM节点使用不同Consumer)
    // Count 1 Block 2000 Streams streams.order >

    return stringRedisTemplate.opsForStream().read(
                    Consumer.from(GROUP_NAME, CONSUMER_NAME),//spring.redis里的Consumer类
                    StreamReadOptions.empty()// 先创建一个空的出来
                            .count(COUNT).block(Duration.ofMillis(2000)),
                    StreamOffset.create(
                            STREAM_KEY,
                            readOffset
                    )
            );
}
```

#### ACK确认

```java
// 4. ACK确认 XAck streams.order g1 id
private void ack(RecordId id) {
    stringRedisTemplate.opsForStream()
            .acknowledge(STREAM_KEY, GROUP_NAME, id);
}
```

#### 消息转化

转成VoucherOrder订单

```java
private VoucherOrder recordToOrder(MapRecord<String, Object, Object> record) {
    Map<Object, Object> value = record.getValue();
    // 糊涂工具包
    return BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);// 忽略错误
}
```

#### 处理消息

存储消息到DB

```java
private void handleVoucherOrder(VoucherOrder order) throws InterruptedException {
    if (order == null) {
        return;
    }
    Long userId = order.getUserId();// 新线程用UserHolder是取不到的User的,特别注意
    Long orderId = redissonLock
            .asynchronousLock("lock:voucher:order:" + userId,
                    () -> proxy.saveOrderDb(order)
            );
    if (orderId.equals(SAVE_FAIL_ID)) {
        log.error("保存队列失败,order信息:{}", order);
    } else if (orderId.equals(OVER_PURCHASES_ID)) {
        // 理论上不可能存在的情况
        log.error("获取锁失败,不允许重复下单,但不应该啊?");
    }
}
```

#### 整体逻辑



```java
/**
 * 优惠券订单任务处理,通过流
 *
 * @date 2024-01-05 17:23
 */
private void saveVoucherOrderByStream() {
    while (true) {
        // 1. 获取消息队列中的订单信息
        List<MapRecord<String, Object, Object>> records =
                getOrdersFromRedisStream(ReadOffset.lastConsumed()/*最近一次未消费*/);
        // 2. 判断消息是否成功
        if (records == null || records.isEmpty() || records.size() != COUNT) {
            // 未成功获取,没有消息(等奇怪的情况),重试
            continue;
        }
        VoucherOrder order = recordToOrder(records.get(0));

        try {
            // 3. 获取成功,可以保存到数据库
            handleVoucherOrder(order);
            // 4. ACK确认 XAck streams.order g1 id
            ack(records.get(0).getId());
        } catch (Exception e) {
            log.error("错误发生,消息未被确认");
            while (true) {
                // 1. 获取消息队列中未确认的订单信息
                // XReadGroup Group g1 c1 Count 1 Block 2000 Streams streams.order 0
                List<MapRecord<String, Object, Object>> unackRecords =
                        getOrdersFromRedisStream(ReadOffset.from("0")/*最早一次未确认的消费*/);
                // 2. 判断消息是否获取成功
                if (unackRecords == null || unackRecords.isEmpty()) {
                    // 未成功获取,没有消息(等奇怪的情况)
                    break;
                }
                if (records.size() != COUNT) {
                    log.error("records.size()!=count,why????");
                }

                VoucherOrder unackOrder = recordToOrder(unackRecords.get(0));
                try {
                    // 3. 获取成功,可以保存到数据库
                    handleVoucherOrder(unackOrder);
                    // 4. ACK确认
                    ack(unackRecords.get(0).getId());
                } catch (Exception ne) {
                    log.error("处理pending-list异常,消息未被确认", ne);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }
    }
}
```

## 测试

成功
