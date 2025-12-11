# SQL提示

 问题引入:

-   ```mysql
    create index user_age_name_gender on user(age,name,gender);
    create index user_age on user(age);
    explain selecct * from user where age = 35;
    ```

-   会使用哪一条索引? 

-   MySQL回决定,怎么决定我不造啊

## 使用SQL提示

-   对于一条字段,它涉及单个索引和联合索引
-   我们可以利用 **SQL提示**  告诉MySQL使用哪一种索引来进行查询

### 语法

-   use index,**至于MySQL接不接受,另一回事**

    ```mysql
    explain select * from user      USE INDEX(USER_AGE)        where age = 35;
    ```

-   ignore index

    ```mysql
    explain select * from user     IGNORE INDEX(USER_AGE)       where age = 35;
    ```

-   force index **强迫MySQL**

    ```mysql
    explain select * from user     FORCE INDEX(USER_AGE)       where age = 35;
    ```

