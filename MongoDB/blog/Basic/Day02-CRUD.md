# CRUD

## 插入

- `db.<collection>.insertOne()`
  - 返回一个包含 `_id` 字段的对象
- `db.<collection>.insertMany()`



```js
use sample_mflix

let insertOne = db.movies.insertOne({
    title: "The Favourite",
    genres: ["Drama", "History"],
    runtime: 121,
    rated: "R",
    year: 2018,
    directors: ["Yorgos Lanthimos"],
    cast: ["Olivia Colman", "Emma Stone", "Rachel Weisz"],
    type: "movie"
});
console.log(insertOne.insertedId); // 获取插入后的ID
```

虽然看似不包含`_id`, 其实有自动生成

```js
use sample_mflix

let insertMany = db.movies2.insertMany([
    {
        title: "The Favourite1"
    }, {
        title: "The Favourite2"
    }, {
        title: "The Favourite3"
    }
]);
console.log(insertMany.insertedIds); // empty, why?


```

## 查询

- `db.<collection>.find()`, 需要一个Filter查询过滤器作为参数

### 查询全部

读取集合中全部的文档就传入空的参数, 返回一个Cursor对象

```js
db.movies.find();
```

### 相等条件查询

```js
db.<collection>.find({ // 查询过滤器
    <field>: <value>
    [, ...]
});
```

```js
db.movies.find( { "title": "Titanic" } )
```

使用Cursor和Project

```js
db.<collection>.find({
    <field>: {
        <operator>: <condition>
    }
    [, ...]
}[,{	// 第二个参数可选, 对字段的映射 project
  	<filed>: 0|1 
    // 此Poject中只出现0, 没提到的全置1; 
    // 此Poject中只出现1, 没提到的全置0;
    // 混合使用的情况, 没提到的全置1 
    [, ...]
}]);
```

```js
let cur = db.movies.find({
    rated: {
        $in: ["PG", "PG-13"]
    }
}, {
    _id: 1,
    rated: 0
});
while (cur.hasNext()) {
    console.log(cur.next().title);
}
```

### 查询操作符

```js
db.<collection>.find({
    <field>: {
        <operator>: <condition>
    }
    [, ...]
}[<Project>]);
```

例如使用operator: `$in`, 表示字段值在这个集合里的就匹配

```js
db.movies.find({
    rated: {
        $in: ["PG", "PG-13"]
    }
})
```

其余的查询操作符(查询运算符/查询谓词)还有

| 名称         | 说明                                                         |
| :----------- | :----------------------------------------------------------- |
| `$all`       | 匹配包含查询中指定的所有元素的数组。                         |
| `$and`       | 使用逻辑 `AND` 连接查询子句，并返回与所有子句的条件匹配的文档。 |
| `$elemMatch` | 如果大量字段中至少有一个元素与所有指定的 `$elemMatch` 条件匹配, 则命中 |
| `$eq`        | 匹配等于指定值的值。                                         |
| `$exists`    | 匹配具有指定字段的文档。                                     |
| `$gt`        | 匹配大于指定值的值。                                         |
| `$gte`       | 匹配大于等于指定值的值。                                     |
| `$in`        | 匹配数组中指定的任何值。                                     |
| `$lt`        | 匹配小于指定值的值。                                         |
| `$lte`       | 匹配小于等于指定值的值。                                     |
| `$mod`       | 根据对字段值进行模运算的结果来匹配文档。                     |
| `$ne`        | 匹配所有不等于指定值的值。                                   |
| `$nin`       | 如果该值不等于任何给定值列表，则匹配。                       |
| `$nor`       | 使用逻辑 `NOR` 连接查询子句，并返回未能匹配所有子句的所有文档。 |
| `$not`       | 反转查询谓词的效果，并返回与查询谓词*不*匹配的文档。         |
| `$or`        | 使用逻辑 `OR` 连接查询子句，并返回至少匹配一个子句的所有文档。 |
| `$regex`     | 匹配值与指定正则表达式匹配的文档。                           |
| `$size`      | 如果大量字段包含指定数量的元素，则选择文档。                 |
| `$type`      | 如果字段属于指定类型，则匹配文档。                           |

### 逻辑操作符

`$and` 和`$or`操作符

```js
db.<collection>.find({
    $or|$and: [
    	{<fild1>:<condition1>},
    	{<fild2>:<condition2>}
        [,...]
    ]
});
```





```js
db.movies.find( {
    $or: [ { countries: "USA"  }, {genres: "Drama" } ]
} )
```



## 更新

### 更新操作符

更新文档格式

```js
{
 	<operator>: {
        <field>: <value>
        [,...]
    }
    [,...]
}
```

`$set`会在字段不存在的情况下创建字段

`$currentDate` 将指定字段的值设定成当前时间

### 更新单个文档

```js
db.<collection>.updateOne(<QueryFilter>, <UpdateOperatorDoc>);
```

```js
let updateOne = db.movies.updateOne({
    title: "Twilight"
}, {
    $set: {
        plot: "A teenage girl risks everything–including her life–when she falls in love with a vampire."
    }, $currentDate: {
        lastUpdated: true
    }
});
```

返回值为

```js
let updateOne = {
    "acknowledged": true,
    "insertedId": null,
    "matchedCount": new NumberLong("1"),
    "modifiedCount": new NumberLong("1"),
    "upsertedCount": new NumberInt("0")
}
```

### 更新多个文档

```js
db.<collection>.updateMany(<QueryFilter>, <UpdateOperatorDoc>);
```

```js
let updateMany = db.listingsAndReviews.updateMany({
    security_deposit: {
        $lt: 100
    }
}, {
    $set: {
        security_deposit: 100,
        minimum_nights: 1
    }
})
```

返回值

```js
let updateMany = {
    "acknowledged": true,
    "insertedId": null,
    "matchedCount": new NumberLong("0"),
    "modifiedCount": new NumberLong("0"),
    "upsertedCount": new NumberInt("0")
}
```

### 替换文档

替换除了`_id`字段之外的文档的所有内容, 第二个参数就是一个新的文档而不包含更新操作符

```js
db.<collection>.replaceOne(
	<QueryFilter>,
    <NewDocument>
);
```

```js
use sample_mflix


let replaceOne = db.movies.replaceOne({
    title: "The Great Train Robbery"
}, {
    "title": "The Great Train Robbery",
    // .. other fields
});

console.log(replaceOne);

console.log(db.movies.findOne({
    title: "The Great Train Robbery"
}));
```

返回的replaceOne的值是

```js
let replaceOne = {
    "acknowledged": true,
    "insertedId": null,
    "matchedCount": new NumberLong("1"),
    "modifiedCount": new NumberLong("0"),
    "upsertedCount": new NumberInt("0")
}
```

## 删除

```
https://www.mongodb.com/zh-cn/docs/manual/reference/operator/aggregation-pipeline/
```



删除一条文档

```js
db.<collection>.deleteOne( <QueryFilter> )
```

删除多条符合条件的文档

```js
db.<collection>.deleteMany( <QueryFilter> )
```

删除所有文档

```js
db.<collection>.deleteMany({ })
```

