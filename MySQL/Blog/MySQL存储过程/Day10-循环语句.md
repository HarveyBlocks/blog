```mysql
while (条件) do
	SQL语句
end while;
```

-   条件控制



-   repeat 满足条件就退出

```mydql
repeat 
	SQL语句;
	Until 条件
End repeat;
```





-   loop

    ```mysql
    [begin_label] loop
    	SQL语句
    End Loop[end_label];
    ```

-   退出指定标记的循环体(break)

    ```mysql
    level label;
    ```

-   进入下一次循环

    ```mysql
    iterate label;
    ```

-   案例

```mysql
create procedure p3(in n int)begin
        declare total int default 0;
        set total=1;

    sum:loop
        if n<=0 then
            leave sum;
        end if;
        if n%2=1 then
            set n:=n-1;
            iterate sum;
        end if;
        set total := total+n;
        set n:=n-1;
    end loop ;-- end loop sum 意义不大
    select total;
end ;
drop  procedure if exists p3;

call p3(100);
```



















如果不增加
