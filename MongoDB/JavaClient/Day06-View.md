# 视图

MongoDB 视图是一个只读可查询对象

其内容由其他集合或视图上的聚合管道定义

- 标准视图不会在磁盘保存视图内容。视图内容将在客户端查询视图时按需计算得出。

- 按需物化视图, 预先计算的聚合管道结果，存储在磁盘上并从磁盘读取。

  按需物化视图通常是 `$merge` 或 `$out` 阶段的结果

## 索引

标准视图使用根本的集合的索引, 无法直接在标准试图上创建, 删除, 或重新构建一般的索引

MongoDB

可以在**仅包含**以下阶段的兼容视图上创建搜索索引和向量搜索索引

- `$addFields`
- `$set`
- `$match` 封装 `$expr` 操作

因为MongoDB将搜索索引和向量搜索索引存储在磁盘上。

今晚要不要.....

## 创建

```java
MongoDatabase viewDb = mongoClient.getDatabase("view_db");
MongoCollection<Document> students = viewDb.getCollection("students");
students.deleteMany(ExpressionDocument.empty());
students.insertMany(List.of(
        Document.parse("{ sID: 22001, name: \"Alex\", year: 1, score: 4.0 }  "),
        Document.parse("{ sID: 21001, name: \"bernie\", year: 2, score: 3.7 }"),
        Document.parse("{ sID: 20010, name: \"Chris\", year: 3, score: 2.5 } "),
        Document.parse("{ sID: 22021, name: \"Drew\", year: 1, score: 3.2 }  "),
        Document.parse("{ sID: 17301, name: \"harley\", year: 6, score: 3.1 }"),
        Document.parse("{ sID: 21022, name: \"Farmer\", year: 1, score: 2.2 }"),
        Document.parse("{ sID: 20020, name: \"george\", year: 3, score: 2.8 }"),
        Document.parse("{ sID: 18020, name: \"Harley\", year: 5, score: 2.8 }")
));
List<Bson> pipeline = new AggregatePipelineBuilder()
        .append(Aggregates.match(Filters.eq("year", 1)))
        .append(Aggregates.unset("_id"))
        .build();
viewDb.createView("firstYears", "students", pipeline);
MongoCollection<Document> firstYears = viewDb.getCollection("firstYears");
FindIterable<Document> firstYear = firstYears.find();
```

输出

```json
{"sID": 22001, "name": "Alex", "year": 1, "score": 4.0}
{"sID": 22021, "name": "Drew", "year": 1, "score": 3.2}
{"sID": 21022, "name": "Farmer", "year": 1, "score": 2.2}
```

## 按需物化视图

使用 `$merge` 或`$out` 阶段更新保存的数据。

可以直接在按需物化视图上创建索引

准备数据

