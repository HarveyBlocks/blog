# AOF持久化

>   Append Only File 追加文件

Redis处理的每一个写命令都会记录在AOF文件中, 可以看作时命令日志文件

## 开启AOF

```ini
# 是否开启AOF功能,默认no
appendonly yes
# AOF 文件名称, 后缀是`.aof`,前缀随意
appendfilename "appendonly.aof"
```

## AOF配置



### 配置记录频次

```ini
# 表示每执行异常写命令, 立即记录到AOF文件
# 数据安全能绝对保障;性能最差
appendfsync always
# 写命令执行完先放入AOF缓存区,然后每隔一秒将缓存区里的数据写到AOF文件, 是默认的方案
# 如果在一秒之间服务宕机, 就有可能出现数据丢失的情况
appendfsync everysec
# 写命令执行完先放入AOF缓冲区, 由操作系统决定合适将缓冲区协会磁盘
# 频率是比较低的
appendsync no
```

## AOF数据冗余

AOF记录的不是数据, 而是命令日志

所以会重复记录数据来回更改的记录

造成了数据的冗余

而且, 在重启时, 以前走过的弯路, 现在还要再走一遍

###对AOF文件重写

```shell
redis-6380:0>bgRewriteAof
"Background append only file rewriting started"
```

后台异步对AOF文件进行整理

```shell
set num 123
set name jack
set age 12
set name 666
del age
```

=> 

```shell
mset name jack num 666
```

甚至还会用批量写操作, 太优秀啦

还会做一点压缩

### 配置触发AOF重写

```ini
# AOF文件比上次文件重写增长超过多少百分比则触发重写,默认100
auto-aof-rewrite-percentage 100
# AOF文件体积最小多大以上触发重写,默认64mb
auto-aof-rewrite-min-size 64mb
```

-   对于`auto-aof-rewrite-percentage`, 那不是一开始是0,那后来不是无穷无尽是0,除非手动来一下? 
-   对于`auto-aof-rewrite-min-size`, 如果重写之后依旧大于64mb, 那不是会鬼畜吗

## AOF和RDB的比较

![image-20240207185318515](../../assets/Day05-AOF%E6%8C%81%E4%B9%85%E5%8C%96/image-20240207185318515.png)