# BSON

Binary Json, 二进制序列化格式

## 数据类型

| 类型标记 | 别名         | 注意       |
| :------- | :----------- | :--------- |
| 1        | "double"     |            |
| 2        | "string"     |            |
| 3        | "object"     |            |
| 4        | "array"      |            |
| 5        | "binData"    | 二进制数据 |
| 6        | "undefined"  | 已弃用。   |
| 7        | "objectId"   |            |
| 8        | "bool"       |            |
| 9        | "date"       | 日期       |
| 10       | "null"       |            |
| 11       | "regex"      | 正则表达式 |
| 13       | "javascript" |            |
| 14       | "symbol"     | 已弃用。   |
| 16       | "int"        | 32 位整数  |
| 17       | "timestamp"  |            |
| 18       | "long"       | 64 位整型  |
| 19       | "decimal"    |            |
| -1       | "minKey"     |            |
| 127      | "maxKey"     |            |

类型标记(占1byte)在二进制序列的数值之前, 用于标注类型

## 二进制数据binData

字节数组

其有诸多子类型

| 数值 | 说明                                                         |
| :--- | :----------------------------------------------------------- |
| 0    | 通用二进制子类型                                             |
| 1    | 函数数据                                                     |
| 2    | 二进制（旧版）                                               |
| 3    | UUID（旧）                                                   |
| 4    | UUID                                                         |
| 5    | MD5                                                          |
| 6    | 加密的 BSON 值                                               |
| 7    | 压缩时间序列数据*5.2 版本中的新增功能*。                     |
| 8    | 敏感数据，例如密钥或密码。 MongoDB 不会记录子类型为 8 的二进制数据的字面值。相反，MongoDB 会记录占位符值`###` 。 |
| 9    | 向量数据是由相同类型的数字组成的密集数组。                   |
| 128  | 自定义数据                                                   |

## ObjectId

对象标识, 唯一(大概), 生成速度快, 有序

- 4字节时间戳
- 5字节随机值, 由机器和进程共同决定生成, 进程重新启动时会重新生成
- 3字节递增计数器, 初始化为随机值, 进程重新启动时, 计数器会重置

ObjectId使用大端存储, 其他BSON类型使用小端存储

如果插入的文档不指定 `_id`字段，则驱动程序**自动**为 `_id`字段生成

执行 `upsert: true` 的更新操作插入的文档也会**自动生成** `_id` 字段(如果不指定)

- ObjectID **大致按创建时间排序，但并非完全有序**
- 一秒的时间分辨率，同一秒的ObjectID不保证顺序
- 可能具有不同系统时钟的客户端生成

## string

`UTF-8`编码

对于string的排序, 低沉使用Cpp的`strcmp`函数

## Timestamp

64bit值, 和Date无关

- 高 32 位是 `time_t` 值（自 UNIX 纪元以来的**秒**数）
- 低 32 位是递增的 `ordinal`, 用于区分秒内的操作

~~其实我觉得可以给time_t多分一点, 一秒产生16bit的值, 也就是十万级的数据就已经非常难得了~~

虽然Timestamp是小端存储的, 但是比较时先比较`time_t`, 再比较`ordinal`

## Date

64bit有符号整型

正值表示 UNIX 纪元以来的million seconds, 负值表示UNIX纪元之前的时间

创建日期对象, 获取当前时间

```js
var my_date1 = new Date()
var my_date2 = ISODate()
```

获取月份

```js
var date1_month = my_date1.getMonth()
```

## `decimal128`

范围在 `10^6144` 和 `10^-6143`

```bash
var d1 = Decimal128("9823.1297")
var d2 = Decimal128.fromStringWithRounding("9823.1297")
```

JS 不原生支持BigDecimal, 需要导入依赖