```java
private static final List<Document> DOCUMENTS = Document.parse(
        "{docs: [ " +
        "{ date: ISODate('2018-12-01'), item: 'Cake - Chocolate', quantity: 2, amount: 60, }, " +
        "{ date: ISODate('2018-12-02'), item: 'Cake - Peanut Butter', quantity: 5, amount: 90,}, " +
        "{ date: ISODate('2018-12-02'), item: 'Cake - Red Velvet', quantity: 10, amount: 200,}, " +
        "{ date: ISODate('2018-12-04'), item: 'Cookies - Chocolate Chip', quantity: 20, amount: 80,}," +
        " { date: ISODate('2018-12-04'), item: 'Cake - Peanut Butter', quantity: 1, amount: 16,}, " +
        "{ date: ISODate('2018-12-05'), item: 'Pie - Key Lime', quantity: 3, amount: 60,}, " +
        "{ date: ISODate('2019-01-25'), item: 'Cake - Chocolate', quantity: 2, amount: 60,}, " +
        "{ date: ISODate('2019-01-25'), item: 'Cake - Peanut Butter', quantity: 1, amount: 16,}, " +
        "{ date: ISODate('2019-01-26'), item: 'Cake - Red Velvet', quantity: 5, amount: 100,}, " +
        "{ date: ISODate('2019-01-26'), item: 'Cookies - Chocolate Chip', quantity: 12, amount: 48,}, " +
        "{ date: ISODate('2019-01-26'), item: 'Cake - Carrot', quantity: 2, amount: 36,}, " +
        "{ date: ISODate('2019-01-26'), item: 'Cake - Red Velvet', quantity: 5, amount: 100,}, " +
        "{ date: ISODate('2019-01-27'), item: 'Pie - Chocolate Cream', quantity: 1, amount: 20,}, " +
        "{ date: ISODate('2019-01-27'), item: 'Cake - Peanut Butter', quantity: 5, amount: 80,}, " +
        "{ date: ISODate('2019-01-27'), item: 'Tarts - Apple', quantity: 3, amount: 12,}, " +
        "{ date: ISODate('2019-01-27'), item: 'Cookies - Chocolate Chip', quantity: 12, amount: 48,}, " +
        "{ date: ISODate('2019-01-27'), item: 'Cake - Carrot', quantity: 5, amount: 36,}, " +
        "{ date: ISODate('2019-01-27'), item: 'Cake - Red Velvet', quantity: 5, amount: 100,}, " +
        "{ date: ISODate('2019-01-28'), item: 'Cookies - Chocolate Chip', quantity: 20, amount: 80,}, " +
        "{ date: ISODate('2019-01-28'), item: 'Pie - Key Lime', quantity: 3, amount: 60,}, " +
        "{ date: ISODate('2019-01-28'), item: 'Cake - Red Velvet', quantity: 5, amount: 100,}" +
        "]}").getList("docs", Document.class);
```

创建按需物化视图并查询

```java
public void createMaterialized(MongoClient mongoClient) {
    MongoDatabase viewDb = mongoClient.getDatabase("view_db");
    MongoCollection<Document> bakeSales = viewDb.getCollection("bake_sales");
    MongoCollection<Document> monthlyBakeSales = viewDb.getCollection("monthly_bake_sales");
    bakeSales.deleteMany(ExpressionDocument.empty());
    bakeSales.insertMany(DOCUMENTS);
    Consumer<Date> updateMonthlyBakeSalesUpdate = startDate -> 
        defineMonthlyBackSalesUpdate(startDate, bakeSales);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date startDate;
    try {
        startDate = simpleDateFormat.parse("1970-01-01");
    } catch (ParseException e) {
        throw new RuntimeException(e);
    }
    updateMonthlyBakeSalesUpdate.accept(startDate);
    MongoCollection<Document> monthlyBakeSales = viewDb.getCollection("monthly_bake_sales");
    DemoShowResult.show(monthlyBakeSales.find());
}

private static void defineMonthlyBackSalesUpdate(Date startDate, MongoCollection<Document> bakeSales) {
    List<Bson> pipeline = new AggregatePipelineBuilder()
            .append(Aggregates.match(Filters.gte("date", startDate)))
            .append(Aggregates.group(
                    new Document(
                            "$dateToString",
                            new Document("format", "%Y-%m").append("date", "$date")
                    ),
                    Arrays.asList(
                            new BsonField("sales_quantity", new Document("$sum", "$quantity")),
                            new BsonField("sales_amount", new Document("$sum", "$amount"))
                    )
            ))
            .append(Aggregates.merge(
                    "monthly_bake_sales",
                    new MergeOptions().whenMatched(MergeOptions.WhenMatched.REPLACE)
            ))
            .build();
    AggregateIterable<Document> aggregate = bakeSales.aggregate(pipeline);
    // 此时只构建完成了聚合管道, 未完成数据库层面的执行, 执行是惰性的
    aggregate.iterator() // 此时真正执行
            .close();
}
```

结果

```json
{"_id": "2018-12", "sales_quantity": 41, "sales_amount": 506}
{"_id": "2019-01", "sales_quantity": 86, "sales_amount": 896}
```

`$merge`阶段会将输出写入 `monthly_bake_sales` 集合。基于ID对文档使用Upsert的逻辑

