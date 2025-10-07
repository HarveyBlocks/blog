# BitMap

>   位图

-   就是二进制的一长串
-   方便, 简单, 节省空间
-   Redis底层使用string实现BitMap,最大上限是512M, 即2^32给bit位



查看一个数转二进制后有几个1



##写



### SetBit

>   向指定位置(offset)存入0或1

```bash
redis(pc2):1>setBit bitMapKey 0 1
"0"
redis(pc2):1>setBit bitMapKey 1 1
"0"
redis(pc2):1>setBit bitMapKey 2 1
"0"
redis(pc2):1>setBit bitMapKey 3 1 7 1 9 1
"ERR wrong number of arguments for 'setbit' command"
redis(pc2):1>setBit bitMapKey 7 1
"0"
```

-   经测试, 这个返回值是0还是1, 和添加是否成功无关, 和添加的是0还是1无关



##读

### GetBit

>   获取指定位置(offset)的bit



### BitField

>   查询/修改/自增BitMap中bit数组中的指定位置(offset)的值

```bash
redis(pc2):1>bitField bitMapKey Get u1 0
1) "1"

redis(pc2):1>bitField bitMapKey Get i1 0
1) "-1"
```

-   `Get`表示查操作
    -   u表示返回的是无符号十进制
    -   i表示返回的是有符号十进制
    -   u和i后面的数表示读的长度
    -   `0`表示`offset`,从哪位开始读

### BitField_Ro

>`ReadOnly`, 获取BitMap中的bit数组, 并以十进制形式返回





###BitPos

>   查找bit数组中指定范围内第一个0/1出现的位置

```bash
redis(pc2):1>bitPos bitMapKey 0
"4"
redis(pc2):1>bitPos bitMapKey 1
"0"
```



##运算

### BitOp

>将多个BitMap的值运算(位与,位或, 位异或)

### BitCount

>   BitMap的值为1的数量

```bash
redis(pc2):1>bitCount bitMapKey
"5"
```



```C
unsigned int countSetBits(unsigned int num) {
	if (num == 0) {
		return 0;
	}
	unsigned int count = 0;
	while (num > 0) {
		if (num & 1) {
			// 末尾是1
			count++;
		}
		num = num >> 1;
	}
	return count;
}
```

