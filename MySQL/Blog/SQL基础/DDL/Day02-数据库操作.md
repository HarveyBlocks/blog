# DDL 数据库(database)操作

## 查询(show)数据库

```mysql
SHOW DATABASES;
```

- 查询所有数据库

## 创建(create)数据库

```mysql
CREATE DATABASE [IF NOT EXISTS] 数据库名 [DEFAULT CHARSET 字符集] [COLLATE 排序规则];
```

### 最简写法

```mysql
CREATE DATABASE 数据库名;
```

- 最简写法
- 如果数据库存在就报错

### if not exists

```mysql
CREATE DATABASE IF NOT EXISTS  数据库名;
```

- 如果数据库不存在就创建数据库
- 如果数据库存在,则什么都不做

### default charset

```mysql
CREATE DATABASE 数据库名 DEFAULT CHARSET 字符集;
```

- 字符集如:
  - utf8mb4
    - 每个字符四个字节,更全更合理
  - utf8
    - 不推荐,每个字符三个字节
  - gb2312(不推荐)
  - ascall(不推荐)

### collate

```mysql
CREATE DATABASE 数据库名 COLLATE 排序规则;
```

## 删除(drop)数据库

```mysql
DROP DATABASE 数据库名;
```

- 最简写法
- 如果数据库不存在就报错

### if exists

```mysql
DROP DATABASE IF EXISTS 数据库名;
```

- 如果数据库存在就删除数据库

- 如果数据库不存在,则什么都不做

## 使用(use)数据库

```mysql
USE 数据库名;
```

- 切换数据库去使用它

- 成功返回:

  Database changed

### 查询(select)当前所在数据库叫啥名

```mysql
SELECT DATABASE();
```

- 查询当前数据库
- **括号**,这涉及函数的概念
