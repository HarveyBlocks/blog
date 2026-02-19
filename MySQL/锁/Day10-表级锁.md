# 表级锁

1.  表锁
2.  元数据锁(**Meta Data Lock - MDL**)
3.  意向锁

## 表锁

1.  表共享读锁 (**Read Lock**)
2.  表独占写锁(**Write Lock**)

### 语法

-   加锁

    ```mysql
    lock tables 表名 read|write;
    ```

-   释放锁

    ```mysql
    unlock tables 
    ```

    ```mysql
    让客户端连接关闭,锁也会关
    ```

### 读锁

#### 特点

-   所有人(上锁的客户端和其他客户端)都只读不写
-   都能读
-   都不能写

-   上锁的客户端

    ![image-20231026132758734](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/锁/Day10-表级锁/image-20231026132758734.png)

    -   报错

-   其他客户端

    ![image-20231026132845347](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/锁/Day10-表级锁/image-20231026132845347.png)

    -   等待

    -   如果接触锁,这个客户端

        ![image-20231026132944378](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/锁/Day10-表级锁/image-20231026132944378.png)

    -   瞬间执行

#### 奇怪的想法

如果我在别人上锁的时候,再上一层锁呢?,我来解锁呢?

**这些操作都会等待那个上锁的客户端解锁后执行**

### 写锁

#### 特点

-   加锁的客户端能读能写
-   其他客户端不能读,不能写

## 元数据锁

>   MDL加锁过程是系统自动控制的,无需显示使用,在访问一张表的时候会自动加上.

### 作用

-   维护表元数据的数据的一致性

-   在表上有活动事物的时候不可以对元数据进行写入操作
    -   如果一张表有未提交的事务,我们就不能去更改这张表的表结构
    -   避免DML(**增删改查**)和DDL(**建表**)的冲突

### 执行原理

-   当对一张表进行DML(**增删改查**)的时候
    -   (执行**查语句**)自动加MDL读锁(**共享**->所有客户端都可以)
    -   (执行**增删改语句**)读锁之间(**Shared_Read和Shared_Write**)是可以兼容的
-   当对表结构进行变更操作的时候
    -   (执行**修改表语句**)加MDL写锁(**排他**)
    -   写锁和写锁,写锁和读锁(**Shared_Read,Shared_Write和Exclusive**)是冲突的

![image-20231026134429398](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/锁/Day10-表级锁/image-20231026134429398.png)

-   在**增删改查**的事务执行时,会体现出他有在自动加锁

### 查看元数据锁

```mysql
delete from user where id = 1;
select
    Object_Type,
    Object_Schema,
    Object_Name,
    Lock_Type,
    Lock_Duration
from Performance_Schema.MetaData_Locks;
```

## 意向锁

-   防止别人乱上锁的锁(**艺术就套娃是嵌套是吧**)
    -   乱上锁是指,线程A上了行锁,线程B给整张表上了表锁
    -   线程B就叫乱上锁
-   解决DML中表锁和行锁的冲突时,需要检查每一行确保每一行都没 有上锁这种效率极低的行为

### 兼容与使用

1.  线程A加行锁之后会加上意向锁

2.  线程B来加表锁

3.  线程A的意向锁回来检查线程B的表锁是什么锁:

    ![image-20231026140518580](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/锁/Day10-表级锁/image-20231026140518580.png)

    -   IS是和共享锁兼容的,与IX锁是不兼容的
    -   IX锁是和所有锁都不兼容的
    -   意向锁之间是不会互斥的

4.  兼容,就可以加表锁,不兼容,就不能加表锁

### 实例

-   线程A-加上共享锁

    ```mysql
    select * from user lock in share mode;
    ```

-   线程B

    ```mysql
    lock tables 表名 read;
    ```

    没事,因为共享与兼容

### 查看意向锁

```mysql
select
    Object_Schema,	-- 涉及到的数据库
    Object_Name,	-- 涉及到的表
    Index_Name,		-- 涉及索引的名字
    Lock_Type,		-- 锁的对象(数据库,数据表,行)
    Lock_Mode,		-- 锁的类型(IS...)
    Lock_Data		-- 锁的......Data?
from Performance_Schema.Data_Locks;
```

## 附:Share Mode

```mysql
Select语句 lock in share mode;
```

-   代表会加上这一行的行锁,和这张表的意向锁

