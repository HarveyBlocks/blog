-   操作MySQl
-   多次操作MySQL
-   多次网络请求
-   把多条语句封装到一个SQL集合

# 存储过程

-   事先经过编译并存储在数据库中的一段SQL语句的集合
-   调用存储过程简化工作
-   减少数据在数据库和应用服务器之间的传输,提高数据处理效率
-   代码的封装与重用



## 特点

-   封装,复用
-   接收参数,返回数据

## 参数与返回

```mysql
create procedure 过程名字([in|out|inout] 参数 参数类型)
begain:

end;
```

-   默认in,参数
-   out返回值
-   inout即是参数,也是返回值



-   分数转换百分制

```mysql
create procedure changeIt( score int, full int,out result double)
begin
	set result := score*100/full;
end;

call changeIt(121,150,@ans);

select @ans;

drop procedure changeIt;
```

-   两百分转百分制(使用inout)

```mysql
create procedure changeIt( inout score double)
begin
	declare full int default 200;
	set score := score*100/full;
end;-- 结束之后调用参数赋值给用户自定义变量

set @ans:=121;
call changeIt(@ans);
select @ans;

drop  procedure if exists  changeIt ;
```

