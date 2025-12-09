# Databasse&Collection

## 操作

### 访问数据库

```java
MongoDatabase database = mongoClient.getDatabase("testDatabase");
```

### 删除数据库

```java
database.drop();
```

**删除数据库会删除数据库中的所有数据**

删除数据库会永久删除该数据库中的所有集合、文档和索引。

仅当不再需要数据库中的数据时才删除数据库。

### 访问集合

```java
MongoCollection<Document> collection = database.getCollection("testCollection");
```

如果提供的集合名称在数据库中尚不存在，则当首次将数据插入该集合时，MongoDB 会隐式创建该集合。



### 创建集合

```java
database.createCollection("exampleCollection");
```

创建集合时添加选项(模式验证)

```java
ValidationOptions opts = new ValidationOptions().validator(Filters.or(
        Filters.exists("commander"),
        Filters.exists("first officer")
)); 
// 选项意为设置写入约束: 
//	 	集合中所有文档都必须满足ValidationOptions的条件
// 		$or {$exist:"commander",$exist:"first officer" }
database.createCollection("ships", new CreateCollectionOptions().validationOptions(opts));
```

### 集合列表

```java
for (String name : database.listCollectionNames()) {
    System.out.println(name);
}
```



### 删除集合

```java
collection.drop();
```



## 文档验证

> ValidationOptions

写入Collection时根据筛选器验证Document, 在创建Collection时指定

```java
ValidationOptions collOptions = new ValidationOptions().validator(
        Filters.or(Filters.exists("commander"), Filters.exists("first officer")));
database.createCollection("ships",
        new CreateCollectionOptions().validationOptions(collOptions));
```



## Capped Collections[文档-实践, 三者严重冲突](TODO)

一旦一个集合填满了分配的空间，它就会通过覆盖集合中最旧的文档来为新文档腾出空间。

固定大小集合最常见的使用场景是存储日志信息。当固定大小集合达到其最大大小时，旧的日志条目将自动被新的条目覆盖。

无法在事务中写入固定大小集合

`$out` 聚合管道阶段无法将结果写入固定大小集合。

### 创建

设置集合大小上限

```java
// 集合大小上限是 10000
// size 会舍入到最进的 256 的整数倍, 257->256, 255->256, 1->256, 10000->9984
cappedDb.createCollection("log", new CreateCollectionOptions().capped(true).sizeInBytes(10000));
```

设置集合的文档上限

```java
// 最多5000个文档
cappedDb.createCollection("log", new CreateCollectionOptions().capped(true).sizeInBytes(5242880).maxDocuments(5000));
```



### 更新

避免更新固定大小集合中的数据。

由于固定大小集合的大小是固定的，因此更新可能会导致数据超出集合的分配空间。

### 冲突

```js
db.log.drop();
db.createCollection("log", {
  capped: true, size: 2000,
});
db.log.insertOne({
  _id: "001", data: "0123456789",
});
db.log.updateOne({
  _id: "001",
}, {
  $set: {
   data: "0123456789".repeat(300),
  },
});
db.log.find();
let stats = db.runCommand({
  collStats: "log",
});
console.log(stats.capped); // true
console.log(stats['size'] > stats.maxSize); // true 非常奇怪
```