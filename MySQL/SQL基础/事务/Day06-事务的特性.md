# 事务的四大特性(ACID)

![image-20231009155146287](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/SQL基础/事务/Day06-事务的特性/image-20231009155146287.png)

-   原子性
    -   **转账案例**,存钱失败了,取钱的行为也不应该被执行
-   一致性
    -   钱的总量不能变
-   隔离性
    -   A,B事务并发进行,A,B事务应不互相影响
-   持久性
    -   事务的更改时永久的,因为存到了磁盘里

