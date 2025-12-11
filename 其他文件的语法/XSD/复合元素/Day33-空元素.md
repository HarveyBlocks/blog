# 空元素

不能包含内容，只能含有属性



## 复合空元素

```xml
<xs:element name="product">
  <xs:complexType>
    <xs:complexContent>
      <xs:restriction base="xs:integer">
        <xs:attribute name="price" type="xs:double"/>
      </xs:restriction>
    </xs:complexContent>
  </xs:complexType>
</xs:element>
```

在上面的例子中，我们定义了一个带有复合内容的复合类型。

-   `complexContent`限定或者拓展某个复合类型的内容模型
-   `integer` 声明一个属性但不会引入任何的元素内容



也可以更加紧凑地声明此 "product" 元素：

```xml
<xs:element name="product">
  <xs:complexType>
    <xs:attribute name="price" type="xs:double"/>
  </xs:complexType>
</xs:element>
```



可以为 "product" 元素设置一个属性并引用这个 complexType 名称

```xml
<xs:element name="product" type="prodType"/>

<xs:complexType name="prodType">
  <xs:attribute name="price" type="xs:double"/>
</xs:complexType>
```

如此，若干个元素均可引用相同的复合类型

