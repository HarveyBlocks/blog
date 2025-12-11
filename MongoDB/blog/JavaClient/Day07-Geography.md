# 地理空间

使用在(GeoJson对象|旧坐标)对上创建地理索引, 讲目标字段的值作为地理位置信息来解析

地理索引有

- `2dsphere`, 在球体上计算集合图形

  ```js
  db.collection.createIndex( { <location field> : "2dsphere" } )
  ```

- `2d` 在欧几里得平面上计算集合图形

  ```js
  db.collection.createIndex( { <location field> : "2d" } )
  ```

Java种创建索引

```java
Indexes.geo2d("geo_field");
Indexes.geo2dsphere("geo_field1","geo_field2");
```

支持单字段索引和复合索引



## 旧坐标对

```json
{
    <field>: [ <x>, <y> ]
}
```

或使用嵌入式文档表示

数组要优于嵌入式文档，因为某些语言不能保证关联映射的顺序

```json
{
    <field>: {<f1>: <x>, <f2>: <y>}
}
```

不原生支持多维坐标

## GeoJSON对象

GeoJson对象必须包含两个字段

-  `type` 字段，指定 GeoJSON 对象类型
   - `Point`
   - `LineString` 两点确定的指向
   - `Polygon` 多边形
-  `coordinates` 字段，指定坐标
   -  先列出**经度**, 取值范围`[-180,180]`
   -  后列出**纬度**, 取值范围`[-90,90]`

```json
{ type: "Point", coordinates: [ 40, 5 ] }
{ type: "LineString", coordinates: [ [ 40, 5 ], [ 41, 6 ] ] }
```

### 带单环的多边形

- 第一个和最后一个坐标必须匹配，才能将多边形闭合
- 环不能自相交

```json
{
  type: "Polygon",
  coordinates: [ 
    [[0, 0], [3, 6], [6, 1], [0, 0]] 
  ]
}
```

### 具有多个环的多边形

- 第一个描述的环必须为外环。
- 外环不能自相交。
- 任何内环必须完全包含在外环内。
- 几个内环之间不能相互交叉或重叠。内环不能共用边缘。

```json
{
  type : "Polygon",
  coordinates : [
     [[0, 0], [3, 6], [6, 1], [0, 0]],
     [[2, 2], [3, 3], [4, 2], [2, 2]]
  ]
}
```



![Diagram of a Polygon with internal ring.](../../assetss/Day07-Geography/index-2dsphere-polygon-with-ring.bakedsvg.svg)







### `MultiPoint`

需要`2dsphere`索引

```json
{
  type: "MultiPoint",
  coordinates: [
     [ -73.9580, 40.8003 ],
     [ -73.9498, 40.7968 ],
     [ -73.9737, 40.7648 ],
     [ -73.9814, 40.7681 ]
  ]
}
```



### `MultiLineString`

需要`2dsphere`索引

```json
{
  type: "MultiLineString",
  coordinates: [
     [ [ -73.96943, 40.78519 ], [ -73.96082, 40.78095 ] ],
     [ [ -73.96415, 40.79229 ], [ -73.95544, 40.78854 ] ],
     [ [ -73.97162, 40.78205 ], [ -73.96374, 40.77715 ] ],
     [ [ -73.97880, 40.77247 ], [ -73.97036, 40.76811 ] ]
  ]
}
```



### `MultiPolygon`

需要`2dsphere`索引

```json
{
  type: "MultiPolygon",
  coordinates: [
     [ [ [ -73.958, 40.8003 ], [ -73.9498, 40.7968 ], [ -73.9737, 40.7648 ], [ -73.9814, 40.7681 ], [ -73.958, 40.8003 ] ] ],
     [ [ [ -73.958, 40.8003 ], [ -73.9498, 40.7968 ], [ -73.9737, 40.7648 ], [ -73.958, 40.8003 ] ] ]
  ]
}
```



### `GeometryCollection`

需要`2dsphere`索引, 这样应该不需要在每一个成员上建立索引, 应该是建立一个Multikey索引了吧?

 GeometryCollection 是 GeoJSON 的一种类型

```json
{
  type: "GeometryCollection",
  geometries: [
     {
       type: "MultiPoint",
       coordinates: [
          [ -73.9580, 40.8003 ],
          [ -73.9498, 40.7968 ],
          [ -73.9737, 40.7648 ],
          [ -73.9814, 40.7681 ]
       ]
     },
     {
       type: "MultiLineString",
       coordinates: [
          [ [ -73.96943, 40.78519 ], [ -73.96082, 40.78095 ] ],
          [ [ -73.96415, 40.79229 ], [ -73.95544, 40.78854 ] ],
          [ [ -73.97162, 40.78205 ], [ -73.96374, 40.77715 ] ],
          [ [ -73.97880, 40.77247 ], [ -73.97036, 40.76811 ] ]
       ]
     }
  ]
}
```



## 查询操作

| 名称             | 说明                                                         |
| ---------------- | ------------------------------------------------------------ |
| `$geoIntersects` | `2dsphere` 索引支持, 与 GeoJSON 相交的几何图形               |
| `$geoWithin`     | `2dsphere` 和 `2d` 索引支持, 在一个有边界 GeoJSON 内选择几何图形。 |
| `$near`          | `2dsphere` 和 `2d` 索引支持, 靠近给定点的文档                |
| `$nearSphere`    | `2dsphere` 和 `2d` 索引支持, 返回与球面上的某个点相邻的地理空间对象。 |



## 聚合操作

`$geoNear`

按距离指定点最近到最远的顺序输出文档。

```js
db.places.aggregate([
   {
     $geoNear: {
        near: { type: "Point", coordinates: [ -73.99279 , 40.719296 ] },
        distanceField: "dist.calculated",
        maxDistance: 2, // 可选
        query: { category: "Parks" }, // 可选
        includeLocs: "dist.location", // 可选, 
         // 用来计算距离的位置(此处是`distanceField`)被复制到字段`"dist.location"`
        spherical: true // true for 球形坐标, false for 由在此字段上建立的索引决定
     }
   }
])
```

## 地理索引

### 2dsphere 索引

- 确定指定区域内的点。
- 计算到指定点的距离。
- 返回坐标查询的精确匹配结果。

```javascript
db.<collection>.createIndex( { <location field> : "2dsphere" } )
```

索引的目标坐标表示第一个值（经度）必须介于 - 180和180 （含）之间。 第二个值（纬度）必须介于90和90 （含）之间。

### 2d

 2d indexes值不会“环绕”球体。

无法使用 2D 索引查询 GeoJSON 对象。要对 GeoJSON 对象进行查询，使用 2dsphere 索引。

```javascript
db.<collection>.createIndex( { <location field> : "2d" } )
```

索引的目标坐标表示第一个值（经度）必须介于 - 180和180 （含）之间。 第二个值（纬度）必须介于90和90 （含）之间。

可以使用2 d 索引上的min和max选项覆盖这些默认限制。 

