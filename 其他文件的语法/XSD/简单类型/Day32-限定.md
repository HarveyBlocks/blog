# 限定

>   restriction	用于为 XML 元素或者属性定义可接受的值
>
>   facet			  对 XML 元素的限定被称为



## 对值的限定

### 数值范围

```xml
<xs:element name="age">
    <xs:simpleType>
        <xs:restriction base="xs:integer">
            <xs:minInclusive value="0"/>
            <xs:maxInclusive value="120"/>
        </xs:restriction>
    </xs:simpleType>
</xs:element>
```

对age的上下限的限制

### 枚举约束

>   enumeration constraint

```xml
<xs:element name="sex">
    <xs:simpleType>
        <xs:restriction base="xs:string">
            <xs:enumeration value="MALE"/>
            <xs:enumeration value="FEMALE"/>
        </xs:restriction>
    </xs:simpleType>
</xs:element>
```



还可以写成

```xml
<xs:simpleType name="Sex">
    <xs:restriction base="xs:string">
        <xs:enumeration value="MALE"/>
        <xs:enumeration value="FEMALE"/>
    </xs:restriction>
</xs:simpleType>
<xs:element name="person">
    <xs:complexType>
        <xs:sequence>
            <xs:element name="name" type="Sex"/>
        </xs:sequence>
    </xs:complexType>
</xs:element>
```
```xml
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.harvey.com/schema/node"
           xmlns="http://www.harvey.com/schema/node"
           elementFormDefault="qualified">
    <xs:simpleType name="Sex">
        <xs:restriction base="xs:string">
            <xs:enumeration value="MALE"/>
            <xs:enumeration value="FEMALE"/>
        </xs:restriction>
    </xs:simpleType>
    <xs:element name="person">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="name" type="Sex"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>
</xs:schema>
```

所有元素, 属性都可以使用改类型

### 正则匹配

>pattern constraint

不确定是不是全部的正则规则都能适用, 也不确定有什么不同

```xml
<xs:element name="name">
    <xs:simpleType>
        <xs:restriction base="xs:string">
            <!--英文字母, 至少一个-->
            <xs:pattern value="[a-zA-Z ]+"/>
        </xs:restriction>
    </xs:simpleType>
</xs:element>
```





### 对空白字符的限定

>   whitespace characters

```xml
<xs:element name="address">
    <xs:simpleType>
        <xs:restriction base="xs:string">
            <xs:whiteSpace value="preserve"/>
        </xs:restriction>
    </xs:simpleType>
</xs:element>
```

-    `preserve`     XML 处理器不会移除任何空白字符

-   `replace`		XML 处理器将移除所有空白字符
    -   移除 换行、回车、空格以及制表符
-   `collapse`      XML 处理器将移除所有空白字符
    -   换行、回车、空格以及制表符会被替换为空格
    -   开头和结尾的空格会被移除
    -   多个连续的空格会被缩减为一个单一的空格





### 对长度的限定

```xml
<xs:element name="password">

<xs:simpleType>
  <xs:restriction base="xs:string">
    <xs:length value="8"/>
  </xs:restriction>
</xs:simpleType>

</xs:element>
```

-   `length` 其值必须精确到 8 个字符



```xml
<xs:element name="password">

<xs:simpleType>
  <xs:restriction base="xs:string">
    <xs:minLength value="5"/>
    <xs:maxLength value="8"/>
  </xs:restriction>
</xs:simpleType>

</xs:element>
```





# 限定

| 限定           | 描述                                                      |
| :------------- | :-------------------------------------------------------- |
| enumeration    | 定义可接受值的一个列表                                    |
| fractionDigits | 定义所允许的最大的小数位数。必须大于等于0。               |
| length         | 定义所允许的字符或者列表项目的精确数目。必须大于或等于0。 |
| maxExclusive   | 定义数值的上限。所允许的值必须小于此值。                  |
| maxInclusive   | 定义数值的上限。所允许的值必须小于或等于此值。            |
| maxLength      | 定义所允许的字符或者列表项目的最大数目。必须大于或等于0。 |
| minExclusive   | 定义数值的下限。所允许的值必需大于此值。                  |
| minInclusive   | 定义数值的下限。所允许的值必需大于或等于此值。            |
| minLength      | 定义所允许的字符或者列表项目的最小数目。必须大于或等于0。 |
| pattern        | 定义可接受的字符的精确序列。                              |
| totalDigits    | 定义所允许的阿拉伯数字的精确位数。必须大于0。             |
| whiteSpace     | 定义空白字符（换行、回车、空格以及制表符）的处理方式。    |

