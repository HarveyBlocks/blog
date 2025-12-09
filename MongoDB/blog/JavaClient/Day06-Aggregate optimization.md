# 聚合优化

聚合管道操作包含一个优化阶段，该阶段会尝试重塑管道以提高性能

优化可能因版本而异

查看优化器的优化方式

```java
AggregateIterable<Document> aggregate = collection.aggregate(pipeline);
Document explain = aggregate.explain(); // 返回一个优化后的聚合操作的文档
```



## 投影优化

使用 `$project` 时，通常应该是管道的最后一个阶段。

在管道的开头或中间使用 `$project` 阶段来减少传递到后续管道阶段的字段数量不太可能提高性能，因为数据库会自动执行此优化。

聚合管道可确定是否只需文档中的部分字段即可获取结果。

如果是，管道则仅会使用这些字段，从而减少通过管道传递的数据量。

## 序列优化

### （`$project`|`$unset`|`$addFields`|`$set`）+ `$match` 

`$project`|`$unset`|`$addFields`|`$set` (称为***`投影阶段`***)之后**紧随**`$match`, MongoDB会将`$match`中

**无需使用**投影阶段**计算**的值的所有Filter移动到投影前的新的 `$match` 阶段。



优化之前:

```json
{$addFields: {
      maxTime: { $max: "$times" },
      minTime: { $min: "$times" }
}},
{$project: {
      _id: 1,
      name: 1,
      times: 1,
      maxTime: 1,
      minTime: 1,
      avgTime: { $avg: ["$maxTime", "$minTime"] }
}},
{$match: {
      name: "Joe Schmoe",
      maxTime: { $lt: 20 },
      minTime: { $gt: 5 },
      avgTime: { $gt: 7 }
}}

```

优化之后:

```json
{$match: { 
    name: "Joe Schmoe"  // name 不参与计算
}},
{$addFields: {
    maxTime: { $max: "$times" },
    minTime: { $min: "$times" }
}},
{$match: { 
    maxTime: { $lt: 20 },  // maxTime 和 minTime 的值不会在之后改变
    minTime: { $gt: 5 } 
}},
{$project: {
    _id: 1, 
    name: 1, 
    times: 1, 
    maxTime: 1, 
    minTime: 1,
    avgTime: { $avg: ["$maxTime", "$minTime"] }
}},
{$match: { 
    avgTime: { $gt: 7 } 
}}
```

Java如下

```java
List<Bson> pipeline = new AggregatePipelineBuilder().append(Aggregates.addFields(
                new Field<>("maxTime", ExpressionDocument.of().max(FieldPath.refer("times"))),
                new Field<>("minTime", ExpressionDocument.of().min(FieldPath.refer("times")))
        ))
        .append(Aggregates.project(Projections.fields(Projections.include(
                "name",
                "times",
                "maxTime",
                "minTime"
        ), Projections.computed("avgTime", ExpressionDocument.of().avg(List.of(
                FieldPath.refer("maxTime"), FieldPath.refer("minTime")
        ))))))
        .append(Aggregates.match(Filters.and(
                Filters.eq("name", "Joe Schmoe"),
                Filters.lt("maxTime", 20),
                Filters.gt("minTime", 5),
                Filters.gt("avgTime", 7)
        )))
        .build();
```



如果聚合管道包含多个投影或 `$match` 阶段，MongoDB 会对每个 `$match` 阶段执行此优化，将每个 `$match` 移到Filter不依赖的所有投影阶段之前。

### `$sort`+`$match`

match会移动到sort之前, 以减少sort需要排序的文档数量

### (`$project`|`$unset`)+`$skip`

当(`$project`|`$unset`)紧接`$skip`, `$skip`操作会前移

(`$project`|`$unset`)会创建新的文档, 先执行`$skip`操作有助于减少新文档的创建

```json
{ $sort: { age : -1 } },
{ $project: { status: 1, name: 1 } },
{ $skip: 5 }
```

优化后

```json
{ $sort: { age : -1 } },
{ $skip: 5 },
{ $project: { status: 1, name: 1 } }
```





## 合并优化

将管道阶段合并到其前置阶段中

合并发生在任何序列重新排序优化**之后**

### `$sort`+`$limit`

sort之后如果没有改变文档数量的操作(例如`$group`和`$unwind`), 则limit会合并入`sort`操作

```json
{ $sort : { age : -1 } },
{ $project : { age : 1, status : 1, name : 1 } },
{ $limit: 5 }
```

优化后

```json
{ "$sort" : {
       "sortKey" : { "age" : -1 },
       "limit" : Long(5)
}},
{ $project : { age : 1, status : 1, name : 1 } }
```

### `$limit` + `$limit` 

两个`$limit`相邻, 选取小的那个

```json
{ $limit: 100 },
{ $limit: 10 }
```

优化

```json
{ $limit: 10 }
```

### `$skip` + `$skip` 

两个`$skip`相邻, 值相加

```json
{ $skip: 5 },
{ $skip: 2 }
```

优化

```json
{ $skip: 7 }
```

### `$match ` + `$match ` 

两个`$match `相邻, 则合并

```json
{ $match: { year: 2014 } },
{ $match: { status: "A" } }
```

优化

```json
{ $match: { $and: [ { "year" : 2014 }, { "status" : "A" } ] } }
```



### `$lookup`、`$unwind` 和 `$match` Coalescence

同时满足以下条件

1.  `$lookup` 后马上是 `$unwind`
2.  `$unwind` 作用在 `$lookup` 的 `as` 字段上
3. `$unwind` 后马上是 `$match` 
4. `$match`比较的字段`$unwind` 目标字段的子字段

则优化器将 `$unwind` 合并到 `$lookup` 阶段, 同时合并 `$match`

这样可以避免创建大型中间文档



例如，一个管道包含以下序列：

```json
{
   $lookup: {
     from: "otherCollection",
     localField: "x",
     foreignField: "y",
     as: "resultingArray"
   }
},
{ $unwind: "$resultingArray"  },
{
  $match: {
    "resultingArray.foo": "bar"
  }
}
```

优化器将 `$unwind`和 `$match` 阶段合并到 `$lookup` 阶段。如果使用 `explain` 选项运行聚合，`explain` 输出将显示合并阶段：

```json
{
   $lookup: {
     from: "otherCollection",
     localField: "x",
     foreignField: "y",
     as: "resultingArray",
     let: {},
     pipeline: [
       {
         $match: {
           "foo": {"$eq": "bar"}
         }
       }
     ],
     unwinding: {
       "preserveNullAndEmptyArrays": false
     }
   }
}
```

`unwinding` 字段指该管道如何在内部进行优化, 和`$unwind`操作无关