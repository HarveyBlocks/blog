# 全局锁

## 介绍

>   对整个数据库实例加锁
>
>   加锁后整个实例处于只读状态
>
>   DML和DDL语句,依据更新操作的事物提交语句都将被阻塞

## 作用与应用场景

-   全库的逻辑备份(mysqldump-->MySQL的备份工具)
    -   对所有表进行锁定,从而获得一致性视图,保证数据的完整性

## 使用,操作与语法

-   加锁

    ```mysql
    Flush table with read lock;
    ```

-   备份

    ```DQS
    MysqlDump -uroot -p密码 数据库>目标SQL文件路径.sql
    ```

-   释放锁

    ```mysql
    unlock tables;
    ```



## 实操

-   成功

## 缺点

![image-20231025234350967](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/锁/Day10-全局锁/image-20231025234350967.png)

-   不加全局锁:

```Dos
MysqlDump -single-transaction -uroot -p密码 数据库>目标SQL文件.sql
```

-   依据快照读实现的

