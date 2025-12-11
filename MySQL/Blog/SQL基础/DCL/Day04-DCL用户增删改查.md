# 管理用户

## 查询用户

```mysql
USE mysql;
SELECT * FROM user;
```

## 创建用户

```mysql
CRESTE USER '用户名'@'主机名' IDENTIFIED BY '密码';
```

-   主机名:
    -   'localhost' - 仅限本地访问
    -   '%' - 任意机器访问



## 修改密码

```mysql
ALTER USER '用户名'@'主机名' IDENTIFEIED WITH mysql_native_password BY  '新密码';
```


## 删除用户

```mysql
DROP USER '用户名'@'主机名';
```

