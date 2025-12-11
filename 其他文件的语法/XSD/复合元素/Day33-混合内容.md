# 混合内容

混合的复合类型可包含属性、元素以及文本。

```xml
<xs:complexType mixed="true">

</xs:complexType>
```

示例: 

```xml
<xs:element name="person">
  <xs:complexType mixed="true">
    <xs:sequence>
      <xs:element name="name" type="xs:string"/>
      <xs:element name="age" type="xs:integer"/>
      <xs:element name="birthday" type="xs:date"/>
    </xs:sequence>
  </xs:complexType>
</xs:element>
```

```xml
<person>
    一些话
    <name>Harvey</name>
    可以在标签之间
    <age>12</age>
    虽然不知道有什么意义
    <birthday>2004-10-10</birthday>
</person>
```

