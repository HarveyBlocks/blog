# 简易元素

>   只包含文本的元素。它不会包含任何其他的元素或属性。

文本有很多类型。

-    XML Schema 定义中包括的类型中的一种（布尔、字符串、数据等等
-   自行定义的定制类型



可向数据类型添加限定（即 *facets*），以此来限制它的内容，或者可以要求数据**匹配特定的模式**

## 声明

```xml
<xs:element name="NAME" type="TYPE"/>
```

### 常用文件类型

![img](../../assets/Day32-%E7%AE%80%E6%98%93%E5%85%83%E7%B4%A0/05104256-eb9c5f9945f34d92af0c1e31added34d.jpg)

-   缺省->空
-   xs:string
-   xs:decimal
-   xs:integer
-   xs:boolean
-   xs:date
-   xs:time



```xml
<xs:element name="name" type="xs:string"/>
<xs:element name="age" type="xs:integer"/>
<xs:element name="boy" type="xs:boolean"/>
<xs:element name="birthday" type="xs:date"/>
```

```xml
<name>Harvey Blocks</name>
<age>20</age>
<boy>true</boy>
<birthday>1980-03-27</birthday>
```

存在顺序的限制

## 默认值和固定值

###default

```xml
<xs:element name="color" type="xs:string" default="red"/>
```

###fixed

```xml
<xs:element name="color" type="xs:string" fixed="red"/>
```





```xml
<xs:element name="radius" type="xs:integer"/>
<xs:element name="pi" type="xs:double" fixed="3.14"/>
```

```xml
<radius>12</radius>
<!--不能不写, 要写还只能写这个值😓-->
<pi>3.14</pi>
```

## 元素个数

```xml
<xs:element name="circle" minOccurs="0" maxOccurs="unbounded">
```

-   `minOccurs`至少需要几个, 默认"1"
-   `maxOccurs` 至多需要几个, 默认"1"