# 复合元素字段的条件查询

## 嵌入式文档

使用点符号对嵌套字段进行查询, `"field.nestedField"`

```java
Document first = collection.find(Filters.eq("awards.wins", "1")).first();
```

使用各个字段一一匹配

```java
Document first = collection.find(Filters.and(
        Filters.eq("awards.wins", 1),
        Filters.eq("awards.nominations", 0),
        Filters.eq("awards.text", "1 win.")
)).first();
```

对象之间可以进行比较, 但是**不建议**, 因为要求整个Bson完全匹配, 字段顺序不对也会无法匹配

```java
Document first = collection.find(Filters.eq("awards", Document.parse(
    "{ wins: 1, nominations: 0, text: '1 win.' }"
))).first();
```

## 数组

### eq

全等查询, 元素必须全部匹配

```java
Document first = collection.find(Filters.eq(
                "cast",
                List.of("Jane Gail", "Ethel Grandin", "William H. Turner", "Matt Moore")
        ));
```

### all

`all`查询, 是指定的目标数组`List.of(...)`的超集, 包含目标数组的所有元素, 且可以增加一些其他没有提到的元素

```java
FindIterable<Document> iterable = collection.find(Filters.all(
                "countries",
                List.of("USA", "UK", "Germany", "France", "Japan")
        ))
        .projection(Projections.fields(
                Projections.include("title"),
                Projections.include("countries"),
                Projections.excludeId()
        ));
try (MongoCursor<Document> cursor = iterable.cursor()) {
    if (!cursor.hasNext()) {
        System.out.println("not find");
        return;
    }
    while (cursor.hasNext()) {
        Document doc = cursor.next();
        System.out.printf("%-50s\t%s\n",
                doc.get("title", String.class), 
                doc.getList("countries", String.class).toString()
        );
    }
}
```

![image-20251119183651547](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/MongoDB/JavaClient/Day04-Query on Array%26Object/image-20251119183651547.png)

### 比较

当希望查询数组种存在一个指定元素, 就是`Filters.all( "countries",List.of("USA"))`, 可以直接用`Filters.eq("countries","USA")`表示

```java
FindIterable<Document> iterable = collection.find(Filters.eq(
            "countries","USA"
    ));
```

### and

对于其他的比较, `lt`, `gt`等, 都表示"存在某一元素符合则匹配"

```Java
FindIterable<Document> iterable = collection.find(Filters.gt(
                "countries", "We"
        ));
```

多个条件(条件A,B,C...)且, 表示"存在元素满足条件A, 且存在元素满足条件B, 且存在元素满足条件C, 且..."

而不是"存在元素满足(条件A, 且条件B, 且条件C...)"

### elemMatch

表达: "存在元素满足(条件A, 且条件B, 且条件C...)"

```java
FindIterable<Document> iterable = collection.find(Filters.elemMatch(
                "countries",
                new MapDocumentBuilder().gt("Wd").lt("Wf").build()
        ));
```

MapDocumentBuilder是自定义的, 由于elemMatch的API可读新较低

```java
collection.find(elemMatch("dim_cm", Document.parse("{ $gt: 22, $lt: 30 }")));
```

其源码如下:

```java
/**
 * <p>
 * 封装了构造器{@link Document#Document(Map)}, 能更自由更规范地构建Document
 * </p><p>
 * 同时对文档形如<pre>{@code
 *    // language=Json
 *    "{"+
 *      "\"$gt\": 12,"+
 *      "\"$lt\": 15"+
 *    "}"
 *   }</pre>进行了一定增强
 * </p>
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-11-19 19:08
 */
public class MapDocumentBuilder {
    private final Document doc = new Document();

    public enum Key {
        NE("$ne"), EQ("$eq"), GT("$gt"), LT("$lt"), GTE("$gte"), LTE("$lte"),
        ;
        private final String field;

        Key(String field) {this.field = field;}
    }

    public static Bson empty() {
        return new Document();
    }

    public MapDocumentBuilder ne(Object value) {
        return append(Key.NE, value);
    }

    public MapDocumentBuilder eq(Object value) {
        return append(Key.EQ, value);
    }

    public MapDocumentBuilder lt(Object value) {
        return append(Key.LT, value);
    }

    public MapDocumentBuilder gt(Object value) {
        return append(Key.GT, value);
    }

    public MapDocumentBuilder lte(Object value) {
        return append(Key.LTE, value);
    }

    public MapDocumentBuilder gte(Object value) {
        return append(Key.GTE, value);
    }

    public MapDocumentBuilder append(Key key, Object value) {
        doc.append(key.field, value);
        return this;
    }
    public MapDocumentBuilder append(String key, Object value) {
        doc.append(key, value);
        return this;
    }
    public Document build() {
        return doc;
    }
}
```

