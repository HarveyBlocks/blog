# 签到

## 签到卡

-   ![image-20240131163648604](../../../assert/Day11-%E7%AD%BE%E5%88%B0%E5%8D%A1/image-20240131163648604.png)

干脆用unsigned int, 差不多一个月

##Redis设计

-   key
    -   user:sign:用户:年:月
-   value选择BitMap, 1表示已签到, 0表示未签到

## 代码

-   在Spring的RedisTemplate里, BitMap的操作被封装到字符串的ValueOperations里去了

###签到

```java
@Override
public void sign(Long userId, LocalDateTime now) {
    int year = now.getYear();
    int month = now.getMonth().getValue();
    int day = now.getDayOfMonth();// from 1 to 31
    String signKey = RedisConstants.USER_SIGN_KEY+userId+":"+year%100+":"+month;
    stringRedisTemplate.opsForValue().setBit(signKey,day-1,true);
}
```

###连续签到数统计

-   问题: 上个月的要不要考虑到这个月的连续里去?

```C
unsigned int countCoiledBits(unsigned int num) {
	unsigned int count = 0;
	while (num & 1) {
		num = num >> 1;
		count++;
	}
	return count;
}
```

```java
@Override
public int countCoiledSign(Long userId, LocalDateTime now) {
    int day = now.getDayOfMonth();
    List<Long> longs = stringRedisTemplate
            .opsForValue().bitField(signKey(userId, now),
                    BitFieldSubCommands.create().get(
                            BitFieldSubCommands.BitFieldType.signed(day)
                    ).valueAt(0));
    if (longs == null || longs.isEmpty()) {
        return 0;
    } else if (longs.size() != 1) {
        log.error(userId + "出现longs.size()=" + longs.size());
        return -1;
    }
    return countCoiled(longs.get(0));
}
```

