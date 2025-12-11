# 事务操作



## 语法

## 方式一

### 语法

```mysql
SELECT @@autocommit;-- 默认为一,自动提交事务的数据到数据可
SET @@autocommit = 0;-- 改为手动提交,改为手动后,任何更改数据库的指令都要加上commit;
```

-   @@是指当前客户端窗口的状态信息



-   手动提交时需要写的提交指令,任何更改数据库的指令都要加

```mysql
COMMIT;
```

-   回滚事务

```mysql
ROLLBACK;
```

### 示例

正常情况:

```mysql
SET @@autocommit = 0;
-- 执行指令
update employee set age = 0 where name is null;
update employee set age = 023 where name is not null ;
-- 提交数据
commit ;
-- 回滚,指令正确不会回滚
rollback ;
```

不正常:

```mysql
SET @@autocommit = 0;
-- 执行指令
update employee set age = 0 where name is null
这里发生了错误....
update employee set age = 23 where name is not null ;
-- 提交数据
commit ;
-- 回滚,指令错误直接回滚
rollback ;
SET @@autocommit = 1;
```



### 回滚事务

如果出现异常了,事务自然会停止,为何还要回滚事务呢?

答:

​	如果不回滚事务,出异常的之前的指令都是执行了的,但这些执行因为后面的中断失去了意义

## 方式二

### 语法

-   开启事务

```mysql
START TRANSCATION;
```

或

```mysql
BEGIN;
```



-   提交,回滚

```mysql
commit;
rollback;
```



### 示例

```mysql
SET @@autocommit = 0;
START Transaction;
	-- 执行指令
	update employee set age = 0 where name is null
	这里发生了错误....
	update employee set age = 23 where name is not null ;
	-- 提交数据
	commit ;
	-- 回滚,指令错误直接回滚
	rollback ;
SET @@autocommit = 1;
```



## 案例

-   升官:
    1.  组长升官
        1.  组长的部门换成董事会,
        2.  level换成董事
        3.  manage_id换成董事长
    2.  选一个合适的( 这里选择年龄最大的)作为组长的接班人
        1.  找出年龄最大的人
            1.  异常:有两个年龄一样大的人
        2.  level升级成组长
    3.  其他组员的manage_id换成新组长的id

