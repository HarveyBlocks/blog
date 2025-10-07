# limit优化

## 问题

越往后速度越慢

![image-20231023162750619](../../shoot/Day09-limit优化/image-20231023162750619.png)

## 解决方案: 覆盖索引+子查询

```mysql
create index user_name_age_gender on user(name,age,gender);
```

-   先创建一个覆盖索引

```mysql
select * from user
	where id in(
    	select id from user
	    order by id
        limit 9000000,10 
    ); 
```

-   **这个是不被支持的语法**

```mysql
select user.* from user, 
	(
    	select id from user
	    order by id
        limit 9000000,10 
    )  user1
    where user.id = user1.id; 
```

-   