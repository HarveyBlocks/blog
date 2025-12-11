# ES

>   Elasticsearch

强大的开源搜索引擎

-   功能

    -   搜索网页
    -   搜索文本关键字
    -   搜索其他实体

-   组件( elastic stack (ELK))

    用于日志数据分析, 实时监控

    -   elasticsearch(核心)
        -   存储
        -   计算
        -   搜索数据
    -   kibana
        -   数据可视化
    -   Logstash
        -   数据抓取(日志)
    -   Beats
        -    数据抓取

-   支持分布式, 可水平拓展, 支持高并发

-   提供Restful接口, 可被任何语言调用

## Lucene技术

>elasticsearch 的底层技术实现

-   Java语言的搜索类库
-   Apache公司的顶级项目
-   官网[Apache Lucene](https://lucene.apache.org/)

### 优势

-   易拓展
-   高性能( 基于倒排索引 )

### 缺点

-   限于Java语言开发
-   学习路线陡峭(API 复杂的很)
-   不支持水平拓展
    -   不支持高并发和集群拓展s

为了克服这些缺点, 必须进行二次开发, elesticsearch就是对Lucene技术的二次开发

## 倒排索引

-   与MySQL的正向索引对比所得

### 正向索引的不足

MySQL的根据ID形成B+树,然后根据搜索,很快

但是如果要使用对TITTLE的搜索, 就算创建索引, 使用模糊匹配 , 破坏索引, 只能逐条扫描

### 倒排索引原理

>   面向文档存储

#### 创建倒排索引原理

1.  创建一张表(**new_table**)
    -   包含字段**词条(term)**, **文档(document)id**
    -   每条数据都是一个文档(每一个商品, 每一个订单, 每一个网页)
    -   文档按语义分成的词语就是词条(小米 手机 充电 电器 充电器)
2.  在一张表(**table**)的title字段的大量字段, 按**词条分词**
3.  扫描分词后的tittle, 将**新出现的词条(还未在new_table的词条字段中存在)存入new_table的词条字段中**
4.  如果是**已出现的词条**, 就在**new_table**的这条**已出现的词条**对应的**文档ID**中将**table**的扫描到的**tittle**对应的ID存入
5.   由于**词条绝对不会重复(唯一性)**, 就可以依据词条创建索引

![image-20231223130101074](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223130101074.png)

#### 查询原理

1.  对用户的搜索内容进行**分词**( **分词n个词** )
2.  将分词所成的词条去查询获得**n条文档ID**
3.  根据**n条文档ID**去存储完整信息的表**根据id查询**记录
    -   可以根据文档ID出现的次数, 将文档ID拍个序
4.  放入结果集

#### 存储数据原理

>   基于JSON存储

无论是原信息还是词条信息, 最终都会化成Json格式存储

#### MySQL与Elasticsearch的概念对比

>   相同类型文档的集合

![image-20231223131206136](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223131206136.png)

对索引的**映射**

![image-20231223131222393](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223131222393.png)

![image-20231223131312770](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223131312770.png)

-   Schema叫约束, 外键约束, 主键约束

### MySQL和ES各自的使用场景

-   MySQL
    -   擅长事务类型操作
    -   确保数据安全和一致性
    -   例如下单
-   Elasticsearch
    -   擅长海量数据搜索, 复杂查询 , 分析, 计算

 ![image-20231223131945612](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223131945612.png)

那么, 他俩都应该存有数据喽:

将数据卸载MySQL

使用数据同步技术,同步到ES

![image-20231223132055645](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223132055645.png)

(根据ID查询这种, 用MySQL也没毛病的)

# 部署ES

## 用Docker创建网络

创建网络, 让ES和Kibana容器互联

```bash
docker network create es-net
```

## 加载镜像

-   下载es镜像

    ```bash
    docker pull elasticsearch:8.6.0
    ```

-   下载kibana镜像

    ```bash
    docker pull docker.elastic.co/kibana/kibana:8.6.0
    ```

-   加载.tar包(如果有用到的话)

    ```bash
    docker load -i es.tar
    ```

### 运行

-   运行dik,部署单点es

    ```bash
    docker run -d \
    	--name es \
        -e "ES_JAVA_OPTS=-Xms1024m -Xmx1024m" \
        -e "discovery.type=single-node" \
        -v es-data:/usr/share/elasticsearch/data \
        -v es-plugins:/usr/share/elasticsearch/plugins \
        --privileged \
        --network es-net \
        -p 9200:9200 \
        -p 9300:9300 \
    elasticsearch:7.12.1
    ```

    es对内存的消耗比较大的

    ```bash
    docker run -d \
    --name kibana \
    -e ELASTICSEARCH_HOSTS=http://es:9200 \
    --network=es-net \
    -p 5601:5601  \
    kibana:7.12.1
    ```

    ```bash
    mkdir -p /mydata/elasticsearch/config
    mkdir -p /mydata/elasticsearch/data
    echo "http.host: 0.0.0.0" > /mydata/elasticsearch/config/elasticsearch.yml
    ```

### 访问

- 访问es

    ```bash
    curl localhost:9200
    ```

    或在浏览器的地址栏输入

    ```url
    http://10.192.128.23:9200/
    ```

- 访问kibana

    ![image-20231223223909221](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223223909221.png)

    访问kibana

    ```bash
    curl http://0.0.0.0:5601
    ```

    或用浏览器

    ![image-20231223232256251](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223232256251.png)

### 老大的Complie

```yml
elasticsearch:
        image: elasticsearch:8.4.2
        container_name: fzuhelper-elasticsearch
        environment:
            bootstrap.memory_lock: "true"
            ES_JAVA_OPTS: "-Xms512m -Xmx512m"
            discovery.type: single-node
            ingest.geoip.downloader.enabled: "false"
            TZ: Asia/Shanghai
            xpack.security.enabled: "false"
        healthcheck:
            test: ["CMD-SHELL", "curl -sf http://elasticsearch:9200/_cluster/health || exit 1"] # ⼼跳检测，成功之后不再执⾏后⾯的退出
            interval: 60s # ⼼跳检测间隔周期
            timeout: 10s
            retries: 3
            start_period: 60s # ⾸次检测延迟时间
        ulimits:
            memlock:
                soft: -1
                hard: -1
        volumes:
            - ../config/elasticsearch:/usr/share/elasticsearch/config
            - ./data/elasticsearch:/usr/share/elasticsearch/data
        ports:
            - "9200:9200"
        restart: always
        networks:
            - fzuhelper

    kibana:
        image: kibana:8.4.2
        container_name: fzuhelper-kibana
        environment:
            - I18N_LOCALE=zh-CN
            - XPACK_GRAPH_ENABLED=true
            - TIMELION_ENABLED=true
            - XPACK_MONITORING_COLLECTION_ENABLED="true"
            - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
        depends_on:
            - elasticsearch
        ports:
            - "5601:5601"
        volumes:
            - ../config/kibana:/usr/share/kibana/config
            - ./data/kibana:/usr/share/kibana/data
        networks:
            - fzuhelper
```

### 简单使用

![image-20231223232746611](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223232746611.png)

-   选择自己玩儿

![image-20231223233031103](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223233031103.png)

-   方便的DSL控制台

![image-20231223232956422](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223232956422.png)

想想, 我们说到, kibana之本质是向`es:9200`发送请求

那么

```json
Get /
```

不就是发送请求:访问`es:9200`吗? 可以得到和访问`http://10.192.128.23:9200/`一样的效果

![image-20231223233550917](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231223233550917.png)

## 安装ik分词器

-   帮助中文分词

安装插件需要知道elasticsearch的plugins目录位置，而我们用了数据卷挂载，因此需要查看elasticsearch的数据卷目录，通过下面命令查看:

```sh
docker volume inspect es-plugins
```

![image-20231224195020438](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231224195020438.png)

[ik分词器, 注意版本对应](https://github.com/medcl/elasticsearch-analysis-ik/tags)

![image-20231224195124805](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231224195124805.png)

-   重启es容器

    ```bash
    docker restart es
    ```

### 简单使用IK分词器

-   英文标准分词

    ```json
    POST /_analyze
    {
      "text": "I'm the bone of my sword",
      "analyzer": "standard"
    }
    ```

    结果

    ```json
    {
      "tokens" : [
        {
          "token" : "i'm",
          "start_offset" : 0,
          "end_offset" : 3,
          "type" : "<ALPHANUM>",
          "position" : 0
        },
        {
          "token" : "the",
          "start_offset" : 4,
          "end_offset" : 7,
          "type" : "<ALPHANUM>",
          "position" : 1
        },
        {
          "token" : "bone",
          "start_offset" : 8,
          "end_offset" : 12,
          "type" : "<ALPHANUM>",
          "position" : 2
        },
        {
          "token" : "of",
          "start_offset" : 13,
          "end_offset" : 15,
          "type" : "<ALPHANUM>",
          "position" : 3
        },
        {
          "token" : "my",
          "start_offset" : 16,
          "end_offset" : 18,
          "type" : "<ALPHANUM>",
          "position" : 4
        },
        {
          "token" : "sword",
          "start_offset" : 19,
          "end_offset" : 24,
          "type" : "<ALPHANUM>",
          "position" : 5
        }
      ]
    }

    ```

-   标准中文分词

    ```json
    POST /_analyze
    {
      "text": "I 滴答滴答 you, you 哗啦哗啦 me",
      "analyzer": "standard"
    }
    ```

    结果

    ```json
    {
      "tokens" : [
        {
          "token" : "i",
          "start_offset" : 0,
          "end_offset" : 1,
          "type" : "<ALPHANUM>",
          "position" : 0
        },
        {
          "token" : "滴",
          "start_offset" : 2,
          "end_offset" : 3,
          "type" : "<IDEOGRAPHIC>",
          "position" : 1
        },
        {
          "token" : "答",
          "start_offset" : 3,
          "end_offset" : 4,
          "type" : "<IDEOGRAPHIC>",
          "position" : 2
        },
        {
          "token" : "滴",
          "start_offset" : 4,
          "end_offset" : 5,
          "type" : "<IDEOGRAPHIC>",
          "position" : 3
        },
        {
          "token" : "答",
          "start_offset" : 5,
          "end_offset" : 6,
          "type" : "<IDEOGRAPHIC>",
          "position" : 4
        },
        {
          "token" : "you",
          "start_offset" : 7,
          "end_offset" : 10,
          "type" : "<ALPHANUM>",
          "position" : 5
        },
        {
          "token" : "you",
          "start_offset" : 12,
          "end_offset" : 15,
          "type" : "<ALPHANUM>",
          "position" : 6
        },
        {
          "token" : "哗",
          "start_offset" : 16,
          "end_offset" : 17,
          "type" : "<IDEOGRAPHIC>",
          "position" : 7
        },
        {
          "token" : "啦",
          "start_offset" : 17,
          "end_offset" : 18,
          "type" : "<IDEOGRAPHIC>",
          "position" : 8
        },
        {
          "token" : "哗",
          "start_offset" : 18,
          "end_offset" : 19,
          "type" : "<IDEOGRAPHIC>",
          "position" : 9
        },
        {
          "token" : "啦",
          "start_offset" : 19,
          "end_offset" : 20,
          "type" : "<IDEOGRAPHIC>",
          "position" : 10
        },
        {
          "token" : "me",
          "start_offset" : 21,
          "end_offset" : 23,
          "type" : "<ALPHANUM>",
          "position" : 11
        }
      ]
    }
    ```

    虽然英文分的很好啦....但这有鸟用啊????

-   ik_smart中文分词

    ```json
    POST /_analyze
    {
      "text": "I 滴答滴答 you, you 哗啦哗啦 me",
      "analyzer": "ik_smart"
    }
    ```

    结果

    ```json
    {
      "tokens" : [
        {
          "token" : "i",
          "start_offset" : 0,
          "end_offset" : 1,
          "type" : "ENGLISH",
          "position" : 0
        },
        {
          "token" : "滴答",
          "start_offset" : 2,
          "end_offset" : 4,
          "type" : "CN_WORD",
          "position" : 1
        },
        {
          "token" : "滴答",
          "start_offset" : 4,
          "end_offset" : 6,
          "type" : "CN_WORD",
          "position" : 2
        },
        {
          "token" : "you",
          "start_offset" : 7,
          "end_offset" : 10,
          "type" : "ENGLISH",
          "position" : 3
        },
        {
          "token" : "you",
          "start_offset" : 12,
          "end_offset" : 15,
          "type" : "ENGLISH",
          "position" : 4
        },
        {
          "token" : "哗啦哗啦",
          "start_offset" : 16,
          "end_offset" : 20,
          "type" : "CN_WORD",
          "position" : 5
        },
        {
          "token" : "me",
          "start_offset" : 21,
          "end_offset" : 23,
          "type" : "ENGLISH",
          "position" : 6
        }
      ]
    }
    ```

    nice

### `ik_smart`和`ik_max_word`

-   `ik_smart`:最少切分
-   `ik_max_word`:最细切分

例子:

```json
POST /_analyze
{
  "text": "他是研究生物化学的",
  "analyzer": "ik_????"
}
```

`ik_smart`:

```json
{
  "tokens" : [
    {
      "token" : "他",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "研究",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 2
    },
    {
      "token" : "生物化学",
      "start_offset" : 4,
      "end_offset" : 8,
      "type" : "CN_WORD",
      "position" : 3
    },
    {
      "token" : "的",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "CN_CHAR",
      "position" : 4
    }
  ]
}
```

他/是研究/生物/化学/的

`ik_max_word`:

```json
{
  "tokens" : [
    {
      "token" : "他",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "研究生",
      "start_offset" : 2,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 2
    },
    {
      "token" : "研究",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 3
    },
    {
      "token" : "生物化学",
      "start_offset" : 4,
      "end_offset" : 8,
      "type" : "CN_WORD",
      "position" : 4
    },
    {
      "token" : "生物",
      "start_offset" : 4,
      "end_offset" : 6,
      "type" : "CN_WORD",
      "position" : 5
    },
    {
      "token" : "物化",
      "start_offset" : 5,
      "end_offset" : 7,
      "type" : "CN_WORD",
      "position" : 6
    },
    {
      "token" : "化学",
      "start_offset" : 6,
      "end_offset" : 8,
      "type" : "CN_WORD",
      "position" : 7
    },
    {
      "token" : "的",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "CN_CHAR",
      "position" : 8
    }
  ]
}
```

```txt
他/是/研究生
    /研究/生物化学
		/生物/化学
		  /物化/
				 /的
```

#### 各自的用处

-   `ik_smart`
    -   占内存少
    -   无法搜索到合适的内容
-   `ik_max_word`
    -   占内存多
    -   比较精准的查询

### IK分词器词典的拓展和停用

要修改词典, 需要配置ik分词器目录中的config目录下的`IKAnalyzer.cfg.xml`文件指定`拓展字典`和`停止词字典`

![image-20231225134005482](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231225134005482.png)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
	<comment>IK Analyzer 扩展配置</comment>
	<!--用户可以在这里配置自己的扩展字典 -->
	<entry key="ext_dict">ext.dic</entry>
	 <!--用户可以在这里配置自己的扩展停止词字典-->
	<entry key="ext_stopwords">stopword.dic</entry>
	<!--用户可以在这里配置远程扩展字典 -->
	<!-- <entry key="remote_ext_dict">words_location</entry> -->
	<!--用户可以在这里配置远程扩展停止词字典-->
	<!-- <entry key="remote_ext_stopwords">words_location</entry> -->
</properties>
```

-   拓展

    -   网络新词, 品牌等专有名词  

    -   准备ext.dic(同级目录下)

        ```txt
        菜鸟
        菜就多练
        奶奶的
        妈妈生的
        ```

        不太好(羞涩)

-   停用

    -   敏感词, 语气词, 助词, 虚词, 代词

    -   stopword.dic已存在(同级目录下)

        ![image-20231225134428721](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/ES/elasticsearch/Day01-ES介绍和安装/image-20231225134428721.png)

        ```txt

        ```

-   测试

    ```json
    POST /_analyze
    {
      "text": "啊福州大专的垃圾呀",
      "analyzer": "ik_max_word"
    }
    ```

    结果

    ```json
    {
      "tokens" : [
        {
          "token" : "福州大专",
          "start_offset" : 1,
          "end_offset" : 5,
          "type" : "CN_WORD",
          "position" : 0
        },
        {
          "token" : "福州",
          "start_offset" : 1,
          "end_offset" : 3,
          "type" : "CN_WORD",
          "position" : 1
        },
        {
          "token" : "大专",
          "start_offset" : 3,
          "end_offset" : 5,
          "type" : "CN_WORD",
          "position" : 2
        },
        {
          "token" : "垃圾",
          "start_offset" : 6,
          "end_offset" : 8,
          "type" : "CN_WORD",
          "position" : 3
        }
      ]
    }
    ```

