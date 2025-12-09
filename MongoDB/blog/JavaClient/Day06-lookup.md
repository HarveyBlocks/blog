# lookup

实现一个链接的操作

```json
{
  $lookup: {
    from: "otherCollection",
    localField: "x",
    foreignField: "y",
    as: "resultingArray"
  }
}
```

等价于

```json
{
  $lookup: {
    from: "otherCollection",
    let: { localX: "$x" }, // 定义变量
    pipeline: [
      {
        $match: {
          // 使用$expr, 能使用变量
          // $expr 中, 必须使用 $eq:[...]数组, 这样设计似乎是为了更加函数化
          $expr: { $eq: [ "$y", "$$localX" ] } // 使用变量匹配
        }
      }
    ],
    as: "resultingArray"
  }
}
```



## 一对一链接

数据准备

orders

```json
[
  {
    "customer_id": "elise_smith@myemail.com",
    "order_date": {"$date": "2020-05-30T08:35:52Z"},
    "product_id": "a1b2c3d4",
    "value": 431.43
  },
  {
    "customer_id": "tj@wheresmyemail.com",
    "order_date": {"$date": "2019-05-28T19:13:32Z"},
    "product_id": "z9y8x7w6",
    "value": 5.01
  },
  {
    "customer_id": "oranieri@warmmail.com",
    "order_date": {"$date": "2020-01-01T08:25:37Z"},
    "product_id": "ff11gg22hh33",
    "value": 63.13
  },
  {
    "customer_id": "jjones@tepidmail.com",
    "order_date": {"$date": "2020-12-26T08:55:46Z"},
    "product_id": "a1b2c3d4",
    "value": 429.65
  },
  {
    "customer_id": "jjones@tepidmail.com",
    "order_date": {"$date": "2020-12-26T08:55:46Z"},
    "product_id": "sjkaldjklas", // 没有对应的product
    "value": 429.65
  }
]
```

products

```json
[
  {
    "id": "a1b2c3d4",
    "name": "Asus Laptop",
    "category": "ELECTRONICS",
    "description": "Good value laptop for students"
  },
  {
    "id": "z9y8x7w6",
    "name": "The Day Of The Triffids",
    "category": "BOOKS",
    "description": "Classic post-apocalyptic novel"
  },
  {
    "id": "ff11gg22hh33",
    "name": "Morphy Richardds Food Mixer",
    "category": "KITCHENWARE",
    "description": "Luxury mixer turning good cakes into great"
  },
  {
    "id": "pqr678st",
    "name": "Karcher Hose Set",
    "category": "GARDEN",
    "description": "Hose + nozzles + winder for tidy storage"
  }
]
```

以order为基进行链接聚合

```java
List<Bson> pipeline = new AggregatePipelineBuilder()
        .append(Aggregates.match(Filters.and(
                Filters.gte("order_date", LocalDateTime.parse("2020-01-01T00:00:00")),
                Filters.lt("order_date", LocalDateTime.parse("2021-01-01T00:00:00"))
        )))
        // 进行链接, 此后product_mapping一定是数组(空数组/单元素数组等)
        // 是左外链接, order的product_id如果没有对应的product, 则为空数组
        .append(Aggregates.lookup(
                /*from*/"products",
                /*local field*/"product_id",
                /*foreign field*/"id",
                /*as*/"product_mapping"
        ))
    	// 截取product_mapping第一个=>product_mapping
        .append(Aggregates.set(new Field<>(
                "product_mapping", 
            	ExpressionDocument.of().first(FieldPath.refer("product_mapping"))
        )))
        .append(Aggregates.set(
                new Field<>("product_name", FieldPath.refer("product_mapping", "name")),
                new Field<>("product_category", FieldPath.refer("product_mapping", "category"))
        ))
        .append(Aggregates.unset("_id", "product_id", "product_mapping"))
        .build();
AggregateIterable<Document> aggregate = orders.aggregate(pipeline);
```





## 多字段链接

当两个集合的文档中有多个对应字段时，就会发生多字段联接。

聚合在相应字段上匹配这些文档，并将两者的信息合并到一个文档中。

数据准备

orders

```json
[
  {
    "customer_id": "elise_smith@myemail.com",
    "order_date": {"$date": "2020-05-30T08:35:52Z"},
    "product_name": "Asus Laptop",
    "product_variation": "Standard Display",
    "value": 431.43
  },
  {
    "customer_id": "tj@wheresmyemail.com",
    "order_date": {"$date": "2019-05-28T19:13:32Z"},
    "product_name": "The Day Of The Triffids",
    "product_variation": "2nd Edition",
    "value": 5.01
  },
  {
    "customer_id": "oranieri@warmmail.com",
    "order_date": {"$date": "2020-01-01T08:25:37Z"},
    "product_name": "Morphy Richards Food Mixer",
    "product_variation": "Deluxe",
    "value": 63.13
  },
  {
    "customer_id": "jjones@tepidmail.com",
    "order_date": {"$date": "2020-12-26T08:55:46Z"},
    "product_name": "Asus Laptop",
    "product_variation": "Standard Display",
    "value": 429.65
  }
]
```

