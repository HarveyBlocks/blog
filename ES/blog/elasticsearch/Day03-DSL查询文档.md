# DSL查询

[Query DSL](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html)

## DSL基本语法

```json
GET /索引库名/_search
{
    "query":{
        "查询类型":{
            "查询条件": "条件值"
        }
    }
}
```





## 查询类型

-   查询所有
    -   查询出所有数据( 存在分页限制 )
    -   一般用于测试
    -   例如: 
        -   `match_all`
-   全文检索(full text)查询
    -   即使用倒排索引的查询
    -   利用分词器
    -     对用户输入内容分词, 对每个分词后的 **词条** 去倒排索引库中匹配
    -   例如:
        -   `match`
        -   `multi_match`
-   精确查询
    -   查找的一般是**keyword** , **数值** , **日期** , **布尔** 等==不需要分词==的字段
    -   为什么不用mysql查呢? 
    -   例如:
        -   `ids`根据id查询
        -   `range`范围查询
        -   `term`依据值精确查询
-   地理(geo)查询
    -   根据经纬度查询
    -   例如:
        -   `geo_distance`
        -   `geo_bounding_box`
-   复合(compound)查询
    -   复合上述各个查询条件, 合并查询条件
    -   例如: 
        -   `bool` 依据逻辑运算组合
        -   `function_source` 依据相关度算分

### `match_all`查询所有

```json
GET /索引库/_search
```

等价于

```json
GET /索引库/_search
{
  "query": {
    "match_all": {}
  }
}
```



```json
{
  "took" : 5,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,// 操作数
    "successful" : 1, // true
    "skipped" : 0, 
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 201,  // 存储的数据的总数量
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [ // 存放数据的数组
      {<-->}, // 不会被全部取出, 会做分词, 默认得十个
      {<-->},
      {<-->},
      {<-->},
      {<-->}
    ]
  }
}

```







### 全文检索查询

>   常用于在搜索框的搜索

![image-20231226130826609](../assets/Untitled/image-20231226130826609.png)



#### `match`查询

```json
GET /索引库名/_search
{
  "query": {
    "match": {
      "字段": "文本"
    }
  }
}
```



```json
GET /hotel/_search
{
  "query": {
    "match": {
      "all": "外滩如家"
    }
  }
}
```

-   查询结果:

```json
1. 外滩+如家
2. 外滩
3. 如家
```

-   很符合需求

#### `multi_match`多字段查询

```json
GET /索引库名/_search
{
  "query": {
    "multi_match": {
      "query": "查询文本",
      "fields": ["字段1","字段2","..."]
    }
  }
}
```



```json
GET /hotel/_search
{
  "query": {
    "multi_match": {
      "query": "外滩如家",
      "fields": ["brand","business","name"]
    }
  }
}
```

-   三个字段中任意有满足的即可
-   查询结果顺序同样符合**《匹配度》**



#### `match{all...}`和 `multi_match` 的 异同

-   效果相同
-   `multi_match`参与查询的字段越多, 效率越低
-   `match{all...}`相当于在建表, 增加数据, 的时候做了足够多的工作, 提高了查询的效率



### 精准查询

>   常用于

![image-20231226130920606](../assets/Untitled/image-20231226130920606.png)







#### `ids`根据id查询

```json
GET /索引库名/_search
{
  "query": {
    "ids": {
      "values": ["id1","id2","..."]
    }
  }
}
```



```json
GET /hotel/_search
{
  "query": {
    "ids": {
      "values": ["434082","60487","415600"]
    }
  }
}
```

-   查询得到的数据不是按照查询时的id顺序排列的





#### `range`范围查询

-   数值可以范围查询
-   字符串按照字典也可以做范围查询(但是没人用)

```json
GET /索引库名/_search
{
  "query": {
    "range": {
      "字段名": {
        "gte": 上限(包含),
        "lte": 下线(包含)
      }
    }
  }
}
```

-   `gte`		大于等于
-   `lte`        小于等于
-   `gt`		  大于不等于
-   `lt`          小于不等于



#### `term`依据值精确查询

```json
GET /索引库名/_search
{
  "query": {
    "term": {
      "字段": {
        "value": "值"
      }
    }
  }
}
```



测试

```json
GET /hotel/_search
{
  "query": {
    "term": {
      "city": {
        "value": "上海"
      }
    }
  }
}
```





查询结果

