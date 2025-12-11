# 聚合

- 对分组数据执行操作，返回单一结果。
- 分析一段时间内的数据变化。
- 查询最新版本的数据。

作为Nosql, 聚合不仅可以在读操作上执行, 在写上也有特殊操作

- 将多个文档中的值组合在一起(Update)。

## 单一目的聚合方法

聚合单个集合中的文档。方法很简单，但缺乏聚合管道的丰富功能。

| 方法                   | 说明                                 |
| ---------------------- | ------------------------------------ |
| estimatedDocumentCount | 返回集合或视图中文档的近似数量。     |
| count                  | 返回集合或视图中文档的数量。         |
| distinct               | 返回具有指定字段的不同值的文档数组。 |

使用数据库`sample_crud.inventory`进行演示

```json
[ { "_id": {"$oid": "691e64cab68e267e646ca990"}, "item": "canvas", "qty": 100, "size": { "h": 28, "w": 35.5, "uom": "cm" }, "status": "A" }, { "_id": {"$oid": "691e64cab68e267e646ca991"}, "item": "journal", "qty": 25, "size": { "h": 14, "w": 21, "uom": "in" }, "status": "P", "lastModified": {"$date": "2025-11-20T02:06:05.688Z"} }, { "_id": {"$oid": "691e64cab68e267e646ca992"}, "item": "mat", "qty": 85, "size": { "h": 27.9, "w": 35.5, "uom": "cm" }, "status": "A" }, { "_id": {"$oid": "691e64cab68e267e646ca993"}, "item": "mousepad", "qty": 25, "size": { "h": 19, "w": 22.85, "uom": "in" }, "status": "P", "lastModified": {"$date": "2025-11-20T02:06:05.688Z"} }, { "_id": {"$oid": "691e64cab68e267e646ca994"}, "item": "notebook", "qty": 50, "size": { "h": 8.5, "w": 11, "uom": "in" }, "status": "P" }, { "_id": {"$oid": "691e64cab68e267e646ca995"}, "item": "paper", "instock": [ { "warehouse": "A", "qty": 60 }, { "warehouse": "B", "qty": 40 } ] }, { "_id": {"$oid": "691e64cab68e267e646ca996"}, "item": "planner", "qty": 75, "size": { "h": 22.85, "w": 30, "uom": "cm" }, "status": "D" }, { "_id": {"$oid": "691e64cab68e267e646ca997"}, "item": "postcard", "qty": 45, "size": { "h": 10, "w": 15.25, "uom": "in" }, "status": "P", "lastModified": {"$date": "2025-11-20T03:06:05.688Z"} }, { "_id": {"$oid": "691e64cab68e267e646ca998"}, "item": "sketchbook", "qty": 80, "size": { "h": 14, "w": 21, "uom": "cm" }, "status": "A" }, { "_id": {"$oid": "691e64cab68e267e646ca999"}, "item": "sketch pad", "qty": 95, "size": { "h": 22.85, "w": 30.5, "uom": "cm" }, "status": "A" } ]
```

```java
long estimated = collection.estimatedDocumentCount();
long counted = collection.countDocuments();
// estimated = 10, counted = 10
System.out.println("estimated = " + estimated + ", counted = " + counted);
```
```java
DistinctIterable<java.util.Date> distinct = collection.distinct("lastModified",
        java.util.Date.class);
show(distinct);
DistinctIterable<java.util.Date> filtered = collection.distinct(
        "lastModified",
        Filters.ne("item", "mousepad"),
        java.util.Date.class
);
show(filtered);
```

## 聚合管道

聚合管道由一个或多个处理文档的阶段组成, 一个阶段输出的文档将传递到管道中的下一阶段。

使用集合`sample_training.routes`进行演示

```java
MongoDatabase database = mongoClient.getDatabase("sample_training");
MongoCollection<Document> collection = database.getCollection("routes");
```

```java
List<Bson> pipeline = new AggregatePipelineBuilder()
        .append(Aggregates.match(Filters.and(
                Filters.eq("src_airport", "PDX"),
                Filters.eq("stops", 0)
        ))).append(Aggregates.group( // 第一个参数可以是简单的"$airline.name", 那么就是该字段值成为`_id`
    			// 如果特别设置字段名, 则_id不会被在返回结果中
                new Document("_id",// the group target field will be _id
                        new Document(
                            /*the new _id field named as */"airline name", 
                            /*target group filed path is*/"$airline.name"
                )), Accumulators.sum("count", 1)
        )).append(Aggregates.sort(Sorts.descending("count")))
        .append(Aggregates.limit(3))
        .build();
AggregateIterable<Document> aggregate = collection.aggregate(pipeline);
```

- 阶段不必为每个输入文档输出一个文档。例如，某些阶段可能会产生新文档或过滤掉现有文档。
- 使用 `MongoCollection#aggregate()` 方法运行的聚合管道不会修改集合中的文档，除非管道包含 `$merge` 或 `$out` 阶段。
- 同一个阶段可以在管道中多次出现，但阶段(`$out`, `$merge`, `$geoNear`)例外

### 表达式

表达式可以由组件构成