### 数组索引

使用dot, 数组索引的下标从0开始

```java
FindIterable<Document> iterable = collection.find(Filters.gt(
                "countries.0", "Wd"
        ));
```

封装了一个组装字段的规范类

```java
/**
 * 构造字段形如school.students.1023.grade.math
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-11-19 19:08
 */
public class FieldBuilder {
    private final StringJoiner joiner = new StringJoiner(".");

    public FieldBuilder at(int index) {
        joiner.add(String.valueOf(index));
        return this;
    }

    public FieldBuilder get(String inner) {
        joiner.add(inner);
        return this;
    }

    public String build() {
        return joiner.toString();
    }
}
```

使用方法如下:

```java
FindIterable<Document> iterable = collection.find(Filters.gt(
                new FieldBuilder("countries").get(0).build(), "Wd"
        ));
```

## 嵌入式文档数组

对于下面的文档示例

```json
{
    arr: [
        {
            "f1":"a",
            "f2":"x",
            "f3":1
        },{
            "f1":"b",
            "f2":"y",
            "f3":2
        },{
            "f1":"c",
            "f2":"z",
            "f3":3
        },
    ]
}
```

```java
collection.find(Filter.lte("arr.f3", 2));
```

表示Document的`arr`数组中, 存在元素满足`f3`成员小于等于2的, 就匹配

```java
collection.find(Filter.lte("arr.0.f3", 2));
```

表示Document的`arr`数组中, 索引为0的元素的`f3`成员满足小于等于2的, 就匹配

亦可使用elemMatch, 表示数组中存在元素, 元素满足各个条件

```java
 collection.find(Filters.elemMatch(
                "arr",
                new Document().append("f3", new MapDocumentBuilder().gt(1).lte(3).build())
))
```

```java
collection.find(elemMatch("arr", Document.parse("{ f3: { $gt: 1, $lte: 3 } }")));
```

表示目标文档的"arr"数组存在元素, 其存在`f3`字段, 且`f3`满足`$gt: 1`且` $lte: 3 `的文档, 则该`arr`字段所在文档被匹配

```java
collection.find(elemMatch("arr", new Document().append("f3",1).append("f2","x")));
```

表示目标文档的"arr"数组存在元素, 该元素存在字段`f3`等于1且存在字段`f2`等于`"x"`, 可以有其他字段, 则该`arr`字段所在文档被匹配

```java
collection.find(and(eq("arr.f3", 1), eq("arr.f1", "x")));
```

表示目标文档的"arr"数组存在元素, 其f3字段值是1, 且目标文档的"arr"数组存在元素, 其f1字段值是"x", 满足条件的两个元素不一定是同一个元素

## 子元素的投影

对于数组元素或嵌入式文档的元素, 进行特别的投影

- 使用Dot来返回某个嵌入式文档的特定子元素

- 返回包含一个嵌入式文档, 但是这个嵌入式文档中的某个子元素不返回

- 对于文档中的数组元素都是嵌入式文档的清空, 返回数组中所有元素的特定子字段

  ```java
  Projections.include("arr.f1")// arr.map(ele->ele.get("f1")).toArr()
  ```

- 对数组切片

  ```java
  Projections.slice("arr",5); // field limit
  Projections.slice("arr",10,5); // field skip limit
  ```

- 

