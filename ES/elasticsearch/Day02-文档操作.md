# 文档CRUD

![image-20231125204155130](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day02-文档操作/image-20231125204155130.png)

## 新增文档(插入)

```json
POST /索引库名/_doc/文档id
{
    "字段1": "值1",
    "字段2": "值2",
    "字段3": {
        "子属性1": "子属性值1",
        "子属性2": "子属性值2"
    }
    // and so on...
}
```

不写文档id, es会给你随机生成一个....这不好吧? 所以不要忘记写文档id

```json
# 插入一个新的文档
POST /employee/_doc/0 
{
    "name": {
      "firstName": "灿辉",
      "lastName": "王"
    },
    "salary": "114514",
    "cv": "福州职业技术学院金牌讲师,参与管理信息系统字典制导生成工具（ MISDDG） 的设计、 编程和测试，负责产品化、 推广和改版工作， 该项目获福建省科技进步三等奖，在福建省得到一定的推广。负责（ 福大项目） 福建省教育厅下达的“ 福建省计算机等级考试系统” 项目（ 编号： JB99052） 的开发（ 项目于2001年底完成）。参与福建省财政厅工(业) 交(通) 财税信息管理系统、 福建南平造纸厂(全厂) 管理信息系统(共16个子系统)、 CAI产品软件（ 高中版）、 电话银行查询系统、福建消防总队项目、福建广东武警总队项目、福州公安局人事管理系统、福建农资集团MIS系统、兴业银行客户服务中心系统、福建省博物馆网络集成工程和信息管理系统等的建设。 "
}
```

返回

```json
{
  "_index" : "employee",
  "_type" : "_doc",
  "_id" : "0",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```

## 查询文档

### 查询所有

```json
GET /索引库名/_search
```

```json
# 获取文档信息,根据id查
GET /employee/_search
```

-   其实是分页查询, 默认查询10条

    ```json
    GET /索引库名/_search
    {
      "query": {
        "match_all": {}
      },
      "size": 数量
    }
    ```

    数量多出来没事, 少了就查不全, 不写就默认10条

### 依据文档id查询

```json
GET /索引库名/_doc/文档id
```

```json
# 获取文档信息,根据id查
GET /employee/_doc/0
```

返回

```json
{
  "_index" : "employee",
  "_type" : "_doc",
  "_id" : "0",
  "_version" : 1,
  "_seq_no" : 0,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "name" : {
      "firstName" : "灿辉",
      "lastName" : "王"
    },
    "salary" : "114514",
    "cv" : "福州职业技术学院金牌讲师,参与管理信息系统字典制导生成工具（ MISDDG） 的设计、 编程和测试，负责产品化、 推广和改版工作， 该项目获福建省科技进步三等奖，在福建省得到一定的推广。负责（ 福大项目） 福建省教育厅下达的“ 福建省计算机等级考试系统” 项目（ 编号： JB99052） 的开发（ 项目于2001年底完成）。参与福建省财政厅工(业) 交(通) 财税信息管理系统、 福建南平造纸厂(全厂) 管理信息系统(共16个子系统)、 CAI产品软件（ 高中版）、 电话银行查询系统、福建消防总队项目、福建广东武警总队项目、福州公安局人事管理系统、福建农资集团MIS系统、兴业银行客户服务中心系统、福建省博物馆网络集成工程和信息管理系统等的建设。 "
  }
}
```

## 删除文档

```json
DELETE /索引库名/_doc/文档id
```

```json
# 删除文档
DELETE /employee/_doc/0
```

返回

```json
{
  "_index" : "employee",
  "_type" : "_doc",
  "_id" : "0",
  "_version" : 2,
  "result" : "deleted",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 1,
  "_primary_term" : 1
}
```

再找

```json
GET /employee/_doc/0
```

返回

```json
{
  "_index" : "employee",
  "_type" : "_doc",
  "_id" : "0",
  "found" : false
}
```

再建

再返回

```json
{
  "_index" : "employee",
  "_type" : "_doc",
  "_id" : "0",
  "_version" : 3,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 4,
  "_primary_term" : 1
}
```

你看, 这version变成3了, 再看看删除时的返回信息

创建(1)->删除(2)->创建(3)

## 修改文档

### 全量修改

>   根据id找文档, 完全删除旧文档, 创建新文档

```json
PUT /索引库名/_doc/文档id
{
    "字段1": "值1",
    "字段2": "值2",
    "字段3": {
        "子属性1": "子属性值1",
        "子属性2": "子属性值2"
    }
    // and so on...
}
```

-   **若id所指文档不存在, 无法删除, 但不影响新增, 所以全量修改可以理解为既可以修改, 也可以新增**

实例:

```json
PUT /employee/_doc/2
{
    "name": {
      "firstName": "红举",
      "lastName": "程"
    },
    "salary": "1919810",
    "cv": "福州职业技术学院金牌讲师,主要从事无线传感器网络的网络协议设计及性能优化分析、高级应用软件开发等研究工作，作为项目负责人主持了一项国家自然基金面上项目、一项教育部科技技术司重点项目、一项福建省自然基金面上项目、二项福建省教育厅科技项目、二项福州大学科技发展基金和一项福州大学人才基金，排名第三参与了一项福建省自然科学基金重点项目，同时主持多项包括国家重点工程相关的横向课题，在Information Sciences、Ad Hoc Networks、Journal of Communications、Sensors、Multimedia Tools and Applications、Journal of Supercomputing、软件学报等国内外权威刊物和国际会议上共发表学术论文30余篇。同时主持多项包括国家重点工程相关的软件开发课题，科研经费充足。 "
}
```

返回

```json
{
  "_index" : "employee",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 6,
  "_primary_term" : 1
}

```

你看他直接返回了created , 都不演了

### 增量修改

>   修改指定字段值

```json
POST /索引库名/_update/文档id
{
    "doc":{
        "字段名": "新的值"
    }
}
```

注意`_update`和`_doc`不一样

测试(不再演示只有修改一个字段的了)

```json
POST /employee/_update/1
{
  "doc": {
    "name": {
      "firstname": "红 举"
    },
    "salary": 123
  }
}
```

返回

```json
{
  "_index" : "employee",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 3,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 7,
  "_primary_term" : 1
}
```

-   `"total" : 2,`

    `"successful" : 1,`

查不存在的, 报错, 404

```json
{
  "error" : {
    "root_cause" : [
      {
        "type" : "document_missing_exception",
        "reason" : "[_doc][10]: document missing",
        "index_uuid" : "kmDxJ1cfRlG5ZFET_WkqiQ",
        "shard" : "0",
        "index" : "employee"
      }
    ],
    "type" : "document_missing_exception",
    "reason" : "[_doc][10]: document missing",
    "index_uuid" : "kmDxJ1cfRlG5ZFET_WkqiQ",
    "shard" : "0",
    "index" : "employee"
  },
  "status" : 404
}
```

-   `"reason" : "[_doc][10]: document missing",`