| 组件           | 例子                 |
| -------------- | -------------------- |
| 常量           | `3`                  |
| 操作符         | `$add`               |
| 字段路径表达式 | `"$<path.to.field>"` |

例如`{ $add: [ 3, "$inventory.total" ] }`, 构建抽象语法树这一块

表达式返回3+`$inventory.total`结果

## 字段路径

用于访问文档中的字段

### 嵌套字段

要指定字段路径，需要在字段路径前添加美元符号`$`

用 `"$user"` 指定 `user` 字段的字段路径，或用 `"$user.name"` 指定嵌入式 `"user.name"` 字段的字段路径

```js
db.planets.aggregate([
   {
      $project: {
         user_name: "$user.name"
      }
   }
])
```

转为JavaClient的语法

```java
Aggregates.project(Projections.fields(
        Projections.computed("user_name", "$user.name"),
        Projections.include("count")
))
```

其实`computed`方法映射到的目标字段`user_name`, 也可以是路径, 例如`user.first_name`, 则自动创建内嵌对象

### 内嵌文档数组

演示集合

```json
[
   { item: "journal", instock: [ { warehouse: "A"}, { warehouse: "C" } ] },
   { item: "notebook", instock: [ { warehouse: "C" },{  } ] },
   { item: "paper", instock: [ { warehouse: "A" }, { warehouse: "B" } ] },
   { item: "planner", instock: [ { warehouse: "A" }, { warehouse: "B" } ] },
   { item: "postcard", instock: [ { warehouse: "B" }, { warehouse: "C" } ] }
] 
```

使用`"warehouses": "$instick.warehose"` 进行聚合

```java
List<Bson> pipeline = new AggregatePipelineBuilder()
        .append(Aggregates.project(Projections.fields(
                Projections.computed("warehouses", "$instock.warehouse"),
                Projections.include("item"),
                Projections.excludeId()
        ))).build();
AggregateIterable<Document> aggregate = collection.aggregate(pipeline);
```

结果为

```Json
[
    {"item": "journal", "warehouses": ["A", "C"]},
    {"item": "notebook", "warehouses": ["C"]},
    {"item": "paper", "warehouses": ["A", "B"]},
    {"item": "planner", "warehouses": ["A", "B"]},
    {"item": "postcard", "warehouses": ["B", "C"]}
]
```

创建新数组warehouses, 并键映射到的字段存入该数组

字段unset的, 不会被聚合进新数组

### 字段是数组的内嵌文档数组

```js
db.fruits.insertOne({inventory: [
		{apples: ['macintosh', 'golden delicious']},
		{oranges: ['mandarin']},
		{apples: ['braeburn', 'honeycrisp']}
]});
```

```java
Aggregates.project(Projections.fields(
        Projections.computed("apples", "$inventory.apples"),
        Projections.include("item"),
        Projections.excludeId()
))
```

返回结果

```json
{"apples": [["macintosh", "golden delicious"], ["braeburn", "honeycrisp"]]}
```

### $CURRENT

`"{0}<field>"` 等效于 `"$CURRENT.<field>"`

`$CURRENT` 为系统变量，而它默认为当前对象的根

## 聚合阶段

- `$project`

  - 能排除字段
  - 能选择字段
  - 能给字段取别名
  - 能在映射的时候做一些$O(1)$的运算

- `$set` | `$addField` |`$unset`

  - `$unset` 只移除字段, `$project` 会重新构建文档结构

- `$unwind: $path.filed` 

  展开目标数组字段的元素如果是

  - 过滤null/unset/empty

  - 文档

    ```json
    { _id: 1, item: "ABC1", sizes: [ "S", "M", "L"] }
    ```

    unwind后

    ```json
    { _id: 1, item: "ABC1", sizes: "S" }
    { _id: 1, item: "ABC1", sizes: "M" }
    { _id: 1, item: "ABC1", sizes: "L" }
    ```

  - 如果目标字段不是数组字段, 则保留原文档不变

- `$match`

- `$group`

  Accumulators 在分组的同时进行的工作

  - first(assignTo, value)
  - sum(assignTo, weight), 加权累和
  - push(assignTo, value)

- `$sort`

- `$skip`

- `$limit`

## 限制

- 返回结果大小限制

  - 如果使用Cursor, 则无限
  - 否则, 每个文档大小不得大于16MB(Bson)
  - 在管道处理过程中允许大于这个限制

- 阶段数量限制: 1000个

  一个聚合管道里最多有1000个聚合阶段

## Update

### `$set`

`$set`后跟随一个或多个字段的映射, 如果字段存在则更新, 不存在则加入字段

```js
db.students.updateOne( 
    { _id: 3 }, [ { $set: { 
        "test3": 98,  // 不存在, 插入字段"test3"
        modified: "$$NOW" // 存在, 更新
    }}] 
)
```

如果值是以美元字符开头的字符串, 为了防止将其解析为对字段的引用，则需要将该值传递给 `$literal`聚合操作符。

```javascript
db.inventory.updateOne( { _id: 1 }, [ { $set: { "cost": { $literal: "$27" } } } ] )
```

