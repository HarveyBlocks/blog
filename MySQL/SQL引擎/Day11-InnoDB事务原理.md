# 概述

>   事务时一组操作的集合
>
>   时一个不可分割的工作单位
>
>   事务会把所有的操作作为一个整体一起向系统提交或撤销操作请求
>
>   即这些操作要么成功,要么同时失败

## 事务的四大特性

[ACID](..\SQL基础\事务\Day06-事务的特性.md)

-   原子性
-   一致性
-   隔离性
-   持久性

[并发事务问题](..\SQL基础\事务\Day06-并发事务.md)

![image-20231027151123534](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/SQL引擎/Day11-InnoDB事务原理/image-20231027151123534.png)

![image-20231027151239058](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/SQL引擎/Day11-InnoDB事务原理/image-20231027151239058.png)

-   MVCC多版本并发控制

# Redo Log 重做日志

-   保证事务的**持久性**
-   **记录**事务提交时**数据页的物理修改**
-   **该日志由两部分组成:**
    -   重做日志缓冲(Redo Log Buffer) ----内存
    -   重做日志文件(Redo Log File) ---- 磁盘
-   当事务提交之后会**把所有修改信息都存到日志文件**中
-   用于**刷新脏页到磁盘**
-   ***发生错误时,进行数据恢复使用***

### 没有 Redo log 的情况:

1.  客户端发出含有多个**删改**语句的事务
2.  首先操作内存的缓冲区(Buffer Pool)
    -   **查找**有没有我们要更新的数据
    -   在**磁盘中查找**,**放到缓冲区**(Buffer Pool)
3.  在缓冲区**删改**数据
    -   此时,缓冲区里的数据变更,磁盘里的数据没变
    -   这两处的数据不一致
    -   此时,缓冲区里的数据称为**脏页**
4.  在一定的时机,**把脏页的数据刷新到磁盘**
    -   此时**缓冲区里的数据和磁盘里的数据一致**

-   那么,在刷新日志到磁盘文件这一刻**出了问题**,但又由于缓冲区没问题因此**已经告诉用户提交成功**,则***为之奈何***

### Redo log ,一致性的救世主(Bushi)

会把我们增删改数据记录的物理变化在**redolog buffer **里

**永久保存**在磁盘文件 **ib_logfile0/1** 中

如果脏页刷新出错了,就可以用 **ib_logfile0/1** 进行数据的恢复

脏页顺利刷新了,就每隔一段时间清理 **ib_logfile0/1** 

 **ib_logfile0/1** **轮流**写,**轮流**清理

### WAL(Write-Ahead Logging)机制

-   刷新脏页是随机磁盘IO
-   随机磁盘IO效率低
-   Log日志文件都是追加的,是顺序IO,效率更高
-   通过cache**合并多条写操作为一条**，减少IO次数
-   

>   而WAL可以将多个随机IO转换为顺序IO操作，从而提高写入性能。
>
>   ​																							--------GPT

# Undo Log 回滚日志

-   ***保障原子性***
-   **用于记录数据被修改前的信息,**

-   作用包括两个:
    -   提供回滚
    -   MVCC(多版本并发控制)

>    Undo Log 和 Redo Log 记录的物理日志不一样,它记录的是**逻辑日志**

### Undo Log 实现回滚

可以认为:

-   当Delete一条记录时,Undo Log中会记录一条**相反的insert记录**
-   当Insert一条记录时,Undo Log中会记录一条**相反的的Delete记录**
-   当Update 一条记录时,他会**记录一条相反的Update记录**
-   当**执行Rollback**是,就从Undo log的逻辑记录中**读取到相应的内容**并进行回滚(太酷啦)

### Undo Log 销毁

Undo Log 在事务执行时产生,事务提交后,**并不会立刻删除**Undo Log,**因为这些日志还可能用于MVCC**

### Undo Log 存储

Undo Log 采用 [**段**](Day11-InnoDB逻辑存储结构) 的方式进行管理和记录,存放在前面的 Rollback Segment 回滚段中,内部包含1024个Undo Log Segment

