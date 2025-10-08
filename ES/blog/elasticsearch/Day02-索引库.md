# 索引库

[Elasticsearch Guide](https://www.elastic.co/guide/en/elasticsearch/reference/7.12/index.html)

## mapping属性

>   mapping , 对索引库文档的约束

![image-20231225140447331](../typora-user-images/Day02-%E7%B4%A2%E5%BC%95%E5%BA%93/image-20231225140447331.png)

### 常见属性讲解

-   `type`: 字段数据类型
    -   `text` 字符串 **可分词的文本**
    -   `keyword` 字符串 **精确词** , 品牌, 国家, ip地址
    -   `long` `integer` `short` `byte` `double` `float` 数字类型
    -   `boolean`布尔值
    -   `date` 日期
    -   `object` 对象 (嵌套), 字属性也接收搜索
    -   **没有数组类型, 但运行某类型字段有多个值**
-   `index`: 是否创建倒排索引 ,默认是true
    -   创建就能搜索
    -   不创建就不能搜索(例如图片的url, 邮箱)
-   `analyser`: 分词器
    -   只在结合`text`的时候使用
-   `properties`:
    -   该字段的子字段



## 创建索引库

```json
PUT /索引库名称
{
  "mappings": {
    "properties": {
      "字段名":{
        "type": "text",
        "analyzer": "ik_smart"
      },
      "字段名2":{
        "type": "keyword",
        "index": "false"
      },
      "字段名3":{
        "properties": {
          "子字段": {
            "type": "keyword"
          }
        }
      },
      // ...等
    }
  }
}
```





-   例:

    ```json
    PUT /employee
    {
      "mappings": {
        "properties": {
          "name":{
            "type": "object", 
            "properties":{
              "firstName": {
                "type": "keyword"
              },
              "lastName": {
                "type": "keyword"
              }
            }
          },
          "salary":{
            "type": "float",
            "index": "false"
          },
          "cv":{
            "type": "text",
            "analyzer": "ik_smart"
          }
        }
      }
    }
    ```

    -   姓名参与搜素, 不被分词

    - 工资是是实数

    -   cv, 简历,分词, 参与搜素

    创建成功

    ```json
    {
      "acknowledged" : true,
      "shards_acknowledged" : true,
      "index" : "employee"
    }
    ```

    



# 索引库CURD

[**和Rust风格一致**](..\..\..\JDK\JavaDailyBlog\spring-mvc\SpringMVC请求与响应\Day02-Rest风格.md)

![image-20231125204155130](../typora-user-images/Day02-%E7%B4%A2%E5%BC%95%E5%BA%93/image-20231125204155130.png)

## 查看索引库

```json
GET /索引库名
```

```json
GET /employee 
```

返回建表信息

```json
{
  "employee" : {
    "aliases" : { },
    "mappings" : {
      "properties" : {
        "cv" : {
          "type" : "text",
          "analyzer" : "ik_smart"
        },
        "name" : {
          "properties" : {
            "firstName" : {
              "type" : "keyword"
            },
            "lastName" : {
              "type" : "keyword"
            }
          }
        },
        "salary" : {
          "type" : "float",
          "index" : false
        }
      }
    },
    "settings" : {
      "index" : {
        "routing" : {
          "allocation" : {
            "include" : {
              "_tier_preference" : "data_content"
            }
          }
        },
        "number_of_shards" : "1",
        "provided_name" : "employee",
        "creation_date" : "1703485911708",
        "number_of_replicas" : "1",
        "uuid" : "yUq8ySu1SGy2t_ic5uL2CA",
        "version" : {
          "created" : "7120199"
        }
      }
    }
  }
}
```

## 删除索引库

```json
DELETE /索引库名称
```

```json
DELETE /employee
```

返回

```json
{
  "acknowledged" : true
}
```

再查

```json
{
  "error" : {
    "root_cause" : [
      {
        "type" : "index_not_found_exception",
        "reason" : "no such index [employee]",
        "resource.type" : "index_or_alias",
        "resource.id" : "employee",
        "index_uuid" : "_na_",
        "index" : "employee"
      }
    ],
    "type" : "index_not_found_exception",
    "reason" : "no such index [employee]",
    "resource.type" : "index_or_alias",
    "resource.id" : "employee",
    "index_uuid" : "_na_",
    "index" : "employee"
  },
  "status" : 404
}
```

404



## 修改索引库字段

**索引库不允许修改**, 你修改索引库字段, 会使所有索引失效

```json
PUT /employee/_mapping 
{
  "properties": {
    "salary":{
      "type": "double",
      "index": true
    }
  }
}
```

报错

```json
{
  "error" : {
    "root_cause" : [
      {
        "type" : "illegal_argument_exception",
        "reason" : "mapper [salary] cannot be changed from type [float] to [double]"
      }
    ],
    "type" : "illegal_argument_exception",
    "reason" : "mapper [salary] cannot be changed from type [float] to [double]"
  },
  "status" : 400
}
```



顺带一提, 数据库的表字段虽然能修改, 但也是不建议的

## 添加索引库字段

```json
PUT /索引库名/_mapping
{
    "properties": {
        "新字段名":{
            "type": "integer"
        }
    }
}
```

字段名不要和之前的字段重复, 否则认为你在修改字段, 会报错

```json
PUT /employee/_mapping 
{
  "properties": {
    "age":{
      "type": "integer"
    }
  }
}
```

```json
{
  "acknowledged" : true
}
```

