# 日期函数

![image-20231007162807543](../../../../../JDK/JavaDailyBlog/blog/java基础/image-20231007162807543.png)

```mysql
select curdate(),curtime(),now();
select year(now()),month(now()),day(now());
select hour(now()),minute(now()),second(now());

select date_add(curtime(),interval 1 Day) ; -- 2023-10-08 16:35:43
select date_add(curtime(),interval -1 hour ) ; -- 15:36:28
-- interval 间隔,往后记一

select datediff(now(),date_add(now() ,interval -1 hour)); -- 0
select datediff(now(),date_add(now() ,interval 1 Day)); -- -1

use company;
select name,
       datediff(now(),entrydate) as days 
    from employee 
    order by days desc;
```

## 其他好用的日期函数

### 用时间戳计算指定时间间隔

```mysql
select TIMESTAMPDIFF(
               DAY,
               '2020-02-01 14:02:36',
               '2021-02-01 14:10:57'
       ) as td;
       -- >>>366

select TIMESTAMPDIFF(
               DAY,
               '2020-02-01 12:20:12',
               '2021-02-01 10:59:59'
       ) as td;
       -- >>>366

select TIMESTAMPDIFF(
               DAY,
               '2020-02-01',
               '2021-02-01 23:59:59'
       ) as td;
       -- >>>366
```

### 计算秒间隔

```mysql
select 
	timediff(
    	now(),
    	date_add(
        	now() ,interval 1 SECOND
    	)
	); -- -00:00:01
```

```mysql
select datediff(); -- 前减去后
select timediff(); -- 前减去后
select timestampdiff(); -- 后减去前
```

