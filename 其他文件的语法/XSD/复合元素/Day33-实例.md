将每个模块分割使其更有条理



```xml
<?xml version="1.0" encoding="UTF-8" ?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <!-- 简易元素的定义 -->
    <xs:element name="orderPerson" type="xs:string"/>
    <xs:element name="name" type="xs:string"/>
    <xs:element name="address" type="xs:string"/>
    <xs:element name="city" type="xs:string"/>
    <xs:element name="country" type="xs:string"/>
    <xs:element name="title" type="xs:string"/>
    <xs:element name="note" type="xs:string"/>
    <xs:element name="quantity" type="xs:positiveInteger"/>
    <xs:element name="price" type="xs:decimal"/>

    <!-- 属性的定义 -->
    <xs:attribute name="orderId" type="xs:string"/>

    <!-- 复合元素的定义 -->
    <xs:element name="shipTo">
        <xs:complexType>
            <xs:sequence>
                <xs:element ref="name"/>
                <xs:element ref="address"/>
                <xs:element ref="city"/>
                <xs:element ref="country"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>
    <xs:element name="item">
        <xs:complexType>
            <xs:sequence>
                <xs:element ref="title"/>
                <xs:element ref="note" minOccurs="0"/>
                <xs:element ref="quantity"/>
                <xs:element ref="price"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>

    <xs:element name="shipOrder">
        <xs:complexType>
            <xs:sequence>
                <xs:element ref="orderPerson"/>
                <xs:element ref="shipTo"/>
                <xs:element ref="item" maxOccurs="unbounded"/>
            </xs:sequence>
            <xs:attribute ref="orderId" use="required"/>
        </xs:complexType>
    </xs:element>

</xs:schema>
```