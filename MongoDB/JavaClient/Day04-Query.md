# Query

## 查找操作

### find

使用`find`创建一个`MongoIterable`, 用于构建向数据库发送的查询请求

也就是说, find之后的代码的顺序不会影响数据库查询的执行先后顺序

```java
MongoIterable<Document> documentIterable = collection.find(/*空表示find all*/);
```

### iterator

使用`forEach`, `Cursor`, `first`或`iterator`对结果进行处理

`first`是基于`cursor`的, 而`forEach`, `Cursor` 都是基于`iterator`的

`iterator`的执行触发`execute`, 然后构建迭代器, `execute`的任务是向数据库发送请求以及接受数据库的响应

```java
documentIterable.forEach(doc->System.out.println(doc.toJson()));
```

### map

```java
MongoIterable<String> jsonIterable = documentIterable.map(Document::toJson);
MongoIterable<String> titleIterable = documentIterable
    .map(document -> document.get("title", String.class/*此处Document.class是不允许的*/));
```

查看源码得知, map只是将mapper函数式接口对象保存, 然后在`execute()`之后执行.

而`projection`投影操作和`sort`和`limit`等聚合操作, 都是在`execute()`之前执行, 用于组成请求

即使在代码层面, `map`可以写在一些组成请求的操作之前, 但执行是在请求操作完成之后的

而`map`操作又总是在`forEach`, `Cursor`, `first`等操作之前完成

依据数据库的理论, map的操作是可以被延后处理的

## projection

```java
// Projects "title" and "imdb" fields, excludes "_id"
Bson projectionFields = Projections.fields(
        Projections.include("title", "imdb"),
        Projections.excludeId()
);
// Retrieves documents with a runtime of less than 15 minutes, applying the
// projection and a sorting in alphabetical order
FindIterable<Document> docs = collection.find(Filters.lt("runtime", 15))
        .projection(projectionFields);
```

`_id`如果不特别指明排除, 则总是会被返回

在管道的开头或中间使用 `$project` 阶段来减少传递到后续管道阶段的字段数量不太可能提高性能，因为数据库会自动执行此优化。

## 查询过滤

```java
Bson filter = Filters.gt("qty", 7); // 编写过滤器
collection.find(filter).forEach(doc -> System.out.println(doc.toJson()));// 执行查询
```

- 比较
  - eq
  - ne
  - gt
  - lt
  - lte
  - gte

- 逻辑
  - and
  - or
  - 

- 数组

  `size`, 某个字段是数组, 且数组的长度应该等于给定值

  ```java
  Bson filter = Filters.size("vendor", 3); // vendor是一个数组
  ```

- 元素

  `exist`, 存在某个字段的文档

  ```java
  Bson filter = Filters.exists("rated"); // 含有rating字段的文档
  Bson filter = Filters.exists("rated",false); // 不含有rating字段的文档
  ```

- 评估

  `regex`正则表达式

  ```java
  Bson filter = Filters.regex("color", "k$"); // color 字段, 应当符合正则表达式`k$`
  ```

## sort和limit

```java
// Projects "title" and "imdb" fields, excludes "_id"
Bson projectionFields = Projections.fields(
        Projections.include("title", "imdb"),
        Projections.excludeId()
);
// Retrieves documents with a runtime of less than 15 minutes, applying the
// projection and a sorting in alphabetical order
FindIterable<Document> docs = collection.find(Filters.lt("runtime", 15))
        .projection(projectionFields)
        .sort(Sorts.ascending("title"))
        .limit(10);
```

## Null 和 缺失字段

一个字段值为null和一个字段没设置是两个改变

一个字段值为null的前提是这个字段设置了

### Null 相等判断

```java
collection.find(Filter.eq("item", null)); // 是 null
collection.find(Filter.ne("item", null)); // 非 null
```

### 类型检查

```java
collection.find(Filters.type("item", BsonType.NULL));
```

### 存在性检查

`exist`, 存在某个字段的文档

```java
Bson filter = Filters.exists("rated"); // 含有rating字段的文档
Bson filter = Filters.exists("rated",false); // 不含有rating字段的文档
```

## 可重试读

在链接字符串种添加参数`retryReads=false`

发生持续性网络错误或者故障转移周期时自动触发读重试

