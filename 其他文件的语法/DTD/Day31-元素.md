# 元素

## 声明

```xml-dtd
<!ELEMENT 元素名称 类别>
```

或

```xml-dtd
<!ELEMENT 元素名称 (元素内容)>
```

## 空

空元素通过类别关键词EMPTY进行声明：

```xml-dtd
<!ELEMENT 元素名称 EMPTY>
```

例如

```xml-dtd
<!ELEMENT br EMPTY>
```

```xml
<br />
```

## PCDATA

只有 PCDATA 的元素通过圆括号中的 #PCDATA 进行声明：

```dtd
<!ELEMENT 元素名称 (#PCDATA)>
```

例如

```dtd
<!ELEMENT hello (#PCDATA)>
```

```xml-dtd
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE note SYSTEM "note-test.dtd">
<note>
    你好世界
</note>
```

## ANY

通过类别关键词 ANY 声明的元素，可包含任何可解析数据的组合：

```xml-dtd
<!ELEMENT 元素名称 ANY>
```

```xml-dtd
<!ELEMENT note ANY>
```

```xml-dtd
<note>
    你好世界
    <![CDATA[
        全部可以加入!
    ]]>
    <!--当然空也可-->
    <note/>
    <note>
        尊嘟假嘟?
    </note>
</note>
```

## 子元素（序列）

### 单个子元素

```xml-dtd
<!ELEMENT 元素名称 (子元素名称 1)>
```

例子

```xml
<!ELEMENT note (label)>
        <!ELEMENT label EMPTY>
```

```xml
<note>
    <label/>
</note>
```

### 多个子元素

```xml-dtd
<!ELEMENT 元素名称 (子元素名称 1,子元素名称 2,.....)>
```

###

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE note SYSTEM "note-test.dtd">
<note>
    <from>John</from>
    <to>George</to>
    <heading>Reminder</heading>
    <body>Don't forget the meeting!</body>
</note>
```

```dtd
<!ELEMENT note (from,to,heading,body)>
        <!ELEMENT from (#PCDATA)>
        <!ELEMENT to (#PCDATA)>
        <!ELEMENT heading (#PCDATA)>
        <!ELEMENT body (#PCDATA)>
```

### 不定量子元素

出现次数符号

-   一次或多次 `+`

     ```xml-dtd
    <!ELEMENT 元素名称 (子元素名称+)>
     ```

-   零次或多次 `*`

    ```xml-dtd
    <!ELEMENT 元素名称 (子元素名称*)>
    ```

-   零次或一次 `?`

    ```xml-dtd
    <!ELEMENT 元素名称 (子元素名称?)>
    ```

-   **既非**型子元素

    ```xml-dtd
    <!ELEMENT 元素名称 (子元素0,...(子元素1|子元素2),...)>
    ```

    ```xml-dtd
    <!ELEMENT note (to,from,header,(message|body))>
    ```

    "note" 元素必须包含 "to" 元素、"from" 元素、"header" 元素，以及非 "message" 元素既 "body" 元素。

    这个的优先级似乎比逗号低, 所以要**加括号**

### 混合型

```xml-dtd
<!ELEMENT note (#PCDATA|to|from|header|message)*>
```

## 实践

pom.xml的dependencies部分

```xml-dtd
<!ELEMENT dependencies (dependency+)>
        <!ELEMENT dependency (groupId,artifactId,version ?, scope ?)>
        <!ELEMENT groupId (#PCDATA)>
        <!ELEMENT artifactId (#PCDATA)>
        <!ELEMENT version (#PCDATA)>
        <!ELEMENT scope (#PCDATA)>
```

```xml-dtd
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE dependencies SYSTEM "define.dtd">
<dependencies>
    <dependency>
        <artifactId>junit</artifactId>
        <groupId>junit</groupId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>dom4j</groupId>
        <artifactId>dom4j</artifactId>
    </dependency>
</dependencies>
```

