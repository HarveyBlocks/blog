# 指示器

-   Order 指示器
    -   All
    -   Choice
    -   Sequence
-   Occurrence 指示器
    -   maxOccurs
    -   minOccurs
-   Group 指示器
    -   Group name
    -   attributeGroup name

## Order 指示器

用于定义元素的顺序。

### All 指示器

规定子元素可以按照任意顺序出现，且每个子元素必须只出现一次：

```xml
<xs:element name="person">
  <xs:complexType>
    <xs:all>
      <xs:element name="firstname" type="xs:string"/>
      <xs:element name="lastname" type="xs:string"/>
    </xs:all>
  </xs:complexType>
</xs:element>
```

当使用 `<all>` 指示器时，你可以把` <minOccurs> `设置为 0 或者 1，而只能把 `<maxOccurs> `指示器设置为 1

### Choice 指示器

规定可出现某个子元素或者可出现另外一个子元素（非此即彼）：

```xml
<xs:element name="person">
  <xs:complexType>
    <xs:choice>
      <xs:element name="employee" type="employee"/>
      <xs:element name="employer" type="employer"/>
    </xs:choice>
  </xs:complexType>
</xs:element>
```

如需设置子元素出现任意次数，可将 `<maxOccurs>` 设置为`0`, `1`, ..... ,`n`,.....` unbounded` 皆可。

### Sequence 指示器

`<sequence> `规定子元素必须按照特定的顺序出现：

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

`<maxOccurs>` 和`<minOccurs>`随便设置

## Occurrence 指示器

用于定义某个元素出现的频率。

对于所有的 "Order" 和 "Group" 指示器（any、all、choice、sequence、group name 以及 group reference），其中的 maxOccurs 以及 minOccurs 的默认值均为 1。

### maxOccurs 指示器

`<maxOccurs>` 指示器可规定某个元素可出现的最大次数

```xml
<xs:element name="person">
  <xs:complexType>
    <xs:sequence>
      <xs:element name="full_name" type="xs:string"/>
      <xs:element name="child_name" type="xs:string" maxOccurs="10"/>
    </xs:sequence>
  </xs:complexType>
</xs:element>
```

子元素 "child_name" 可在 "person" 元素中最少出现一次(采用默认)，最多出现 10 次

`maxOccurs="unbounded"  ` 声明某个元素的出现次数不受限制

### minOccurs 指示器

`<minOccurs> `指示器可规定某个元素能够出现的最小次数

```xml
<xs:element name="person">
  <xs:complexType>
    <xs:sequence>
      <xs:element name="full_name" type="xs:string"/>
      <xs:element name="child_name" type="xs:string"
      maxOccurs="10" minOccurs="0"/>
    </xs:sequence>
  </xs:complexType>
</xs:element>
```

子元素 "child_name" 可在 "person" 元素中最少出现一次(采用默认)，最多出现 10 次

## Group 指示器

定义相关的数批元素。

### 元素组

元素组通过 group 声明进行定义：

```xml
<xs:group name="组名称">
  ...
</xs:group>
```

必须在 group 声明内部定义一个 Order指示器(all、choice 或者 sequence 元素)

示例: 

```xml
<xs:group name="nameGroup">
    <xs:sequence>
        <xs:element name="firstname" type="xs:string"/>
        <xs:element name="lastname" type="xs:string"/>
    </xs:sequence>
</xs:group>
```

把 group 定义完毕以后，就可以在另一个定义中引用它了：

```xml
<xs:group name="nameGroup">
    <xs:sequence>
        <xs:element name="firstname" type="xs:string"/>
        <xs:element name="lastname" type="xs:string"/>
    </xs:sequence>
</xs:group>

<xs:element name="person" type="personInfo"/>

<xs:complexType name="personInfo">
    <xs:sequence><!--sequence不能和Group的指示器冲突-->
        <xs:group ref="nameGroup"/>
        <xs:element name="birthday" type="xs:date"/>
        <xs:element name="country" type="xs:string"/>
    </xs:sequence>
</xs:complexType>
```

### 属性组

属性组通过 attributeGroup 声明来进行定义：

```xml
<xs:attributeGroup name="组名称">
  ...
</xs:attributeGroup>
```

定义属性组

```xml
<xs:attributeGroup name="nameAttrGroup">
    <xs:attribute name="firstname" type="xs:string"/>
    <xs:attribute name="lastname" type="xs:string"/>
</xs:attributeGroup>
```

在另一个定义中引用它

```xml
<xs:attributeGroup name="nameAttrGroup">
    <xs:attribute name="firstname" type="xs:string"/>
    <xs:attribute name="lastname" type="xs:string"/>
</xs:attributeGroup>
<xs:complexType name="personType">
    <xs:attributeGroup ref="nameAttrGroup"/>
    <xs:attribute name="birthday" type="xs:date"/>
</xs:complexType>
<xs:element name="persons">
    <xs:complexType>
        <xs:sequence>
            <xs:element name="person" type="personType" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>
</xs:element>
```
