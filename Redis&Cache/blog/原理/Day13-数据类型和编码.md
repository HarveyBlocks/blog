# 数据类型和编码

![image-20240405194043887](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405194043887.png)



## string

### RAW

-   最基本的编码方式是**RAW**, 基于简单字符串SDS实现, 存储上限是**512MB**



![image-20240405194438227](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405194438227.png)

### EMBSTR



-   如果存储的SDS长度小于44字节, 将会改变编码格式, 采用**EMBSTR**编码

    此时`object head`和SDS是一段连续的空间, 申请内存只需要调用一次内存分配函数, 效率更高

-   44字节? Object头16字节+SDS头3字节+44字节+结束符`\0`一字节=64字节

![image-20240405195104794](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405195104794.png)

### INT

-   如果存储的字符串是整数值, 并且大小在LONG_MAX范围内, 则采用**INT**编码
-   直接讲数据保存在RedisObject的ptr指针位置(刚好8字节), 不再需要SDS

![image-20240405195548404](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405195548404.png)



### 使用指导

能用数字格式就用数字格式

能不要超过44字节就不要超过44字节

## List

可以从双端访问

### LinkedList

双端链表

内存占用较高

内存碎片较多

### ZipList

内存占用低

对连续内存的要求高, 容易达到存储上限

### QuickList

LinkedList+ZipList

内存占用低, 存储上限高

![image-20240405201151086](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405201151086.png)

## Set

无有序性要求

元素唯一

高效查询元素是否存在

交并差集

### IntSet

-   存储的所有数据都是整数
-   元素数量不超过`set-max-intset-entries`, 默认512
-   当插入元素不符合上述条件时, 将会把IntSet改变成HashTable
-   更节省内存



![image-20240405205853450](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405205853450.png)





### HashTable

-   Key存储元素
-   value统一为null





![image-20240405205952847](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405205952847.png)



## ZSet

-   根据Score值排序
-   确保Member的唯一性
-   能根据member查询score



```C
// zset结构
typedef struct zset{
    // Dict指针
    dict *dict; // 实现键的唯一存储和从键到值的查询
    // SkipList指针
    zskiplist *zskiplist; // 实现排序, 范围查询, 从值到键的查询
}
```



### HashTable+SkipList

![image-20240405211011738](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405211011738.png)

数据重复存储, 肥肠臃肿内存占用很高

### ZipList

当元素数量不多时, HashTable和SkipList的优势不明显, 因为遍历也不会慢多少

反而这种组合会更消耗内存

因此zset在同时满足: 

1.  元素个数小于`zset_max_ziplist_entries`, 默认128
2.  每个元素都小于`zset_max_ziplist_value`字节, 默认64

时采用ZipList结构节省内存

相邻两个entry分别是member和score



![image-20240405214418243](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405214418243.png)

-   score越小越接近队首, score升序排序

## Hash

-   都是键值存储
-   都是需要根据键取值
-   键唯一
-   无序

### ZipList

-   默认
    -   当以下条件同时满足: 
    -   ZipList中的元素超过了`hash-max-ziplist-entries`, 默认256个
    -   ZipList中的任意entry大小超过了`hash-max-ziplist-value` , 默认64字节
-   节省空间
-   相邻两个entry分别是field和value



![image-20240405215336522](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405215336522.png)

### HashTable

![image-20240405215549570](../../assetss/Day13-%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E7%BC%96%E7%A0%81/image-20240405215549570.png)

