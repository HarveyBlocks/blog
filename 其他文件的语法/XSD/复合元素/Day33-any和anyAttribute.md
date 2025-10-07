#`<any>`&`<anyAttribute>`

作用是预留一个空位给其他任意元素/属性

这个空位, 不能留给自己(不能直接嵌套), 但可以留给自己的父元素, 然后再嵌套自己😀

##`<any>`

```xml
<xs:complexType name="personType">
    <xs:choice>
        <xs:annotation>
            <xs:documentation><![CDATA[
        然后这个选择指示器, 其中一个最少0个,
        也就是说可以什么都不选,
        本质上其实是选择了第二个, 但是使用了有0个
            ]]></xs:documentation>
        </xs:annotation>
        <xs:element name="child"/>
        <xs:any minOccurs="0"/><!--不能在all中使用any-->
    </xs:choice>
    <xs:attribute name="firstname" type="xs:string"/>
    <xs:attribute name="lastname" type="xs:string"/>
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

##可扩展的文档

准备两个xsd文档

注意`targetNamespace`和`xmlns` , 是命名空间

`person.xsd`

给出`any`交由其他文件拓展

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.harvey.com/schema/perosn"
           xmlns="http://www.harvey.com/schema/perosn"
           elementFormDefault="qualified">
    <xs:complexType name="personType">
        <xs:choice>
            <xs:element name="child"/>
            <xs:any minOccurs="0"/>
        </xs:choice>
        <xs:attribute name="firstname" type="xs:string"/>
        <xs:attribute name="lastname" type="xs:string"/>
        <xs:attribute name="birthday" type="xs:date"/>
    </xs:complexType>
    <xs:element name="persons">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="person" type="personType" minOccurs="0" maxOccurs="unbounded"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>

</xs:schema>
```

`country.xsd`, 提供拓展

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.harvey.com/schema/country"
           xmlns="http://www.harvey.com/schema/country"
           elementFormDefault="qualified">
    <xs:complexType name="countryType">
        <xs:attribute name="name" type="xs:string"/>
    </xs:complexType>
    <xs:element name="country" type="countryType"/>

</xs:schema>
```

测试最终使用的`test.xml`

 `xmlns:con`声明命名空间, 其实也有`xmlns:per`声明person, 但是奈何persons作为根节点可以选择使用默认命名空间, 可以不写

`"http://www.harvey.com/schema/country"`和`country.xsd`里的命名空间一致

`country.xsd`是文件名

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persons xmlns="http://www.harvey.com/schema/perosn"
         xmlns:con="http://www.harvey.com/schema/country"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.harvey.com/schema/perosn person.xsd
          http://www.harvey.com/schema/country country.xsd">

    <person lastname="Blocks" firstname="Harvey" birthday="2004-10-08">
        <con:country name="China!"/> <!--拓展了其他文件-->
    </person>
</persons>

```

##`<anyAttribute>`

```xml
<xs:complexType name="personType">
    <xs:attribute name="firstname" type="xs:string"/>
    <xs:attribute name="lastname" type="xs:string"/>
    <xs:attribute name="birthday" type="xs:date"/>
    <xs:anyAttribute/>
</xs:complexType>
```

### 属性的文件拓展

`person.xsd`

给出`anyAttribute`交由其他文件拓展

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.harvey.com/schema/perosn"
           xmlns="http://www.harvey.com/schema/perosn"
           elementFormDefault="qualified">
    <xs:complexType name="personType">
        <xs:attribute name="firstname" type="xs:string"/>
        <xs:attribute name="lastname" type="xs:string"/>
        <xs:attribute name="birthday" type="xs:date"/>
        <xs:anyAttribute/> <!--拓展点-->
    </xs:complexType>
    <xs:element name="persons">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="person" type="personType" minOccurs="0" maxOccurs="unbounded"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>

</xs:schema>
```

`country.xsd`, 提供拓展

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.harvey.com/schema/country"
           xmlns="http://www.harvey.com/schema/country"
           elementFormDefault="qualified">

    <xs:attribute name="country" type="xs:string"/>

</xs:schema>
```

最终使用`test.xml`

命名空间的声明一致

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persons xmlns="http://www.harvey.com/schema/perosn"
         xmlns:con="http://www.harvey.com/schema/country"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.harvey.com/schema/perosn person.xsd
          http://www.harvey.com/schema/country country.xsd">

    <person lastname="Blocks" firstname="Harvey"
            birthday="2004-10-08" con:country="China!"/>
</persons>
```

