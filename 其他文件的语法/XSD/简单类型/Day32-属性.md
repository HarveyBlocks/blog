# 属性

所有的属性均作为简易类型来声明

简易元素无法拥有属性。

假如某个元素拥有属性，它就会被当作某种复合类型。但是属性本身总是作为简易类型被声明的。

## 声明

```xml
<xs:attribute name="NAME" type="TYPE"/>
```

### 最常用的类型

-   xs:string
-   xs:decimal
-   xs:integer
-   xs:boolean
-   xs:date
-   xs:time

## 默认和固定

```xml
<xs:attribute name="scope" type="xs:string" default="singleton"/>
```

```xml
<xs:attribute name="pi" type="xs:double" fixed="3.141"/>
```

## 可选和必须

```xml
<xs:attribute name="lang" type="xs:string" use="required"/>
```

-   `optional` 缺省 可选
-   `required` 必须
-   `prohibited` 禁止

##使用

XSD的声明

```xml
<xs:element name="bean">
    <xs:complexType>
        <xs:attribute name="id" type="xs:ID" use="required"/>
        <xs:attribute name="classpath" type="xs:string" use="required"/>
        <xs:attribute name="name" type="xs:string"/>
        <xs:attribute name="scope" type="xs:string" default="singleton"/>
    </xs:complexType>
</xs:element>
```



XML使用

```xml
<bean id="userController" classpath="com.harvey.summer.demo.UserController" scope="singleton"/>
```

