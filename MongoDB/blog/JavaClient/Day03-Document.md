# Document

## 构造

构造Document对象

```java
new Document()
    .append("_id", new ObjectId())
    .append("title", "Ski Bloopers")
    .append("genres", Arrays.asList("Documentary", "Comedy"))
```
对于json是array的情况

```java
List<Document> documents = Document.parse("{\"docs\": " + json + "}").getList("docs", Document.class);
```

## Insert

`InsertOne`

```java
int intValue = 2;
String stringValue = "value";
Document doc1 = new Document().append("field1", stringValue).append("field2", intValue);
Document doc2 = new Document("filed1", stringValue).append("field2", intValue);
Document doc3 = new Document(Map.of("filed1", stringValue, "field2", intValue));
InsertOneResult result = collection.insertOne(doc1);
System.out.println("Inserted a document with the following id: " +
                   Optional.ofNullable(result.getInsertedId()).map(id -> id.asObjectId().getValue()));
```

`InsertMany`

批量插入不一定全部成功(例如id已经存在), 但是MongoDB支持**返回已经成功的部分**文档

```java
List<Document> documents = Document.parse("{\"docs\": " + arrayJson + "}").getList("docs", Document.class);
Collection<BsonValue> insertedValues;
try {
    InsertManyResult result = collection.insertMany(documents);
    insertedValues = result.getInsertedIds().values(); // 全部成功
} catch (MongoBulkWriteException exception) {
    insertedValues = exception.getWriteResult() // 部分成功
            .getInserts()
            .stream()
            .map(BulkWriteInsert::getId)
            .collect(Collectors.toList());
}
System.out.println("成功的一部分文档的id: " +
                   insertedValues.stream().map(doc -> doc.asInt32().getValue()).collect(Collectors.toList()));
```

## Update

添加测试文档

```java
MongoDatabase database = mongoClient.getDatabase("sample_crud");
MongoCollection<Document> collection = database.getCollection("inventory");
collection.insertMany(List.of(
    	Document.parse("{item:'canvas',qty:100,size:{h:28,w:35.5,uom:'cm'},status:'A'}"),
        Document.parse("{item:'journal',qty:25,size:{h:14,w:21,uom:'cm'},status:'A'}"),
        Document.parse("{item:'mat',qty:85,size:{h:27.9,w:35.5,uom:'cm'},status:'A'}"),
        Document.parse("{item:'mousepad',qty:25,size:{h:19,w:22.85,uom:'cm'},status:'P'}"),
        Document.parse("{item:'notebook',qty:50,size:{h:8.5,w:11,uom:'in'},status:'P'}"),
        Document.parse("{item:'paper',qty:100,size:{h:8.5,w:11,uom:'in'},status:'D'}"),
        Document.parse("{item:'planner',qty:75,size:{h:22.85,w:30,uom:'cm'},status:'D'}"),
        Document.parse("{item:'postcard',qty:45,size:{h:10,w:15.25,uom:'cm'},status:'A'}"),
        Document.parse("{item:'sketchbook',qty:80,size:{h:14,w:21,uom:'cm'},status:'A'}"),
        Document.parse("{item:'sketchpad',qty:95,size:{h:22.85,w:30.5,uom:'cm'},status:'A'}")
));
```

updateOne

```java
collection.updateOne(
        Filters.eq("item", "paper"), // 如果Filter匹配了多条, 则更新第一条
        Updates.combine(
                Updates.set("size.uom", "cm"),
                Updates.set("status", "P"),
                Updates.currentDate("lastModified") // 如果lastModified字段不存在, 则插入
            // 也就是一个Upsert的逻辑
        )
);
```

updateMany

```java
collection.updateMany(Filters.lt("qty", 50), Updates.combine(
        Updates.set("size.uom", "in"),
        Updates.set("status", "P"),
        Updates.currentDate("lastModified") // 依旧是走一个Upsert
));
```

replaceOne, 替换`_id`外所有字段

