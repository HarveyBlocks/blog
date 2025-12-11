# 标量子查询

![image-20231008164546829](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/SQL基础/多表查询/子查询/Day05-标量子查询/image-20231008164546829.png)

```mysql
SELECT * FROM 表1 WHERE 某字段=(SELECT 字段2 FROM 表2) 
```

## 实践

-   查询**开发部**的所有员工信息
    1.  查询"开发部"ID
    2.  查询**开发部**的所有员工信息

```mysql
select id
from section
where name = '开发部';
-- '010'
select name, gender, age, entrydate, level
from employee
where section_ID = '010';
```

↓

```mysql
select name, gender, age, entrydate, level
from employee
where section_ID = (select id
                    from section
                    where name = '开发部');
```

-   查询和Mike同一个部门的人

    -   错解:

        ![image-20231008170854608](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/SQL基础/多表查询/子查询/Day05-标量子查询/image-20231008170854608.png)

```mysql
select name
from employee
where section_ID = (select section_ID
                    from employee
                    where name = 'Mike');
```

-   查询**开发部** **组长**的**入职日期**之前的**开发部成员**的信息

    1.  查询**开发部**的ID
    2.  查询**开发部** 的**组长**
    3.  查询该**组长**的**入职日期**
    4.  查询**入职日期**之前的**开发部成员**的信息

    -   错误:
    -   ![image-20231008171825743](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MySQL/SQL基础/多表查询/子查询/Day05-标量子查询/image-20231008171825743.png)
    -   正确:

```mysql
select e.name, e.gender, e.age, e.entrydate, e.level
from employee as e
where entrydate < (select entrydate_010.entrydate
                   from (select level, entrydate
                         from employee
                         where section_ID = (select id
                                             from section
                                             where name = '开发部')) as entrydate_010
                   where entrydate_010.level = '组长')
          &&
      section_ID = (select id
                    from section
                    where name = '开发部') ;
```

-   这好像是表子查询耶??

### 全程量词和存在量词

Mysql里只有存在量词`EXIST`, 所以要实现全程量词只有把存在量词否定

```mysql
select `s`.`name`,s.id
from `student` s
-- 如果不存在(没有被这个人选的)课程, 那就是选择了全部的课程
where NOT EXISTS(select c.id -- 那些没有被这个人选的课程
                   from `course` c
                   where NOT EXISTS(
                             select cs.id
                             from `course_student` cs
                             where `cs`.`student_id` = `s`.`id`
                               AND `cs`.`course_id` = `c`.`id`
                             ));

```

