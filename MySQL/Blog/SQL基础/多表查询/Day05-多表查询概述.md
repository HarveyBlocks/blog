# 多表查询概述

```mysql
select * from employee,section;
```

-   结果很奇妙
-   笛卡尔积
    -   集合A和集合B所有组合情况

```mysql
select * from employee,section where employee.section_ID = section.id;
```

## 分类

-   连接查询
    -   内连接(AB)
    -   外连接
        -   左外连接(A)
        -   右外连接(B)
    -   自连接(必须使用表别名)
-   子查询