products

```json
[
  {
    "name": "Asus Laptop",
    "variation": "Ultra HD",
    "category": "ELECTRONICS",
    "description": "Great for watching movies"
  },
  {
    "name": "Asus Laptop",
    "variation": "Standard Display",
    "category": "ELECTRONICS",
    "description": "Good value laptop for students"
  },
  {
    "name": "The Day Of The Triffids",
    "variation": "1st Edition",
    "category": "BOOKS",
    "description": "Classic post-apocalyptic novel"
  },
  {
    "name": "The Day Of The Triffids",
    "variation": "2nd Edition",
    "category": "BOOKS",
    "description": "Classic post-apocalyptic novel"
  },
  {
    "name": "Morphy Richards Food Mixer",
    "variation": "Deluxe",
    "category": "KITCHENWARE",
    "description": "Luxury mixer turning good cakes into great"
  }
]
```

执行聚合

```java
// 基于product
List<Bson> pipeline = new AggregatePipelineBuilder()
        .append(Aggregates.lookup(
                /*from*/ "orders",
                // let, 定义一组变量
                /*let*/ Arrays.asList(
                        new Variable<>("product_name", "$name"),
                        new Variable<>("product_variation", "$variation")
                ),
                /*pipeline, 基于order*/
                new AggregatePipelineBuilder()
                        // expr使其能够使用应用(字段路径表达式)
                        .append(Aggregates.match(Filters.expr(Filters.and(
                                /*$$product_name, 引用自定义变量product_name*/
                                // 在expr里使用 $eq:[v1,v2] 数组的形式似乎是为了更加函数式
                                // v1: {$eq: v2} 就不太函数式了
                                // 那Filters.eq()也无法使用了
                                new Document(
                                    "$eq", 
                                    Arrays.asList("$product_name", "$$product_name")
                                ),
                                new Document(
                                    "$eq", 
                                    Arrays.asList("$product_variation", "$$product_variation")
                                )
                        ))))
                        .append(Aggregates.match(Filters.and(
                                Filters.gte(
                                    "order_date",
                                    LocalDateTime.parse("2020-01-01T00:00:00")
                                ),
                                Filters.lt(
                                    "order_date", 
                                    LocalDateTime.parse("2021-01-01T00:00:00")
                                )
                        )))
                        .append(Aggregates.unset("_id", "product_name", "product_variation"))
                        .build(),
                /*as*/ "orders"
        ))
        .append(Aggregates.match(Filters.ne("orders", Collections.emptyList())))
        .append(Aggregates.unset("_id", "description"))
        .build();
DemoShowResult.showPipeline(pipeline);
AggregateIterable<Document> aggregate = products.aggregate(pipeline);
```

inner pipeline必须用变量, 因为两个pipline的执行环境是独立的

Pipline Json:

```json
[
  {
    "$lookup": {
      // 与另一个集合进行链接
      "from": "orders",
      // 设置变量
      "let": {
        "product_name": "$name",
        "product_variation": "$variation"
      },
      "pipeline": [
        {
          // 与外部的值链接, 过滤
          "$match": {
            "$expr": {
              "$and": [
                {"$eq": ["$product_name","$$product_name"]},
                {"$eq": ["$product_variation","$$product_variation"]}
              ]
            }
          }
        },
        // 内部的值过滤
        {
          "$match": {
            "$and": [
              {"order_date": {"$gte": {"$date": "2020-01-01T00:00:00Z"}}},
              {"order_date": { "$lt": {"$date": "2021-01-01T00:00:00Z"}}}
            ]
          }
        },
        // 不映射一些值
        {"$unset": ["_id", "product_name", "product_variation" ]}
      ],
      "as": "orders"
    }
  },
  // 过滤订单为空的文档
  {
    "$match": {
      "orders": {"$ne": []}
    }
  },
  // 不映射一些值
  { "$unset": [ "_id", "description" ] }
]
```

执行结果

```json
[
  {
    "name": "Asus Laptop",
    "variation": "Standard Display",
    "category": "ELECTRONICS",
    "orders": [
      {
        "customer_id": "elise_smith@myemail.com",
        "order_date": {"$date": "2020-05-30T08:35:52Z"},
        "value": 431.43
      },
      {
        "customer_id": "jjones@tepidmail.com",
        "order_date": {"$date": "2020-12-26T08:55:46Z"},
        "value": 429.65
      }
    ]
  },
  {
    "name": "Morphy Richards Food Mixer",
    "variation": "Deluxe",
    "category": "KITCHENWARE",
    "orders": [
      {
        "customer_id": "oranieri@warmmail.com",
        "order_date": {"$date": "2020-01-01T08:25:37Z"},
        "value": 63.13
      }
    ]
  }
]
```

