# 表子查询

![image-20231009001258973](../../../../assets/Day05/image-20231009001258973.png)

-   缝合怪: 结合了行子查询的()元组和列子查询的IN

## 示例

-   选出与Amy或Mike(性别和年龄)一致的员工信息

```mysql
select name, gender, age, entrydate, level
from employee
where (gender, age) in (select gender,age
                        from employee
                        where name in ('Mike', 'Amy'));
```

-   有in了还用什么or 啊

