# 杂项

-   逻辑 Boolean 
-   base64Binary/hexBinary
-   十六进制
-   浮点
-   双精度
-   anyURI
-   NOTATION
-   boolean
-   QName

## Boolean

```xml
<xs:attribute name="disabled" type="xs:boolean"/>
```

```xml
<prize disabled="true">999</prize>
```

合法的布尔值是 true、false、1（表示 true） 以及 0（表示 false）。

## Binary

-   base64Binary (Base64 编码的二进制数据)
-   hexBinary (十六进制编码的二进制数据)

```xml
<xs:element name="blobsrc" type="xs:hexBinary"/>
```

## AnyURI

```xml
<xs:attribute name="src" type="xs:anyURI"/>
```

```xml
<pic src="http://www.w3school.com.cn/images/smiley.gif" />
```

若 URI 含有空格，用 %20 替换它们。

## Restriction

-   enumeration (布尔数据类型无法使用此约束*)
-   length (布尔数据类型无法使用此约束)
-   maxLength (布尔数据类型无法使用此约束)
-   minLength (布尔数据类型无法使用此约束)
-   pattern
-   whiteSpace

==约束指 constraint。(不明, 待考)==

