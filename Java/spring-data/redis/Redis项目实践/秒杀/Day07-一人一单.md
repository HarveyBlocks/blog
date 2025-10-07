# 一人一单

>   如果我要 "每人限购七张, 阁下又该如何应对"



##实现流程

![image-20240120151734806](../../../assert/Day07-%E4%B8%80%E4%BA%BA%E4%B8%80%E5%8D%95/image-20240120151734806.png)

##实现代码

```java
// 4. 查询是否存在订单
if(voucherUserExit(userId)){
    return OVER_PURCHASES_ID;
}
// 5.  扣减库存
// 6. 保存订单信息
```



```java
/**
 * 判断是否已经存在用户的该订单
 *
 * @param userId    用户Id
 * @param voucherId 优惠券Id
 * @return 存在为true
 */
private boolean voucherUserExit(Long userId, Long voucherId) {
    return query()
            .eq("user_id",userId)
            .eq("voucher_Id",voucherId)
            .count() > 0 ;
}
```

### 测试

![image-20240120153128019](../../../assert/Day07-%E4%B8%80%E4%BA%BA%E4%B8%80%E5%8D%95/image-20240120153128019.png)

## 存在问题

使用JMETER高并发地查

![image-20240120153601176](../../../assert/Day07-%E4%B8%80%E4%BA%BA%E4%B8%80%E5%8D%95/image-20240120153601176.png)

高并发, 导致连续几次`insert`的操作进行时, order记录还是不存在的

### 解决方案

加死锁

**但是又不能让之前的乐观锁白费**



###锁的粒度

要使用改变锁的粒度的方法

我们分析一下发现, 我们只需要防范**一个**用户的高并发. 所以可以将粒度设计为`userId`

```java
Long userId = UserHolder.getUser().getId();
synchronized (userId){ 
    // 同一个用户加锁!!!!降低锁的粒度!!!
    ...
}
```

->

```java
Long userId = UserHolder.getUser().getId();
synchronized (userId){ 
    // 5. 查询是否存在订单
    if(voucherUserExit(userId, voucherId)){
        return OVER_PURCHASES_ID;
    }
    // 4. 扣减库存
    // 6. 保存订单信息
    return orderId;
}
```

### "值"作为锁

**但是还没完!!!**

使用的锁`userId`是一个**Long对象**

因此它就会使用地址作为判断是否是同一个锁

要怎么使用它的**值作为锁**呢 ?

只用`userId.toString()`是不够的:

![image-20240120155456729](../../../assert/Day07-%E4%B8%80%E4%BA%BA%E4%B8%80%E5%8D%95/image-20240120155456729.png)

它使用了new这种方法创建新的字符串对象

**正确的处理方法**

```java
userId.toString()// 获取值
    .intern()//在静态的字符串池中找寻相同值的字符串返回
```

### 事务和锁

-   **在方法内部启用锁:** 

    1.  锁释放之后, 方法才能结束; 
    2.  方法结束了, 事务才能提交数据;
        -   *其他线程此刻**进入锁***
        -   开始查询,发现不存在订单(因为事务还没提交)
    3.  事务提交之后才能改变数据, 才能确实查出订单是否真实存在

-   而在**锁释放之后**, **事务提交之前**, 其他线程查询到的订单是 ***不存在的*** **!!!** 此时就会引发线程安全问题

-   解决如下:

    1.  把`seckillVoucher()`的锁去掉

    2.  给Controller加锁, 把`@Transactional`修饰的方法被锁包裹

        ```java
        Long userId = UserHolder.getUser().getId();
        long orderId;
        synchronized (userId.toString().intern()){
            orderId = voucherOrderService.seckillVoucher(voucher);
        }
        ```

        

#### Spring事务失效的可能性

```java
void a(){
    b();//其实是调用了this.b(),导致了事务的失效
}

@Transactional
void b(){
    ...
}
```

解决

依赖:

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
```



```java
@EnableAspectJAutoProxy(exposeProxy = true)
public class 启动类 {}
```



```java
void a(){
	IService proxy = (IService) AopContext.currentProxy();
	proxy.b();
}
```

