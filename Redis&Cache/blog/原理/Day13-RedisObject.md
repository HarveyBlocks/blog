# RedisObject

Redis中, 无论键值, 任何数据都会被包装成RedisObject;

## 结构

```C
typedef struct redisObject {
    unsigned type:4; // 四个Bit位, string(0), list(1), set(2), zset(3), hash(4)
    unsigned encoding:4; // 6.2.6版本是11中编码
    // ↓记录Redis对象最近一次访问是什么时候
    unsigned lru:LRU_BITS; /* LRU time (relative to global lru_clock) or
                            * LFU data (least significant 8 bits frequency
                            * and most significant 16 bits access time). */
    // 引用计数器, 被引用就会+1, 结束引用就会-1, 到0就会被回收
    int refcount;
    void *ptr; // 指向存储的类型
} robj;
```

每个对象都有一个头, 如果是string, 每个string都会有一个头, 浪费了内存

但是如果用了存储容器, 就只有一个头, 节省了内存的开支

## Encoding编码

```C
#define OBJ_ENCODING_RAW 0     /* Raw 编码动态字符串 */
#define OBJ_ENCODING_INT 1     /* long类型整数字符串 */
#define OBJ_ENCODING_HT 2      /* hash表(字典dict) */
#define OBJ_ENCODING_ZIPMAP 3  /* Encoded as zipmap 已废弃 */
#define OBJ_ENCODING_LINKEDLIST 4 /* No longer used: old list encoding. u欧菲去哦, 双端列表*/
#define OBJ_ENCODING_ZIPLIST 5 /* Encoded as ziplist */
#define OBJ_ENCODING_INTSET 6  /* Encoded as intset */
#define OBJ_ENCODING_SKIPLIST 7  /* Encoded as skiplist */
#define OBJ_ENCODING_EMBSTR 8  /* Embedded sds string encoding */
#define OBJ_ENCODING_QUICKLIST 9 /* Encoded as linked list of ziplists 快速列表*/
#define OBJ_ENCODING_STREAM 10 /* Encoded as a radix tree of listpacks 流*/
```

### 类型和编码

![image-20240405194043887](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Redis%26Cache/原理/Day13-RedisObject/image-20240405194043887.png)

