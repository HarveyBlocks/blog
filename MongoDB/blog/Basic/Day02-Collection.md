# Collection

即文档的集合

Collection存在于Database中

集合没有固定的结构，可以对集合插入不同格式和类型的数据，但通常情况下插入集合的数据都会有一定的关联性。

## 概述

**当第一个文档插入时，集合就会被创建**

### 集合名

合法的集合名

- 非空字符串
- 不能含有`\0`字符
- 不能以`system.`开头，这是为系统集合保留的前缀。
- 一般不要包含`$`等保留字符

```bash
db.col.findOne() # TODO TOBE DELELTE
```

### Identifier

系统会为集合分配一个不可变的UUID

在副本集合的所有节点和分片集群的分片中保持不变

## Capped Collections

- 固定大小的collection
  - 创建时需要指定大小(单位字节)
- 队列过期(过期按照插入的顺序)
  - 顺序存储
  - 自动的维护对象的插入顺序
  - 更新其中的文档时, 更新后的文档不可以超过之前文档的大小(以保证后续文档的位置不变)

创建

```javascript
db.createCollection("mycoll", {capped:true, size:100000})
```

- 能添加新的文档到末尾
- 不能删除文档
- 可以使用 drop() 方法删除 collection 所有的文档(包括collection本身)
- 能进行更新，但文档不能增加存储空间。否则，更新就会失败 。

