# 数据表(table)操作

- 要先用`use 数据库名`进入该数据库

## 查询

### 查询(show)数据表

```mysql
SHOW TABLES;
```

- 查询当前说有数据表

### 查询(description)表结构

```mysql
DESC 表名;
```

- descend 下降

### 查询(show)指定表的建表语句

```mysql
SHOW CREATE TABLE 表名;
```



## 创建

```mysql
CREATE TABLE 表名(
	字段1 类型[COMMENT '字段注释'],
    字段2 类型[COMMENT '字段注释'],
    ...
    字段N 类型[COMMENT '字段注释']
)[COMMENT '表注释'];
```



## 示例


```mysql
create table tb_user(
	id int comment '编号',
    name varchar(50) comment '姓名',
    age int ,
    gender char(1) comment '性别非男即女'
)comment '用户表';

show tables;

desc tb_user;-- 不详细
show create table tb_user;
```
```mysql
create table employee(
	id int comment '编号',
	worknumber varchar(10) comment '员工工号',
    name varchar(5) comment '姓名',
    gender char(1) comment '性别',
    age tinyint unsigned comment '无负数,小于255',
    idcard char(18) comment '身份证',
    entrydate date comment '入职时间,年月日,不需要时分秒'
)comment '员工表';
```

## 为已创建的数据表修改(alter table)

- 现有如下两张数据表

  ```mysql
  +-------------------+
  | Tables_in_company |
  +-------------------+
  | emplee            |
  | tb_user           |
  +-------------------+
  2 rows in set (0.00 sec)
  ```

  - emplee:

    ```mysql
  mysql> desc employee;
    +------------+------------------+------+-----+---------+-------+
  | Field      | Type             | Null | Key | Default | Extra |
    +------------+------------------+------+-----+---------+-------+
    | id         | int              | YES  |     | NULL    |       |
    | worknumber | varchar(10)      | YES  |     | NULL    |       |
    | name       | varchar(5)       | YES  |     | NULL    |       |
    | gender     | char(1)          | YES  |     | NULL    |       |
    | age        | tinyint unsigned | YES  |     | NULL    |       |
    | idcard     | char(18)         | YES  |     | NULL    |       |
    | entrydate  | date             | YES  |     | NULL    |       |
    +------------+------------------+------+-----+---------+-------+
    7 rows in set (0.01 sec)
    ```
  
  - tb_user
  
    ```mysql
    +--------+-------------+------+-----+---------+-------+
    | Field  | Type        | Null | Key | Default | Extra |
    +--------+-------------+------+-----+---------+-------+
    | id     | int         | YES  |     | NULL    |       |
    | name   | varchar(50) | YES  |     | NULL    |       |
    | age    | int         | YES  |     | NULL    |       |
    | gender | char(1)     | YES  |     | NULL    |       |
    +--------+-------------+------+-----+---------+-------+
    4 rows in set (0.01 sec)
    ```


### 修改表名

```mysql
ALTER TABLE 表名 REANEAME TO 新表名;
```

### 删除(drop)表

```mysql
DROP TABLE [IF EXISTS] 表名;
```
- 删除表

```mysql
TRUNCATE TABLE 表名;
```
-  删除(truncate)表然后再创建
   -  	类似于**清空**记录(会保留字段行,但不会保留记录)

