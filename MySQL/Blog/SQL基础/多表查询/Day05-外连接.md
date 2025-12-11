# 外连接

```mysql
SELECR 字段列表 FROM 左表 LEFT  [OUTER] JOIN 右表 ON 条件;
SELECR 字段列表 FROM 左表 RIGHT [OUTER] JOIN 右表 ON 条件;
```



```mysql
select e.* ,s.name from employee e left join section s on e.section_ID = s.id;
select e.* ,s.name from employee e Inner join section s on e.section_ID = s.id;
```

-   区别:对左表section_ID为null的记录的处理不同
    -   外连接显示所有记录(s.name也为null)
    -   内连接忽略null值

