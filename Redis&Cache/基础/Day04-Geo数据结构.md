# Geo数据结构

-   key
-   latitude 纬度
-   longitude 经度
-   member 值

底层是SortedKey, latitude 纬度和longitude 经度经过运算转化为score

![image-20240131110937834](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Redis%26Cache/基础/Day04-Geo数据结构/image-20240131110937834.png)

## 单位

-   m ：米，默认单位。
-   km ：千米。
-   mi ：英里。
-   ft ：英尺。

## 命令

### 写

#### GeoAdd

-   添加一个地理空间信息

```bash
redis(pc2):1>geoAdd geoKey 0 0 (0,0)号点
"1"
redis(pc2):1>geoAdd geoKey 0 1 (0,1)号点 1 0 (1,0)号点
"2"
redis(pc2):1>geoAdd geoKey 1 1 (1,1)号点
"1"
```

### 查

#### GeoDist

-   计算两点之间的距离并返回

```bash
redis(pc2):1>geoDist geoKey (0,0)号点 (0,1)号点
"111226.0989"
redis(pc2):1>geoDist geoKey (0,0)号点 (0,1)号点 km
"111.2261"
```

#### GeoHash

-   将指定Member的坐标转化为hash字符串形式返回

```bash
redis(pc2):1>geoHash geoKey (0,0)号点
1) "s0000000000"

```

#### GeoPos

-   返回指定member的坐标

```bash
redis(pc2):1>geoPos geoKey (0,0)号点
1) 1) "0.00000268220901489"
   2) "0.00000126736058093"
```

### 范围查

#### ~~GeoRadius~~

-   指定圆心半径, 返回其内的所有member
-   6.2之后废弃

![image-20240131112225581](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Redis%26Cache/基础/Day04-Geo数据结构/image-20240131112225581.png)

```bash
redis(pc2):1>geoRadius geoKey 0.5 0.5 100 km WithCoord WithDist ASC 
1) 1) "(0,1)号点"
   2) "78.6451"
   3) 1) "0.00000268220901489"
      2) "0.99999945914297683"

2) 1) "(1,1)号点"
   2) "78.6453"
   3) 1) "0.99999994039535522"
      2) "0.99999945914297683"

3) 1) "(0,0)号点"
   2) "78.6481"
   3) 1) "0.00000268220901489"
      2) "0.00000126736058093"

4) 1) "(1,0)号点"
   2) "78.6483"
   3) 1) "0.99999994039535522"
      2) "0.00000126736058093"

```

-   `WithCoord` 带上坐标
-   `WithDIst` 带上距离
-   也可以`StoreDist key`
-   默认`ASC`

#### GeoSearch

-   在指定范围内搜索member
-   按照与指定点之间的距离排序后返回
-   范围可以是圆或矩形
-   6.2新功能

![image-20240131112455136](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Redis%26Cache/基础/Day04-Geo数据结构/image-20240131112455136.png)

```bash
redis(pc2):1>geoSearch geoKey FromMember (0,0)号点 ByRadius 120 km Desc WithCoord WithDist WithHash
1) 1) "(0,1)号点"
   2) "111.2261"
   3) "3377785620988224"
   4) 1) "0.00000268220901489"
      2) "0.99999945914297683"

2) 1) "(1,0)号点"
   2) "111.2260"
   3) "3377736806566050"
   4) 1) "0.99999994039535522"
      2) "0.00000126736058093"

3) 1) "(0,0)号点"
   2) "0.0000"
   3) "3377699720527872"
   4) 1) "0.00000268220901489"
      2) "0.00000126736058093"

```

-   `FromLonLat`根据经纬度

#### GeoSearchStore

-   与GeoSearch功能一致
-   可以把结果存储到一个key
-   6.2新功能

## 原理

底层数据结构是ZSet, 重点是设计score

score 使用 **GeoHash** 来计算, 目标是距离上相近的点, GeoHash值也相近

1. 以二维坐标为例, 经度和维度都有范围, [-180, 180] 以及 [-90,90]

2. [-180, 180] 区间进行二分, 坐标的经度如果落在左边则记为0, 如果落在右边的为1
3. 不断二分, 不断得出0/1, 则得出一个二进制序列
4. 同理, 维度也能有一个二进制序列
5. 将二进制序列拼合(直接拼合或交替拼合等)