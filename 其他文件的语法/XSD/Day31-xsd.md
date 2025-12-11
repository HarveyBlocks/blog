# XSD

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.harvey.com/schema/node"
           xmlns="http://www.harvey.com/schema/node"
           elementFormDefault="qualified">

    <xs:element name="note">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="to" type="xs:string"/>
                <xs:element name="from" type="xs:string"/>
                <xs:element name="heading" type="xs:string"/>
                <xs:element name="body" type="xs:string"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>

</xs:schema>
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<note xmlns="http://www.harvey.com/schema/node"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.harvey.com/schema/node note.xsd">

    <to>George</to>
    <from>John</from>
    <heading>Reminder</heading>
    <body>Don't forget the meeting!</body>
</note>
```

## XSD解释

```xml
xmlns:xs="http://www.w3.org/2001/XMLSchema"
```

显示 schema 中用到的元素和数据类型来自命名空间 "http://www.w3.org/2001/XMLSchema"。(不只是网址, 可以是虚假的网址, 本质是本地文件的引用)

规定了来自命名空间 "http://www.w3.org/2001/XMLSchema" 的元素和数据类型应该使用前缀 `xs：`

这个`xs`是自定义的, 也可以是`xsd` 或者任何东西, 相当于将命名空间赋值给了这个标签, 下次`xs:`之后表示使用`xs` 代表命名空间里的东西

```xml
targetNamespace="http://www.harvey.com/schema/node"
```

显示被此 schema 定义的元素 (note, to, from, heading, body) 来自命名空间： "http://www.harvey.com/schema/node"。

```xml
xmlns="http://www.harvey.com/schema/node"
```

默认的命名空间是 "http://www.harvey.com/schema/node"

```xml
elementFormDefault="qualified"
```

指出任何 XML 实例文档所使用的且在此 schema 中声明过的元素必须被命名空间限定。

## XML引用Schema解释

```xml
<?xml version="1.0" encoding="UTF-8"?>
<note xmlns="http://www.harvey.com/schema/node"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.harvey.com/schema/node note.xsd">

    <to>George</to>
    <from>John</from>
    <heading>Reminder</heading>
    <body>Don't forget the meeting!</body>
</note>
```

```xml
xmlns="http://www.harvey.com/schema/node"
```

规定了默认命名空间的声明。

此声明会告知 schema 验证器，在此 XML 文档中使用的所有元素都被声明于 "http://www.harvey.com/schema/node" 这个命名空间。

```xml
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
```

可用的 XML Schema 实例命名空间

```xml
 xsi:schemaLocation="http://www.harvey.com/schema/node note.xsd"
```

在声明可用的XML Schema实例命名空间后, 可以使用 schemaLocation 属性了。

-   `http://www.harvey.com/schema/node`
    -   需要使用的命名空间
-   `note.xsd`
    -   供命名空间使用的 XML schema 的位置文件

## 全局元素

>Global Elements

 "schema" 元素的直接子元素

本地元素（Local elements）指嵌套在其他元素中的元素。

