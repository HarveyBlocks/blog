# RDB数据备份

>   Redis Database Backup file Redis数据备份文件
>
>   也叫做Redis数据快照

就是把内存中的所有数据都记录到磁盘中. 当Redis实例故障重启后, 从磁盘读取快照文件, 恢复数据

## 执行备份

### 主进程保存

在Redis内

```shell
centos-redis:0>save
"OK"
```

由Redis的主进程完成(Redis只有一条线程), 做这个命令的时候会阻塞所有命令

`save`是磁盘IO,比较慢

适合在Redis宕机之前, 或者是人为的,主动的想要关闭服务器之前进行备份(其实主动停机的化, 是会帮你自动RDB的)

### 后台保存

>   Backgroung Save

```shell
centos-redis:0>bgSave
"Background saving started"
```

开启子进程,避免主进程收到影响

##RDB有关配置

Redis.config

```ini
# 900s内,如果至少由一个key被修改, 就执行bgsave
# 如果为`save ""`表示禁用RDB
save 900 1
save 300 10
save 60 10000

# 是否压缩RDB文件
# 不建议开启,压缩会消耗CPU,磁盘容量不值钱
rdbcompression no

# RDB文件名
dbfilename dump.rdb

# 文件保存的路径目录
dir /var/lib/redis
```

## 异步保存原理

### Fork

`bgSave`开始时会**fork**主进程到子进程, 子进程**共享**主进程的内存数据

完成**fork**后读取内存数据并写入RDB文件

写入过程不占用主进程资源; *fork过程会占用资源*, **造成主进程阻塞**

###RDB实现原理

-   主进程为了维护虚拟内存, 有一张映射**虚拟内存**和**物理内存**关系的**页表**
-   从主进程将数据**fork**到子进程, 实际上**fork**的是这张**页表**,而不会拷贝真实的数据
-   那么就有可能出现**脏数据**

###Redis对脏数据的处理

-   `copy-on-write`
-   当主进程执行读操作的时候,则会拷贝一份数据,执行写操作

1.  主进程依据页表向物理内存内写(改)数据
2.  不会直接写到物理内存里去, 而是向从物理内存中**完整拷贝一份数据**到数据副本
3.  此后页表的映射将从原来的, 改为映射到副本了
4.  主线程读写就从副本里去读写

-   `copy-on-write`可能会产生在备份数据(RDB)时, 就有可能出现拷贝出好多副本导致内存被大量占用的情况

