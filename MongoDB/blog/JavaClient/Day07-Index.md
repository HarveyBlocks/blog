# 索引

MongoDB 在创建集合时会在 `_id`字段上创建一个唯一索引。

`_id` 索引可防止客户端插入两个具有相同 `_id` 字段值的文档。

无法删除此索引。

## 查看

```js
db.collection.getIndexes()
```

## 创建

MongoDB 仅在没有相同规格索引存在时才创建索引。

```java
MongoDatabase database = mongoClient.getDatabase("sample_mflix");
MongoCollection<Document> collection = database.getCollection("movies");
// 创建索引, 默认索引名, title_1, 1表示升序, tilte_-1, -1表示降序
String indexName1 = collection.createIndex(Indexes.ascending("title"));
// 指定索引名
collection.createIndex(Indexes.ascending("title"),new IndexOptions().name("index_title"));
// 两个字段
String indexName2 = collection.createIndex(Indexes.ascending("title","runtime"));
```

## 删除

```java
collection.dropIndex("<index_name>");
collection.dropIndex(Document.parse("[\"<index_name1>\",\"<index_name2>\",\"<index_name3>\"]"));
// 直接删除除了_id_外的索引
collection.dropIndexes();
```

## 单字段索引

可对文档中的任意字段创建单字段索引，其中包括：

- 顶级文档字段
- 嵌入式文档
  - 嵌入式文档索引并不会对嵌入式文档中的字段的查询进行优化
- 嵌入式文档中的字段

```json
db.<collection>.createIndex( { <field>: <sort-order> } )
```

```java
String indexName = collection.createIndex(Indexes.ascending("title"));
```

![Diagram of an index on the ``score`` field (ascending).](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MongoDB/JavaClient/Day07-Index/index-ascending.bakedsvg.svg)

## 复合索引

复合索引最多可包含 32 个字段。

```javascript
db.<collection>.createIndex( {
   <field1>: <sortOrder>,
   <field2>: <sortOrder>,
   ...
   <fieldN>: <sortOrder>
} )
```

- `sortOrder` 可选`1` (升序)和`-1`

![Diagram of a compound index on the ``userid`` field (ascending) and the ``score`` field (descending). The index sorts first by the ``userid`` field and then by the ``score`` field.](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MongoDB/JavaClient/Day07-Index/index-compound-key.bakedsvg.svg)

```java
// {"a": 1, "b": 1}
Indexes.ascending("a", "b");
// {"a": -1, "b": -1}
Indexes.descending("a", "b");
// {"a": 1, "b": 1, "c": -1, "d": -1}
Indexes.compoundIndex(Indexes.ascending("a", "b"), Indexes.descending("c", "d"));
// {"a": 1, "b": 1, "c": -1, "d": 1}
Indexes.compoundIndex(
        Indexes.ascending("a", "b"),
        Indexes.compoundIndex(Indexes.descending("c"), Indexes.ascending("d"))
);
```

```java
collection.createIndex(indexBson);
```

索引也支持以相反的顺序优化该查询。

例如索引` { score: -1, username: 1 }`创建, 则排序`{ score: 1, username: -1 }`也会被索引优化

## Multikey

多键索引用于提高对数组字段的查询性能

从包含数组值的字段中收集数据并进行排序

```json
db.<collection>.createIndex( { <arrayField>: <sortOrder> } )
```

![Diagram of a multikey index on the ``addr.zip`` field. The ``addr`` field contains an array of address documents. The address documents contain the ``zip`` field.](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MongoDB/JavaClient/Day07-Index/index-multikey.bakedsvg.svg)

图为使用addr.zip创建Index而自动构建的`Multikey`

将数组中的值拍平到索引上, 索引的多个键都指向同一个文档, 以此进行优化

例如上图的`"10036"`和`"94301"`都指向"xyz"这个文档

### 嵌入文档数组上创建索引

```javascript
db.inventory.insertMany([
  {
   "item": "t-shirt", "stock": [
    {"size": "small", "quantity": 8,}, 
    {"size": "large", "quantity": 10,},
   ],
  }, {
   "item": "sweater", "stock": [
    {"size": "small", "quantity": 4,},
    {"size": "large", "quantity": 7,},
   ],
  }, {
   "item": "vest", "stock": [
    {"size": "small", "quantity": 6,}, 
    {"size": "large", "quantity": 1,},
   ],
  },
]);
```

`stock.quatity`上建立索引, 也会有Multikey的优化, 将`stock.quatity`上的值拍平到索引上

### 复合索引偏离公共路径

数据准备

```json
{
  _id: 1, item: "ABC", ratings: [
   {scores: [{q1: 2, q2: 4}, {q1: 3, q2: 8}], loc: "A"},
   {scores: [{q1: 2, q2: 5}], loc: "B"},
  ],
}, {
  _id: 2, item: "XYZ", ratings: [
   {scores: [{q1: 7}, {q1: 2, q2: 8}], loc: "B"},
  ],
},
```

创建索引`{ "ratings.scores.q1": 1, "ratings.scores.q2": 1 }`

两种查询方式

```javascript
// 不在所需路径上的$elemMatch 
// MongoDB 看到的是在 ratings 数组上进行 $elemMatch, 
db.collection.find( { ratings: { $elemMatch: { 'scores.q1': 2, 'scores.q2': 8 } } } );
// 在公共路径上使用$elemMatch
// MongoDB 看到的是在 ratings.scores 数组上进行 $elemMatch
db.collection.find( { 'ratings.scores': { $elemMatch: { 'q1': 2, 'q2': 8 } } } );
```

不在所需路径上的$elemMatch , 则 **无法复合** 两个索引

要复合边界，必须在公共路径`"ratings.scores"`上使用`$elemMatch` 

不过真要说, 不考虑索引, 这两个查询方式也不同

`$elemMatch`是查询目标数组中存在一个元素**同时满足**`$elemMatch`内所有的条件

第一条是, 查询`ratings`数组内存在一个元素满足`{ 'scores.q1': 2, 'scores.q2': 8 }`, 但这个条件表达的是`scores`数组存在元素满足`'scores.q1': 2` 且存在元素满足`'scores.q2': 8`

第二条是, 查询`'ratings.scores'`数组内存在元素 **同时满足** `$elemMatch`内所有的条件的

因此, 第一个条件查出两个文档, 第二个条件只能查出第二个文档

对于第二种需求要建立索引`{ "ratings.scores.q1": 1, "ratings.scores.q2": 1 }` 才能生效

把`q1`拍平, `q1`相等的`q2`拍到`q1`上

对于第一个条件的`q1`和`q2`, 不一定是同一个元素上的, 例如`_id: 1`这个文档上, 依照索引的建立去查询, 不是依照这个需求做的

## 隐藏

将索引隐藏之后, 其将不对查询进行优化, 也不会再对写操作而造成负担

允许下一次取消隐藏该索引, 而不是重新创建一个被删除的索引

