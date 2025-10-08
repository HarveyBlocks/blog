# 联合查询-Union

-   把多次查询结果合并起来,形成一个新的查询结果

```mysql
SELECT 字段列表 FROM 表1
UNION [ALL]
SELECT 字段列表 FROM 表2;
```



-   想要年龄大于20人的信息和2024年之前入职的人的信息

    -   有重复的保留重复

        ```mysql
        select * from employee; -- 34条记录
        
        select name,age,entrydate
                from employee where age>20 -- 34条记录
        union all
            select name,age,entrydate
                from employee where datediff('2024-1-1',entrydate)>0;-- 34条记录
        ```
    
    -   有重复的去重(干脆or算了)
    
        ```mysql
        select * from employee; -- 34条记录
        
        select name,age,entrydate
                from employee where age>20 -- 34条记录
        union
            select name,age,entrydate
                from employee where datediff('2024-1-1',entrydate)>0;-- 34条记录
                
        --  共33条
        ```
        
    -   为何是33条呢?
        
        ```
            2023-10-04,efea,32809,男,23,123051847024301843,实习生,010,00146
            2023-10-04,efea,32810,男,23,123051831255201843,实习生,,""
            ```
        
        这两条,name,age,entrydate皆一致,应为重复信息