```json
"total" : {
    "value" : 83,
    "relation" : "eq"
},
```



测试一下不存在的keyword

```json
GET /hotel/_search
{
  "query": {
    "term": {
      "city": {
        "value": "尚海"
      }
    }
  }
}
```

或

```json
GET /hotel/_search
{
  "query": {
    "term": {
      "city": {
        "value": "上海杭州"
      }
    }
  }
}
```



查询结果都是

```json
"total" : {
  "value" : 0,
  "relation" : "eq"
},
```

精准查询不会对搜索的文本做分词啦







### 地理坐标查询

>   查询我最近的酒店
>
>   查询离我附件的顺风车

  



#### `geo_distance`

>到指定中心点小于某个距离值的所有文档(**圆盘**)



```json
GET /hotel/_search
{
  "query": {
    "geo_distance":{
      "distance": "半径",
      "geo_point字段": "中心点经度, 中心点维度" 
    }
  }
}
```

-   关于半径的单位
    -   缺省: `m`
    -   支持 `m`,`km`,`cm`,`dm`,`mm`(没错, 支持`mm`)
    -   不支持 `mile`(说不定mile有其他表示方法, 我不造啊),不支持`nm`



```json
GET /hotel/_search
{
  "query": {
    "geo_distance":{
      "distance": "20km",
      "location": "30.921659, 121.575572"
    }
  }
}
```

#### `geo_bounding_box`

>   查询geo_point值落在某个**矩形范围**内的所有文档
>
>   ~~没在生活中见过~~



~~kibana不会提示呜呜呜~~

```json
GET /索引库名/_search
{
  "query": {
    "geo_bounding_box":{
      "geo_point类型字段":{
        "top_left": {
          "lat": 左上角经度,
          "lon": 左上角维度 
        },
        "bottom_right": {
          "lat": 右下角经度,
          "lon": 右下角维度 
        }
      }
    }
  }
}
```







```json
GET /hotel/_search
{
  "query": {
    "geo_bounding_box":{
      "location":{
        "top_left": {
          "lat": 31.1,
          "lon": 121.5 
        },
        "bottom_right": {
          "lat": 25.1,
          "lon": 130.5 
        }
      }
    }
  }
}
```





### 复合查询



#### `function_score` 依据相关度算分

>   为什么不放到排序模块????
>
>   ~~谁给钱, 谁排前~~



##### 原始相关性算分


$$
TF(词条频率)=\frac{词条出现次数}{文档中词条总数}\\
score(搜索内容总相关度)=\sum{TF(词条)}\\
TF算法
$$





-   `"稀里,哗啦, 哗啦,哗啦"`
    -   总计词条数4
    -   TF(稀里)=0.25
    -   TF(哗啦)=0.75
-   `"稀里,稀里, 稀里,哗啦"`
    -   总计词条数4
    -   TF(稀里)=0.75
    -   TF(哗啦)=0.25
-   当搜索`"哗啦"`时, 第二条文档相关性更高, 排名更靠前
-   **缺点**: 当所有文档中都包含`哗啦`,且`哗啦`数一样时, 计算`哗啦`的TF没有必要



$$
IDF(逆文档频率) = \log{\frac{文档总数}{包含词条的文档总数}}\\
score=\sum{[TF(词条频率) \times IDF(逆文档频率)]}\\
TF-IDF算法
$$



-   **包含(无关一文档中重复数量)**词条的文档总数越多, 这个词条的权重就越小
-   如果所有文档都**包含**这一词条,则`IDF`为0, 此词条不占权重
-   es在5.1开始放弃**TF-IDF算法**, 采用**BM25算法**

$$
score(Q,d) = \sum_i^n{
	[ \log(1+\frac{N-n+0.5}{n+0.1})
	\times 
	\frac{f_i}{f_i+k_i \cdot (1-b+b \cdot \frac{dl}{avgdl} )}]
}\\
BM25算法
$$

-   BM25算法能减少词频对相关性的影响, 在词频增加的情况加区域水平(TF_IDF算法取余无穷)

##### 语法案例

```json
GET /hotel/_search
{
  "query": {
    "function_score": {
      "query": {
        "原始查询类型": {
          ...
        }
      },
      "functions": [
        {
          "filter": {
            "过滤条件的查询类型": {
              ...//符合过滤条件的文档被更改权重
            }
          },
          算分函数: 值
        }
      ],
      "boost_mode": "函数结果的影响模式"
    }
  }
}
```

