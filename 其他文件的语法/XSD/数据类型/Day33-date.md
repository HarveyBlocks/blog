# 日期

日期及时间数据类型用于包含日期和时间的值。

## 日期

>Date Data Type

-   格式 : `"YYYY-MM-DD"`
    -   YYYY 表示年份
    -   MM 表示月份
    -   DD 表示天数
-   所有的成分都是必需的

```xml
<xs:element name="start" type="xs:date"/>
```

```xml
<start>2002-09-24</start>
```

## 时间

>   Time Data Type

-   `"hh:mm:ss"`
    -   hh 表示小时
    -   mm 表示分钟
    -   ss 表示秒
-   所有的成分都是必需的！

```xml
<xs:element name="start" type="xs:time"/>
```

```xml
<start>09:00:00</start>
```

或

```xml
<start>09:30:10.5</start>
```

## 日期时间

>   DateTime Data Type

-   `"YYYY-MM-DDThh:mm:ss"`
    -   YYYY 表示年份
    -   MM 表示月份
    -   DD 表示日
    -   T 表示必需的时间部分的起始
    -   hh 表示小时
    -   mm 表示分钟
    -   ss 表示秒
-   所有的成分都是必需的！

```xml
<xs:element name="startdate" type="xs:dateTime"/>
```

```xml
<startdate>2002-05-30T09:00:00</startdate>
```

或

```xml
<startdate>2002-05-30T09:30:10.5</startdate>
```

## 时区

可以通过在日期/时间/日期时间后加一个 "Z" 的方式，使用世界调整时间（UTC time）来输入一个日期/时间/日期时间

```xml
<startdate>2002-05-30T09:30:10Z</startdate>
```

或者也可以通过在时间后添加一个正的或负时间的方法，来规定以世界调整时间为准的偏移量

```xml
<startdate>2002-05-30T09:30:10-06:00</startdate>
```

或

```xml
<startdate>2002-05-30T09:30:10+06:00</startdate>
```

## 持续时间

>   Duration Data Type

用于规定时间间隔

-   `"PnYnMnD[TnHnMnS]"`
    -   P 表示周期(**必需**)
    -   nY 表示年数
    -   nM 表示月数
    -   nD 表示天数
    -   T 表示时间部分的起始 （如果需要规定小时、分钟和秒，则此选项为必需）
    -   nH 表示小时数
    -   nM 表示分钟数
    -   nS 表示秒数

```xml
<xs:element name="period" type="xs:duration"/>
```

```xml
<period>P5Y</period>
```

一个 5 年的周期。

```xml
<period>P5Y2M10D</period>
```

一个 5 年、2 个月及 10 天的周期。

```xml
<period>P5Y2M10DT15H</period>
```

一个 5 年、2 个月、10 天及 15 小时的周期。

```xml
<period>PT15H</period>
```

一个 15 小时的周期。

### 负的持续时间

在 P 之前输入减号

```xml
<period>-P10D</period>
```

一个负 10 天的周期。

## 日期和时间数据类型

| 名称       | 描述                                  |
| :--------- | :------------------------------------ |
| date       | 定义一个日期值                        |
| dateTime   | 定义一个日期和时间值                  |
| duration   | 定义一个时间间隔                      |
| gDay       | 定义日期的一个部分 - 天 (DD)          |
| gMonth     | 定义日期的一个部分 - 月 (MM)          |
| gMonthDay  | 定义日期的一个部分 - 月和天 (MM-DD)   |
| gYear      | 定义日期的一个部分 - 年 (YYYY)        |
| gYearMonth | 定义日期的一个部分 - 年和月 (YYYY-MM) |
| time       | 定义一个时间值                        |

## 限定

>   Restriction

-   enumeration
-   maxExclusive
-   maxInclusive
-   minExclusive
-   minInclusive
-   pattern
-   whiteSpace

