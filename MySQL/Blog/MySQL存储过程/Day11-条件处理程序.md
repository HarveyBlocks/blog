# 条件处理程序 Handler

>   用来定义在流程控制结构执行过程中遇到问题时相应的处理
>
>   异常机制?

## 语法

```mysql
Declare [continue|exit] Handler for 状态值1[,状态值2...] statement;
```

-   continue|exit

    ```mysql
    continue -- 继续执行当前程序
    exit -- 终止当前程序
    ```

-   d

    ```mysql
    SQLstate SQLstate_value -- SQLstate_value状态码,如02000
    SQLwarning -- 所有以'01'开头的SQLstate代码的简写
    			-- 类似于所有的RuntimeException都能被Exception捕获
    Not Found -- 所有以02开头的SQLstate代码的简写
    SQLexception -- 所有没有被 SQLwarning和Not Found捕获的代码的简写

    ```

[MySQL ERROR状态码](https://dev.mysql.com/doc/mysql-errors/8.0/en/server-error-reference.html) - ctrl + F 查找

![image-20231025142016920](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/MySQL存储过程/Day11-条件处理程序/image-20231025142016920.png)

![image-20231025142105898](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/MySQL存储过程/Day11-条件处理程序/image-20231025142105898.png)

```mysql
declare exit handler for SQLSTATE '02000'
    close cur;-- '02000' 游标没有数据
-- 这个存储过程中但凡出现 ' 02000 '这种异常,直接exit;mysql
```

