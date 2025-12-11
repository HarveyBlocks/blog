# 自连接

-   就是一张表被引用了两遍,为了区分这两张处于不同时间的同一张表,**一定要起别名**

-   自连接可以是内连接,也可以是外连接

```mysql
select 字段列表 from 表 别名1 JOIN 表 别名2 ON条件
```

-   把一张表看作两张表

领导也是员工

**一定要取别名**

-   自连接-内连接

    ```mysql
    select a.name ,b.name as manager_name
    	from employee a, employee b 
    		where a.manager_ID=b.employee_id;

    select a.name ,b.name as manager_name
    	from employee a 
    		join employee b  on a.manager_ID=b.employee_id;
    ```

    -   没领导者不显现

-   自连接-外连接

    ```mysql
    select a.name, b.name as manager_name
    	from employee a
             left join employee b on a.manager_ID = b.employee_id;
    ```

