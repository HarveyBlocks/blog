# 概述

MongoDB是一个文档数据库, NoSQL

## 概念

使用集合（Collections）来组织文档（Documents），每个文档都是由**键值对**组成的

- **数据库（Database）**：存储数据的容器，类似于关系型数据库中的数据库。
- **集合（Collection）**：数据库中的一个集合，类似于关系型数据库中的表。
- **文档（Document）**：集合中的一个数据记录，类似于关系型数据库中的记录，以 **BSON** 格式存储。
- **BSON(Binary JSON)**: 二进制形式的 JSON
- **事务（Transactions）**：从 MongoDB 4.0 开始支持，允许一组操作作为一个原子单元执行
- **TTL（Time-To-Live）**：可以为集合中的某些字段设置 TTL，以自动删除旧数据

![A MongoDB document.](../../assetss/Day01-Overview/crud-annotated-document.svg)

| MongoDB 术语/概念 | 解释/说明                                  |
| :---------------- | :----------------------------------------- |
| database          | 数据库                                     |
| collection        | 集合                                       |
| document          | 文档                                       |
| field             | 数据字段                                   |
| index             | 索引                                       |
| primary key       | 主键, MongoDB**自动将`_id`字段设置为主键** |





## 特点

- **索引优化查询**：MongoDB 允许用户为文档中的任意属性创建索引
- **数据镜像与扩展性**：通过本地或网络创建数据的副本，实现的数据冗余和扩展能力
- **水平扩展与分片**：通过分片技术将数据分布到计算机网络中的其他节点上，实现水平扩展
- **JSON 格式的查询语法**，支持复杂的查询表达式，包括对内嵌对象和数组的查询。
- **MapReduce 批量处理**：MongoDB 的 MapReduce 功能专为大规模数据处理和聚合操作设计
- **MapReduce 脚本编写**：Map 和 Reduce 函数使用 JavaScript 编写
- **GridFS 大文件存储**：用于存储和检索大于 BSON 文档大小限制的文件
- **服务端脚本执行**：MongoDB 允许在服务端执行 JavaScript 脚本

