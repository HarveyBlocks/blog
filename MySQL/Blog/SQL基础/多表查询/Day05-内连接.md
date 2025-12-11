# 内连接

-   隐式内连接
-   显式内连接
-   区别:INNER JOIN做提示表**显式**

## 隐式内连接

```mysql
SELECT 字段列表 FROM 表1 ,表2 WHERE 条件;
```

-   起别名

```mysql
SELECT e.name s.name FROM employ e ,section s WHERE e.section_ID=s.ID;
```

-   起了别名之后就不能使用原来的名字了

# 显式内连接

```sql
SELECT 字段列表 FROM 表1 [INNER] JOIN 表2 ON 条件;
```

-   显式内连接有DataGrip智能加持

-   你就说JOIN-WHERE配能不能运行吧

```mysql
SELECT e.name , s.name 
FROM employee e 
JOIN section s 
where e.section_ID=s.id;
```

-   还真能
    -   你说这么写好不好吧--我不造啊

