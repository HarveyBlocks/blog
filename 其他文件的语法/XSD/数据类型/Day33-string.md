# 字符串

字符串数据类型可包含字符、换行、回车以及制表符

XML 处理器就不会更改字符串数据类型中的值

```xml
<xs:element name="customer" type="xs:string"/>
```

```xml
<customer>	John Smith	</customer>
```

## 规格化

>NormalizedString Data Type 规格化字符串数据类型

**XML 处理器会移除规格化字符串数据类型的折行，回车以及制表符**

```xml
<xs:element name="customer" type="xs:normalizedString"/>
```

```xml
<customer>	John Smith	</customer>
```

**XML 处理器会使用空格替换所有的制表符**

## Token

>Token Data Type

但是 XML 处理器会移除Token的换行符、回车、制表符、开头和结尾的空格以及（连续的）空格。

```xml
<xs:element name="customer" type="xs:token"/>
```

```xml
<customer>	John Smith	</customer>
```

**XML 解析器会移除制表符。**

## 衍生字符串类型

| 名称             | 描述                                                         |
| :--------------- | :----------------------------------------------------------- |
| ENTITIES         |                                                              |
| ENTITY           |                                                              |
| ID               | 在 XML 中提交 ID 属性的字符串 (仅与 schema 属性一同使用)     |
| IDREF            | 在 XML 中提交 IDREF 属性的字符串(仅与 schema 属性一同使用)   |
| IDREFS language  | 包含合法的语言 id 的字符串                                   |
| Name             | 包含合法 XML 名称的字符串                                    |
| NCName           |                                                              |
| NMTOKEN          | 在 XML 中提交 NMTOKEN 属性的字符串 (仅与 schema 属性一同使用) |
| NMTOKENS         |                                                              |
| normalizedString | 不包含换行符、回车或制表符的字符串                           |
| QName            |                                                              |
| string           | 字符串                                                       |
| token            | 不包含换行符、回车或制表符、开头或结尾空格或者多个连续空格的字符串 |

## 限定

>Restriction

可与字符串数据类型一同使用的限定：

-   enumeration
-   length
-   maxLength
-   minLength
-   pattern (NMTOKENS、IDREFS 以及 ENTITIES 无法使用此约束)
-   whiteSpace

