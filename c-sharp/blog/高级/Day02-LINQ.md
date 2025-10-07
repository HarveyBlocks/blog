# LINQ

-   关系数据库的 SQL 

-   XML 的 XQuery
-   C# Entity对象
-   C# 数组, 集合

提供C# 统一语言模型

```csharp
var query = from student in students
    where student.Age == 18
    orderby student.Score.Chemistry ascending
    group student by student.Score.Chemistry
    into sameChemistry
    select sameChemistry.Count();
query.ToList().ForEach(Console.WriteLine);
```