-   算分函数

    -   ```json
        "weight": 常量
        ```

        直接作为函数结果

    -   ```json
        "field_value_factor": 函数字段
        ```

        函数字段的值做函数结果

    -   ```json
        "random_score": {}
        ```

        随机值作为函数结果

    -   ```json
        "script_score": 自定义公式
        ```

        自定义公式

-   `bost_mood`函数结果的影响模式

    -   `multiply`: query_score*function_score的结果(缺省)
    -   `replase`: 抛弃原始求分, 选择function_score
    -   `sum`, `avg`, `max`, `min ` ....

-   测试

    ```json
    GET /hotel/_search
    {
      "query": {
        "function_score": {
          "query": {
            "match": {
              "all": "如家"
            }
          },
          "functions": [
            {
              "filter": {
                "term": {
                  "business": "虹桥地区"
                }
              },
              "weight": 10
            }
          ],
          "boost_mode": "multiply"
        }
      }
    }
    ```

    ![image-20231226194417748](../assets/Day03-DSL%E6%9F%A5%E8%AF%A2%E6%96%87%E6%A1%A3/image-20231226194417748.png)



#### `bool` 复合条件查询

-   组合方式
    -   `must`必须匹配每个子查询
    -   `should` 选择性匹配任意个子查询
    -   `must_not` 必须不匹配, **不参与算分**
    -   `filter`: 必须匹配, 但**不参与算分**
-   **除了参与算分的子查询外, 其余所有的查询都应该放入`filter`或`must_not`, 减少算分带来的效率损失**

```json
GET /索引库名/_search
{
  "query": {
    "bool": {
      "组合方式": [
        {
          "子查询查询类型": {
          }
        },
        ...// 其他子查询
      ],
      ...// 其他组合方式+子查询
    }
  }
}
```

-   名字包含如家, 价格不高于400, 在坐标31.21, 121.5周围10公里以内的酒店
    -   名字包含如家,
        -   `match`查询
        -   `must`(涉及算分)
    -   价格不高于400
        -   `range`范围查询
        -   `must_not`
    -   坐标31.21, 121.5周围10公里
        -   `geo_distance`距离查询
        -   `filter`(不涉及算分)

```json
GET /hotel/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "all": "如家"
          }
        }
      ],
      "must_not": [
        {
          "range": {
            "price": {
              "gt": 400
            }
          }
        }
      ],
      "filter": [
        {
          "geo_distance": {
            "distance": "10km",
            "location": {
              "lat": 31.21,
              "lon": 121.5
            }
          }
        }
      ]
    }
  }
}
```



## 搜索结果处理

### 排序

-   默认是按照相关度算分排序
-   一旦自己排序, es就会放弃算分(score=null), 速度也会有所上升
-   接收排序的字段类型有: `keyword` , `数值类型`, `地理坐标`, `日期类型`
-   对地理坐标进行排序, 返回值的排序值是距离值

```json
GET /索引库名/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [// 注意要放在query同级
    {
      "字段": {
        "order": "desc"
      }// 支持多字段排序, 从上到下优先级
    }
  ]
}
```

```json
// 数值类型排序
GET /hotel/_search
{
  "query": {
    "match_all": {}
  }
  , "sort": [
    {
      "score": "desc",
      "price": "asc"
    }
  ]
}
```

-   啊? 能这么写你不早说? 

```json
// 按照地理位置的距离排序
GET /hotel/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "_geo_distance": {
        "location": "31.21, 121.5",//维度, 经度
      	"order": "asc"   
      }
    }
  ]
}
```

```json
// 按照地理位置的距离排序的另一种面向对象的写法
GET /hotel/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "_geo_distance": {
        "location": {
          "lat": 31.21,
          "lon": 121.5
        },
      "order": "asc"
      }
    }
  ]
}
```

![image-20231226204701509](../assets/Day03-DSL%E6%9F%A5%E8%AF%A2%E6%96%87%E6%A1%A3/image-20231226204701509.png)

-   还有相差距离的提示

### 分页

-   `from`
-   `size`

```json
GET /索引库名/_search
{
  "query": {
    "match_all": {}
  },
  "from": 1, // 缺省为0
  "size": 20 // 缺省为10
}
```

因为es使用了倒排索引, 所以只能真的全部查出来之后, 再截取一部分返回(悲)

这是**逻辑分页**

