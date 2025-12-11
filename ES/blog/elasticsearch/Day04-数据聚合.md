# 数据聚合

>   aggregations

## 聚合种类

>   聚合的字段一定不能用来分词

-   桶(Bucket)聚合
    -   用来对文档做分组
    -   `TermAggregation`: 按照文档字段值分组
    -   `Date HIstogram`: 按照日期阶梯分组(例如 一周一组 , 一个月一组等)
-   度量( Metric ) 聚合
    -   计算一些值
    -   `Avg`
    -   `max`
    -   `min`
    -   `stats`
-   管道 ( pipeliine ) 聚合
    -   其他聚合结果为基础做聚合

高

## DSL

### Buckert聚合

#### `aggs`字段值分组

```json
GET /索引库名/_search
{
  "size": 0, // 不会返回文档
  "aggs": {
    "自定义聚合名": {
      "查询类型,以term为例": {
        "field": "参与聚合的字段名",
        "size": 返回的聚合结果数量,默认10,
        "order": {
          "_count": "desc" //可选, 按照每个组类的成员个数排序
        }
      }
    }
  }
}
```

```json
GET /hotel/_search
{
  "size": 0,
  "aggs": {
    "brandAgg": {
      "terms": {
        "field": "brand.keyword",
        "size": 4
      }
    }
  }
}
```

返回: 

```json
{
  "took" : 8,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 201,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "brandAgg" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 109,
      "buckets" : [ // 桶
        {
          "key" : "7天酒店",
          "doc_count" : 30
        },
        {
          "key" : "如家",
          "doc_count" : 30
        },
        {
          "key" : "皇冠假日",
          "doc_count" : 17
        },
        {
          "key" : "速8",
          "doc_count" : 15
        }
          //看得出来, 降序排序了
      ]
    }
  }
}
```

-   以doc_count升序排列

```json
GET /hotel/_search
{
  "size": 0,
  "aggs": {
    "brandAgg": {
      "terms": {
        "field": "brand.keyword",
        "size": 20
        , "order": {
          "_count": "asc"
        }
      }
    }
  }
}
```

-   `range`也可以用在聚合类型

```json
GET /hotel/_search
{
  "size": 0,
  "aggs": {
    "brandAgg": {
      "range": {
        "field": "price",
        "ranges": [
          {
            "from": 50,
            "to": 100
          },
          {
            "from": 100, 
            "to": 150
          },
          {
            "from": 150,
            "to": 200
          },
          ...
        ]
      }
    }
  }
}
```

**默认情况下, Bucket聚合会对索引库的所有文档做聚合, 可以限定聚合文档范围, 减少对内存的压力**

```json
GET /hotel/_search
{
  "query": {
    "term": {
      "city": {
        "value": "上海"
      }
    }

  }, 
  "size": 0,
  "aggs": {
    "brandAgg": {
      "terms": {
        "field": "brand.keyword",
        "size": 20,
         "order": {
          "_count": "desc"
        }
      }
    }
  }
}
```

### Metric 聚合

>   获取每个品牌的用户评分的最大, 最小, 平均

```json
GET /索引库名/_search
{
  "size": 0, // 不会返回文档
  "aggs": {
    "自定义聚合名": {
      "查询类型,以term为例": {
        "field": "参与聚合的字段名",
        "size": 返回的聚合结果数量,默认10,
        "order": {
          "需要被排序的聚合结果名": "排序方式"
        }
      },

      "aggs": {
        "聚合结果名": {
          "聚合统计方式名,如avg,max,min,sum,stats(包含前四)": {
            "field": "配统计的字段"
          }
        }
      }
    }
  }
}
```

```json
GET /hotel/_search
{

  "size": 0,
  "aggs": {
    "brandAgg": {
      "terms": {
        "field": "brand.keyword",
        "size": 20,
        "order": {
          "scoreAvg": "desc"
        }
      },

      "aggs": {
        "scoreAvg": {
          "avg": {
            "field": "score"
          }
        }
      }
    }
  }
}
```

```json
GET /hotel/_search
{

  "size": 0,
  "aggs": {
    "brandAgg": {
      "terms": {
        "field": "brand.keyword",
        "size": 20,
        "order": {
          "scoreStatus.avg": "desc"
        }
      },

      "aggs": {
        "scoreStatus": {
          "stats": {
            "field": "score"
          }
        }
      }
    }
  }
}
```

## Rust Client

```java
public void getData() throws IOException {
    // 获取请求
    SearchRequest request = new SearchRequest(INDEX);

    // bucket聚合, 以term精准分组
    String aggName = "city_agg";
    TermsAggregationBuilder cityAgg = AggregationBuilders
            .terms(aggName).field("city").size(5);

    // Metric聚合
    String avgName = "score_avg";
    String sumName = "score_sum";
    AvgAggregationBuilder avgAgg = AggregationBuilders.avg(avgName).field("score");
    SumAggregationBuilder sumAgg = AggregationBuilders.sum(sumName).field("score");

    // 组合聚合
    cityAgg.subAggregation(avgAgg).subAggregation(sumAgg);

    // 组装source
    request.source()
            .query(new MatchAllQueryBuilder())
            .aggregation(cityAgg);

    // 发送请求
    SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);

    // 解析请求.因为和source高度契合, 故不易分开
    Terms terms = response.getAggregations().get(aggName);
    List<? extends Terms.Bucket> buckets = terms.getBuckets();
    buckets.forEach(bucket -> {
        System.out.println(bucket.getKey());
        Aggregations aggregations = bucket.getAggregations();
        ParsedAvg parsedAvg = aggregations.get(avgName);
        System.out.println(parsedAvg.getValue());
        ParsedSum parsedSum = aggregations.get(sumName);
        System.out.println(parsedSum.getValue());
    });
}
```

## 聚合的应用

### 分组结果的应用

>   索引库中存在的文档中, 共涉及了哪些城市 ? 这决定了用户能在前端看见的选项数量

![image-20231228195711226](../assets/Day04-数据聚合/image-20231228195711226.png)

```json
GET /hotel/_search
{
  "size": 0,
  "aggs": {
    "brandAgg": {
      "terms": {
        "field": "city",
        "size": 5
      }
    }
  }
}
```

### 带条件的聚合的应用

>   如果用户选择了选项100-500区间, 品牌栏目就不应该继续存在哪些价格不在此区间范围的选项
>
>   如果用户搜索了虹桥, 因为虹桥在上海, 城市栏目就不应该继续存在北京之类的的选项 

