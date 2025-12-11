```mysql
create function A(i int)-- in i int ,in爱加不加,还只能是in
    returns int deterministic no SQL
    -- 因为MySQL8.0版本自动开启了binary logging,
    -- 所以强制要你描述一下当前存储函数的特性
begin
    declare total int default 0;
    while i>0 do
        set total := total + i;
        set i := i-1;
        end while;
    return total;
end;


```

-   存储函数的特性?



-   然后就可以调用了

```mysql
select A(120);
```





## 存储函数的特性

-   **Deterministic**  相同的参数总是产生相同的结果
-   **No SQL** 不包含SQL语句
-   **Reads SQL Data** 包含读取数据的语句,但不包含写入数据的语句



## 注意

-   存储函数使用的比较少
    -   存储函数能做的,存储过程全能做
    -   存储函数一定要有返回值

