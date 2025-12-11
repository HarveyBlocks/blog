# 模式验证

MongoDB的文档在没有模式的情况下是及其自由的

对文档字段的(存在等)约束和对文档值(类型, 值, 范围等)的约束

使用模式验证来确保没有意外的模式更改或不当的数据类型出现

默认情况下，当插入或更新操作将导致文档不符合模式的演示时，MongoDB 会组织插入或更新操作



不能为`admin`、`local` 和 `config` 数据库中的集合指定模式验证

## json-schema

### 类型与范围限制

```js
db.createCollection("students", {
   validator: {
      $jsonSchema: { // 描述一整个Document
         bsonType: "object",
         title: "Student Object Validation",
         required: [ "address", "major", "name", "year" ],
         properties: {
            name: { // Document的部分字段
               bsonType: "string",
               description: "'name' must be a string and is required"
            },
            year: {
               bsonType: "int",
               minimum: 2017,
               maximum: 3017,
               description: "'year' must be an integer in [ 2017, 3017 ] and is required"
            },
            gpa: {
               bsonType: [ "double" ],
               description: "'gpa' must be a double if the field exists"
            }
         }
      }
   }
} )
```



可以使用 `title` 和 `description` 字段来解释验证规则。当文档验证失败时，MongoDB 会在错误输出中包含这些字段。

### enum

```js
db.createCollection("shipping", {
   validator: {
      $jsonSchema: {
         bsonType: "object",
         title: "Shipping Country Validation",
         properties: {
            country: {
               enum: [ "France", "United Kingdom", "United States" ],
               description: "Must be either France, United Kingdom, or United States"
            }
         }
      }
   }
} )
```

### null验证

如果字段被限制了类型, 则null无法插入, null必须另外考虑

```js
db.createCollection("store", {
  validator: {
    "$jsonSchema": {
      "properties": {
        "storeLocation": { 
          "bsonType": [ "null", "string" ]  // 单独一个"string"将导致该字段无法设置null
        }
      }
    }
  }
})
```

### additionalProperties字段

 `additionalProperties: ture`(默认), 允许插入模式中`"properties"`描述的属性外的属性

指定 `additionalProperties: false` 时, 将组织插入存在`"properties"`描述的属性外的属性的文档

特别的, 要注意打开 `additionalProperties: false` 时, `"properties"`一定要描述`_id`, 因为`_id`一定存在于文档中

```json
{
  "$jsonSchema": {
    "required": [ "_id", "storeLocation" ],
    "properties": {
      "_id": { "bsonType": "objectId" },
      "storeLocation": { "bsonType": "string" }
    },
    "additionalProperties": false
  }
}
```

## 查询操作符

可以将`$jsonSchema`的位置设置成查询操作符

```js
 db.createCollection("sales", {
   validator: {
     "$and": [
       // Validation with query operators
       {
         "$expr": {
           "$lt": ["$lineItems.discountedPrice", "$lineItems.price"]
         }
       },
       // Validation with JSON Schema
       {
         "$jsonSchema": {
           "properties": {
             "items": { "bsonType": "array" }
           }
          }
        }
      ]
    }
  }
)
```

下面的查询操作符不允许

- `$expr`带有 `$function` 表达式
- `$near`
- `$nearSphere`
- `$text`
- `$where`

