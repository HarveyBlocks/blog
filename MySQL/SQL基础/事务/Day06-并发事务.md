# 并发事务

-   就是两个客户端同时mysql

## 并发事务问题

![image-20231009155620510](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/SQL基础/事务/Day06-并发事务/image-20231009155620510.png)

-   脏读
    -   对数据库产生更改的事务还**未提交**,就被另一个事务录取到了更改后的数据
-   不可重复读
    -   在一事务两次查询数据库之间,另一事务对数据库进行了更改并**提交**
-   幻读
    -   事务查询一条数据,没发现这条数据,就插入这条数据,但是在此之前,另一事务就已经对数据库插入了这条数据

## 事务隔离级别

![image-20231009161629487](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/SQL基础/事务/Day06-并发事务/image-20231009161629487.png)

-   默认是指MySQL的默认
-   **数据的安全性越高,性能就越差**
-   Serializable**串行化** 

### 相关命令

-   查询当前事务隔离级别

```mysql
SELECT @@TRANSACTION_ISOLATION;
```

-   设置事务隔离级别

```mysql
SET [SESSION|GLOBAL] TRANSACTION ISOLATIOn LEVEL 事务隔离级别
```

-   事务隔离级别使用范围
    -   SESSION 针对当前客户端窗口
    -   GLOBAL 针对所有客户端窗口 
    -   缺省 仅对下一个事务生效. 
        -   下一个事务提交后，会恢复为session的事务级别
        -   该语句在事务中是不允许的。(必须写在commit后begin/START Transaction 前)

