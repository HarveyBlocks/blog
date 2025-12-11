# 前缀索引

-   如果一个字段里存的是一些很长的字符串,

    查询时考虑这些字符串的每一个字符,建索引也会使用所有字符

    **很占空间,效率很低**

-   这时候就可以截取字符串的前几个字符作为这个字符串的代表

-   要称为**代表**,

    意味着他能足够代替原来的字符,

    意味着原来的字符是唯一的,

    那么这个代表也要尽可能唯一

-   这时候联想到学习**Git**时,那个上传编号,很长,但截取前几个就够了

那么,截取前**几个**才能在效率,空间,上取得win win呢?

## 截取个数公式

```mysql
count(distinct substr(name,1,n))/count(*)
```

n的合适的较小值就是最终可以截取的长度

```mysql
select substr(name ,1,10) from user;
# 与 select name from user; 等价

select count(substr(name,1,10))/count(*) from user; 
# 未去重,此返回必定是1.000
select count(distinct substr(name,1,10))/count(*) from user;
# name去重之后的比值称为选择性
select count(distinct substr(name,1,9))/count(*) from user
;# 改变截取的长度,选择性为1.0000
select count(distinct substr(name,1,8))/count(*) from user;
# 改变截取的长度,选择性为1.0000
select count(distinct substr(name,1,7))/count(*) from user;
# 改变截取的长度,选择性为1.0000
select count(distinct substr(name,1,6))/count(*) from user;
# 改变截取的长度,选择性为1.0000
select count(distinct substr(name,1,5))/count(*) from user;
# 改变截取的长度,选择性为0.9812
select count(distinct substr(name,1,4))/count(*) from user;
# 改变截取的长度,选择性为0.9527
select count(distinct substr(name,1,3))/count(*) from user;
# 改变截取的长度,选择性为0.7642
select count(distinct substr(name,1,2))/count(*) from user;
# 改变截取的长度,选择性为0.1211
select count(distinct substr(name,1,1))/count(*) from user;
# 改变截取的长度,选择性为0.0036
```

-   对于不同业务,选择性在哪个值以上都不同
-   对于相同的选择性,例如这里6<=n<=10的情况,要求n尽可能的小,以节省空间
-   当n = 5 时,这个选择性的值较高,这时候就要权衡是要空间(选n=5)还是时间(选n=6)
-   当然,n小到一定程度之后,选择性就会很差
-   当然,实际业务中字符串的长度可能有所不同,n最开始的选值偏大没事

## 创建前缀索引

```mysql
create index 索引名 on 表名(字段名(合适的前缀长度));
```

-   这里决定取**n=5**

```mysql
create index user_name_5 on user(name(5));

create index user_age_name_gender on user(age,name(5),gender);
# 这么写也当然没啥问题

```

建好了前缀索引之后:

```mysql
show index from user;
```

返回的sub_part是5,而其他的是null

-   前缀索引在返回数据前,会把截取到的数据和条件进行完整的比较
-   这一点上,它和普通索引的区别在于完整比较的次数少了
-   但还是会比较
-   所以不用担心会返回多条前缀相同,后面不同的数据

emmm

## 奇奇怪怪的想法:

-   结合模糊查询和前缀索引,何如?应该是前缀和模糊比较吧?

```mysql
select * from user where name like "A%B%";
```

-   那么它会前5位当作`"A%"`会比较合理咯?

