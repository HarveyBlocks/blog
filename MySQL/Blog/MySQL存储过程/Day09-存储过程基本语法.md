- 创建

```mysql
create procedure 存储过程名([参数列表]) begain
	-- SQL语句
	
End;
```

- 调用

 ```mysql
call 存储过程名([参数列表]) ;
 ```

-   查看

```mysql
select *
	from information_schema.ROUTINES 
	where ROUTINE_SCHEMA= '数据库名';
	-- 查询指定数据库的存储过程及状态信息
show create procedure 存储过程名; -- 查询存储过程的创建定义
```



## 在命令行里使用存储过程

-   在命令行里,MySQl遇到分号直接结束

```mysql
delimiter $$;
```

-   把两个dollar 符作为结束
-   记得设置回来

-   似乎SQL语句编译时的结束符和读取的结束符是两回事
-   在底层似乎依旧按照分号解析SQL语句