```java
private void execute(MongoCollection<Document> collection) {
    collection.replaceOne(
            Filters.eq("item", "paper"),
            Document.parse("{item:'paper',instock: [{warehouse:'A',qty:60},{warehouse:'B',qty:40}]}")
    );
    show(collection.find().limit(20));
}
```

Upsert设置为true, 如果**没有匹配的文档**, 则进行插入操作

```java
collection.updateOne(
        Filters.eq("_id", "not existed _id"),
        Updates.combine(Updates.set("f1", "v1"), Updates.set("f2", "v2")),
        new UpdateOptions().upsert(true)
);
```

Updates成员

- `set` Upsert字段
- `unset` 删除字段
- `rename` 重命名字段

Update无法更新 `_id` 字段的值

Update更新字段后, 字段在文档中的位置不变

`rename` 操作可能会导致文档中的字段**重新排序**

## 删除

- deleteMany
- deleteOne

删除所有文档, 则使用空文档作为Filter参数

```java
collection.deleteMany(MapDocumentBuilder.empty());
```

即使从集合中删除所有文档，删除操作也不会删除索引。

## 批量写

### 有序写和无序写

MongoDB将批量写分为**有序**和**无序**

对于无序的批量写, MongoDB将进行并行优化, 即使出现异常也会继续进行

对于有序的批量写,  出现异常也不会继续进行

要指定无序操作，请在调用首选命令或方法时将 `ordered` 选项设立为 `false`

```java
collection.bulkWrite(List.of(), new BulkWriteOptions().ordered(false));
```

### 示例

```java
BulkWriteResult bulkWriteResult = collection.bulkWrite(List.of(
        new InsertOneModel<>(new Document("_id", 3)
                .append("type", "beef")
                .append("size", "medium")
                .append("price", 6)),
        new InsertOneModel<>(new Document("_id", 4)
                .append("type", "sausage")
                .append("size", "large")
                .append("price", 10)),
        new UpdateOneModel<>(
                Filters.eq("type", "cheese"),
                Updates.set("price", 8)
        ),
        new DeleteOneModel<>(Filters.eq("type", "pepperoni")),
        new ReplaceOneModel<>(
                Filters.eq("type", "vegan"),
                new Document("type", "tofu").append("size", "small").append("price", 4)
        )
), new BulkWriteOptions().ordered(false));
System.out.println(bulkWriteResult);
```

结果输出

```json
{
  "insertedCount": 2,
  "matchedCount": 0,
  "removedCount": 0,
  "modifiedCount": 0,
  "upserts": [],
  "inserts": [
    {
      "index": 0,
      "id": {
        "value": 3
      }
    },
    {
      "index": 1,
      "id": {
        "value": 4
      }
    }
  ]
}
```

## 跨Database的批量写

在MongoDB 8.0以上支持此操作

```java
// Concrete 具体
List<? extends ClientNamespacedWriteModel> clientNamespacedWriteModels = List.of(
        new ConcreteClientNamespacedInsertOneModel(
                new MongoNamespace("db", "authors"),
                new ConcreteClientInsertOneModel(new Document("name", "Stephen King"))
        ),
        new ConcreteClientNamespacedInsertOneModel(
                new MongoNamespace("db", "books"),
                new ConcreteClientInsertOneModel(new Document("name", "It"))
        ),
        new ConcreteClientNamespacedUpdateOneModel(
                new MongoNamespace("db", "books"),
                new ConcreteClientUpdateOneModel(
                        Filters.eq("name", "It"),
                        Updates.set("year", 1986),
                        null, // pipline nullable, 和上面update二选一
                        null // option nullable
                )
        )
);
ClientBulkWriteOptions options = new ConcreteClientBulkWriteOptions().ordered(true)
        .bypassDocumentValidation(true); // 绕过文档验证
ClientBulkWriteResult clientBulkWriteResult = mongoClient.bulkWrite(clientNamespacedWriteModels, options);
System.out.println(clientBulkWriteResult);
```

![image-20251120212007035](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MongoDB/JavaClient/Day03-Document/image-20251120212007035.png)

输出结果

```json
{insertedCount: 2, upsertedCount: 0, matchedCount: 1, modifiedCount: 1, deletedCount: 0}
```

