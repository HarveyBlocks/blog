# 游标

-   概念:

    >   Cursor 是用来存储查询**结果集**的数据类型
    >
    >   在存储过程和函数中可以使用游标对**结果集**进行循环的处理

## 使用

-   包括游标的声明,Open,Fetch,Close

### 语法

-   声明游标

    ```mysql
    Declare 游标名 cursor for 查询语句;
    ```

-   打开游标

    ```mysql
    open 游标名;
    ```

-   获取游标记录

    ```mysql
    fetch 游标名 into 变量[,变量];
    ```

-   关闭游标

    ```mysql
    Close 游标名;
    ```

```mysql
-- 根据传入的参数 age_low ,age_high
-- 查询用户表employee中在low-high之间(闭区间)的员工名和性别
-- 并将employee 的姓名和性别插入到一张新创建的表(id,name,gender)


create procedure p4(in age_low int,in age_high int)begin

    declare e_name varchar(5);
    declare e_gender char(1) ;
    -- 游标的声明应该在其他变量声明之后
    Declare cur cursor for
    select name,gender
        from employee
        where age_low<=age and age<=age_high
    ;
    -- 游标的声明应该在其他变量声明之后
    declare exit handler for NOT FOUND
        close cur;
    -- '02000' 游标没有数据
    -- 这个这个存储过程在条件处理声明之后
    -- 但凡出现 '02000' 这种异常,直接exit;

    drop table if exists emp;
    create table if not exists emp(
        id int primary key auto_increment,
        name varchar(5),
        gender char(1) 
    );

    open cur;
    catch:loop
    	START Transaction;
        	fetch cur into e_name,e_gender;
        	insert into emp(name, gender)
            	values (e_name,e_gender);
        commit ;
        rollback ;
    end loop ;

end;

drop  procedure if exists p4;

call p4(25,26);

select * from emp;
```