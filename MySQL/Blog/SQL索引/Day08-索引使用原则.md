# 索引使用原则

## 最左前缀法则

>    如果建表时索引了多列(联合索引)

```mysql
create index user_age_name_gender on user(age,name,gender);
# 和建表时候的 id name age gender无关,在这里重新定义了顺序
```

>   查询从索引的最左列开始
>
>   ​	`where age = 35 and name ='Ahajksddsa' and gender = '女'`√
>
>   ​	`where name ='Ahajksddsa' and gender = '女'` ×
>
>   (必须存在,存在即可,不在意查询顺序)
>
>   ​	`where name ='Ahajksddsa' and age=35 and gender = '女'` √
>
>   ​	`where age = 35 and gender = '女'` ×
>
>   并且不跳过其**中**的某一列
>
>   `where age = 35 and gender = '女'` ×
>
>   `where age = 35 and name = 'adshkjejkczx'` √

>   如果跳过某一列
>
>   后面字段的索引将失效



-   测出不一样啊

## 范围查询法则

>联合索引
>
>使用>=或<=替代>或<

>   如果使用范围(>,<)查询
>
>   后面字段的索引将失效

## 列计算法则

### 56原因

>   任意索引
>
>   在条件里计算

>   ```mysql
>   select * from user where substring(name,2,5) = 'A';
>   ```

>   索引失效

## 字符串引号法则

>   任意字符串索引
>
>   字符串的查询不加引号(不是根本不能查吗)
>
>   使用了类型转换计算
>
>   索引失效

## 模糊查询法则

>   头部模糊匹配, 索引失效
>
>   尾部索引不失效

```mysql
explain select * from user where name like 'A%B%';
```

-   有效

## OR连接条件

>   OR分割的条件:
>
>   1.  如果OR前的条件中的列有索引
>
>   2.  后面列中没有索引
>
>       -> 那么涉及的索引**都**不会被用到
>       
>   3.  or前后的条件都建索引就好啦

## 数据分布

>    MySQL判断使用索引会比全表扫描还慢,就会使用全表扫描而不是索引

-   MySQL你这个**判断**好离谱啊
-   这个possible-key还是在的,key不在了

-   所以`is null`和`is not null`是否用索引也是这个知识点

![image-20231022153019409](../../shoot/Day08-索引使用原则/image-20231022153019409.png)