#### 分页可能引发的问题

es集群, 把数据拆分, 放到不同的机器上, **每台机器上的数据不一样**

问: ==十个班, 每个班的前十名合在一起, 就是年段的前一百名吗?==

(想到mycat在添加数据时就可以配置指定数据所在的机器, 我真的好感动)

![image-20231226213145557](../assets/Day03-DSL%E6%9F%A5%E8%AF%A2%E6%96%87%E6%A1%A3/image-20231226213145557.png)

-   突发奇想

言归正传

对于在进行分页查询时把集群上的每台机器的数据全部查询, 全部排序, 全部分页------明白有多离谱了吧

如果搜索页数过深(from+size), 或者结果集(size)太大,对内存和CPU的消耗也越高, 因此ES设定结果查询上限是**10000(一万)**

```json
GET /hotel/_search
{
  "query": {
    "match_all": {}
  },
  "from": 9999,
  "size": 2
}
```



![image-20231226214338144](../assets/Day03-DSL%E6%9F%A5%E8%AF%A2%E6%96%87%E6%A1%A3/image-20231226214338144.png)

在实际的业务中, 会限制用户的搜索页数的深度(例如百度最多查70页, 每页大概15条, 顶多一千多条, 再往后查相关度也低, 没必要,很合理), 来避免出现这种错误

但这里依然给出解决方案

#### 分页限制的解决方案

-   `search after`

    分页时需要排序, 从上一次的排序值开始, 查询下一页的数据

    官方推荐

    -   缺点:不能往前翻页(多几个指针指向前几个不行吗?总比全查出来要好很多吧?为什么没有这种qwq)

    ```json
    GET /hotel/_search
    {
        "query": {
            "match_all": {
    
            }
        },
        "sort": [
          {
            "price":  "desc"
          }
        ],
        "search_after": [1899],
        size="15"
    }
    ```

    ![image-20231226220935889](../assets/Day03-DSL%E6%9F%A5%E8%AF%A2%E6%96%87%E6%A1%A3/image-20231226220935889.png)

    他会返回`sort`值来提示你,`sort`里的,就是`search_after`里的

-   `scroll`

    将排序数据形成快照保存在内存中

    官方不推荐

    缺点显而易见



### 高亮

-   虽然但是, 我们明明能在自己的Dao层做,自由度还更高, 不用记这么多配置(虽然不多),写一次就一劳永逸, 也不难, 为什么要让es帮我们做啊 .....哦,它有分词, 我没有....欸嘿

![image-20231226221306731](../assets/Day03-DSL%E6%9F%A5%E8%AF%A2%E6%96%87%E6%A1%A3/image-20231226221306731.png)

![image-20231226221535279](../assets/Day03-DSL%E6%9F%A5%E8%AF%A2%E6%96%87%E6%A1%A3/image-20231226221535279.png)

-   查询结果是动态的, 如果在前端实现了给词条加标签, emmmm ......我不懂前端啊, 我不造啊

    同样的问题, 同样的原因: 前端不会分词





-   `字段`: 告诉ES在哪些地方加标签
-   `pre_targs`, `post_targs`: 告诉es加什么标签,默认\<em\>\</em\>
-   默认情况下, es的搜索字段必须和高亮字段一致, 否则不会高亮, 通过`"require_field_match": "false"`更改

```json
GET /hotel/_search
{
  "query": {
    "match": {
      "all": "北京"
    }
  },
  "highlight": {
    "fields": {
      "city": {
        "pre_tags": "<strong>",
        "post_tags": "</strong>",
        "require_field_match": "false"
      }
    }
  }
}
```



![image-20231226223317625](../assets/Day03-DSL%E6%9F%A5%E8%AF%A2%E6%96%87%E6%A1%A3/image-20231226223317625.png)

-   我似乎对高亮有些误解: 

    -   ```json
        GET /hotel/_search
        {
          "query": {
            "match": {
              "name": "如家"
            }
          },
          "highlight": {
            "fields": {
              "city": {
                "pre_tags": "<strong>",
                "post_tags": "</strong>",
                "require_field_match": "false"
              }
            }
          }
        }
        ```

    -   Q: 为什么city没有被高亮?

        A: 因为没有city叫如家

    -   Q: 那么想要让city高亮呢?

        A: 回到最前面的问题: 让city高亮关分词什么事? 那就写在自个儿的......别说时Dao层了,写在前端 !

