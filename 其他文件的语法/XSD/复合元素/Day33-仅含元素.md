# 仅含元素

只能包含*其他* *元素*的元素。

元素中不能包含文本, 只能包含元素

## 声明

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

-   `sequence` 表示被定义的元素必须按上面的定义出现在 `person` 元素中

```xml
<person>
    <firstname>Harvey</firstname>
    <lastname>Blocks</lastname>
</person>
```