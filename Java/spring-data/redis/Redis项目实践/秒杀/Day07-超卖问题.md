# 超卖问题

## 存在

[JMeter测试](超买问题.jmx)

-   我的直接-80,这不是因为逻辑问题, 因为我放慢速度之后, 扣到0没有再扣,唯一的原因是我电脑性能太好了?(或者说是扣库存的逻辑执行太慢了?)

## 原理

![image-20240120135627045](../../../assets/Day07-%E8%B6%85%E5%8D%96%E9%97%AE%E9%A2%98/image-20240120135627045.png)

并发安全问题, 多条线程操作一个资源

## 解决方案

### 加锁

-   悲观锁

-   乐观锁

    ![image-20240120142544858](../../../assets/Day07-%E8%B6%85%E5%8D%96%E9%97%AE%E9%A2%98/image-20240120142544858.png)

在本方案中, 使用库存的变化代替版本号的变化

```mysql
update `秒杀优惠券表` 
	set stock = stock - 1 
	where 
		id = ${id}
		and stock = ${之前查询到的stock};
```

#### 实现

```java
/**
 * 自动扣减(减一)库存
 * @param voucher 优惠券
 * @return 是否更新成功
 */
@Override
public boolean decrStock(Voucher voucher) {
    return this.update()
            .setSql("stock = stock - 1")
            .eq("voucher_id",voucher.getId())
            .eq("stock",voucher.getStock())
            .update();
}
```

#### 乐观锁引发的降低成功率问题

-   测试可以看出, 测试了两百次,只有二三十次成功了

    -   解决方案一:

        由上测试可以看出, 乐观锁会导致, 而设计库存的时候stock可以为负数

        (当然设计成字段非负的话,也是报错而不是返回false)

        所以不会报错而又返回false的时候,

        认为是请求繁忙而不是库存不足,也就是说:

        ```java
        long orderId = voucherOrderService.seckillVoucher(voucher);
        if (orderId==IVoucherOrderService.SECKILL_BUSY){
            return Result.fail("请求繁忙,请稍后重试");
        }
        ```

        换一种说法, 很符合国情-----谨记我的一次真实经历😡

    -   解决方案二

        ```java
        .eq("stock",voucher.getStock())
        ```

        换成大于0不就行了

        ```mysql
        update `秒杀优惠券表` 
        	set stock = stock - 1 
        	where 
        		id = ${id}
        		and stock > 0;
        ```

        ```java
        .gt("stock",0)
        ```

