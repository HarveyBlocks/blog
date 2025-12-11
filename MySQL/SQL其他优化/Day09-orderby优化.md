# Order By 优化

Extra会提示的两条OrderBy信息:

-   Usering filesort

    1.  通过表达索引或全表扫描,
    2.  读取满足条件的数据行,
    3.  排序缓冲区sort buffer中完成

    -   **所有不是通过索引直接返回排序结果的排序都是FileSort**

-   Using index

    -   通过有序索引顺序扫描直接返回有序数组
    -   **不需要额外排序,操作效果高**
    -   不要违背索引的几个法则,法则对ORDER_BY也有效

-   Backward index scan
    -   倒序扫描
    -   这个就是字面意思理解

## Order By缓冲区

```mysql
show variables like 'sort_buffer_size';
-- 查看缓冲区大小(Byte)
-- 默认256KB

-- 业务中可以把这个数据适当上调

```

-   如果缓冲区不够用,SQL就会去磁盘里写文件,更慢了

## 创建有序的索引

-   索引默认是升序排的

```mysql
create [unique|fullText] index 取一个索引名 on 
	表名(
    	字段1 [asc|desc] 
        [,
         字段2 [asc|desc],
         字段3 [asc|desc]
         ,....
        ]
    );
```

## 针对多字段排序建立索引

```mysql
creat unique index user_phone_age on user(
	phone,
    age,
);#索引一
creat unique index user_phone_age on user(
	phone desc,
    age,
);#索引二

```

```mysql
select id,phone,age from user order by phone asc  ,age asc;  # 索引一
select id,phone,age from user order by phone asc  ,age desc; # 索引二倒序扫描
select id,phone,age from user order by phone desc ,age asc;  # 索引二
select id,phone,age from user order by phone desc ,age desc; # 索引一倒叙扫描
```

