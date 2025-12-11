# 属性

>   Attribute List

## 声明

```xml-dtd
<!ATTLIST 元素名称 属性名称 属性类型 默认值>
```

```xml-dtd
<!ELEMENT note (#PCDATA)>
        <!ATTLIST note type>
        <!ATTLIST note name CDATA "Unknown">
```

```xml
<note  name="Hello" type="string">XXX</note>
```

## 属性类型

| 类型               | 描述                               |
| :----------------- | :--------------------------------- |
| CDATA              | 值为字符数据 (character data)      |
| (*en1*\|*en2*\|..) | 此值是枚举列表中的一个值           |
| ID                 | **值为唯一的 id**                  |
| IDREF              | 值为另外一个元素的 id              |
| IDREFS             | 值为其他 id 的列表, *IDEA支持不好* |
| NMTOKEN            | 值为合法的 XML 名称, *未知*        |
| NMTOKENS           | 值为合法的 XML 名称的列表, *未知*  |
| ENTITY             | 值是一个实体 , *IDEA支持不好*      |
| ENTITIES           | 值是一个实体列表                   |
| NOTATION           | 此值是符号的名称, *未知*           |
| xml:               | 值是一个预定义的 XML 值, *未知*    |

### 枚举

```xml-dtd
<!ELEMENT note (#PCDATA)>
        <!ATTLIST note type (int|float)>
```

![image-20240611224806424](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/其他文件的语法/DTD/Day31-属性/image-20240611224806424.png)

检查报错

### ID-IDREF

```xml-dtd
<!ELEMENT notes (note+)>
        <!ELEMENT note EMPTY>
        <!ATTLIST note id ID>
        <!ATTLIST note ref IDREF>
```

```xml-dtd
<!DOCTYPE notes SYSTEM "define.dtd">
<notes>
	<!--允许循环依赖-->
    <note id="001" ref="002"/>
    <note id="002" ref="001"/>
	<!--允许自依赖-->
    <note id="003" ref="003"/>
	<!--ERROR-->
    <note id="003" ref="002"/>
    <note id="005" ref="000"/>
</notes>
```

![image-20240611225210411](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/其他文件的语法/DTD/Day31-属性/image-20240611225210411.png)

### XXX的列表

字符串中用空格分割, IDEA的支持不太好

## 默认值参数

| 值           | 解释                        |
| :----------- | :-------------------------- |
| 值           | 属性的默认值                |
| #REQUIRED    | 属性值是**必需的**          |
| #IMPLIED     | 属性**不是必需**的 **缺省** |
| #FIXED value | 属性值是**固定**的          |

```xml-dtd
<!ELEMENT notes (note+)>
        <!ELEMENT note EMPTY>
        <!ATTLIST note id ID #REQUIRED>
        <!ATTLIST note pi CDATA #FIXED "3.14">
```

```xml
<notes>
    <!--正确-->
    <note id="000"/>
    <!--正确, IDEA推荐去掉pi这个属性-->
    <note id="001" pi="3.14"/>
    <!--PI值出错-->
    <note id="002" pi="12"/>
    <!--出错-->
    <note pi="3.14"/>
</notes>
```

