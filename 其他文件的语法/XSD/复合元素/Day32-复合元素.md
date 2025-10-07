# 复合元素

## 复杂类型

`<xs:complexType>`定义类型, 让元素有更多的设置选项

##复合元素

包含其他元素及/或属性的 XML 元素。

-   空元素
-   包含其他元素的元素
    -   `xs:sequence` 表示被定义的元素必须按上面的定义出现在外层元素中
-   仅包含文本的元素
-   包含元素和文本的元素

上述元素均可包含属性

## 声明

`sequence`指定元素里可以包含更多元素

```xml
<xs:element name="person">
    <xs:complexType>
        <xs:sequence>
            <xs:element name="firstname" type="xs:string"/>
            <xs:element name="lastname" type="xs:string"/>
        </xs:sequence>
    </xs:complexType>
</xs:element>
```

或

```xml
<xs:element name="employee" type="Person"/>
<xs:element name="student" type="Person"/>
<xs:element name="teacher" type="Person"/>

<xs:complexType name="Person">
    <xs:sequence>
        <xs:element name="firstname" type="xs:string"/>
        <xs:element name="lastname" type="xs:string"/>
    </xs:sequence>
</xs:complexType>
```

## 疑似继承

```xml
<xs:complexType name="Person">
    <xs:sequence>
        <xs:element name="firstname" type="xs:string"/>
        <xs:element name="lastname" type="xs:string"/>
        <xs:element name="age" type="xs:unsignedInt"/>
    </xs:sequence>
</xs:complexType>

<xs:complexType name="Student">
    <xs:complexContent>
        <xs:extension base="Person">
            <xs:sequence>
                <xs:element name="score" type="xs:unsignedInt"/>
                <xs:element name="schoolId" type="xs:string"/>
                <xs:element name="class" type="xs:string"/>
            </xs:sequence>
        </xs:extension>
    </xs:complexContent>
</xs:complexType>
```