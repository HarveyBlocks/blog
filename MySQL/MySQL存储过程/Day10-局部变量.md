# 局部变量

-   这个局部是在存储过程,在存储过程中创建,在存储过程中死去
    ```mysql
    create procedure 存储过程名([参数列表]) begain
    	-- SQL语句
    End;
    ```

-   Declare 声明类型

-   局部变量范围在Begain->End

## 声明

```mysql
declare 变量名 变量类型 [default...];
```

## 赋值

```mysql
set 变量名 = 值;
set 变量名:=值;
select 字段名 into 变量名 from 表名;
```

在存储过程中**所有的declare声明语句都要在set赋值语句之前**

```mysql
create procedure p()
begin
    declare s int;
    set s := 23;
    set @a := 23;
    select count(*) into s from user;
    select s;
end;

call p();
```

```mysql
create procedure p()
begin
    declare s int;
    set s := 23;
    declare a int default 23;-- 这是错的
    select count(*) into s from user;
    select s;
end;

call p();
```

