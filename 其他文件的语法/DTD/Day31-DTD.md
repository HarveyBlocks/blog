# DTD

>   **D**ocument **T**ype **D**efine 文档类型定义



## 声明

### XML文件内部的声明

声明本文件应该遵守的格式

```xml
<!DOCTYPE 根元素 [元素声明]>
```

```xml-dtd
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE note [
        <!--标签在使用的顺序-->
        <!ELEMENT note (from,to,heading,body)>
        <!--以下是各标签, 定义没有顺序-->
        <!ELEMENT to      (#PCDATA)>
        <!ELEMENT from    (#PCDATA)>
        <!ELEMENT heading (#PCDATA)>
        <!ELEMENT body    (#PCDATA)>
        ]>
<note>
    <from>John</from>
    <to>George</to>
    <heading>Reminder</heading>
    <body>Do not forget the meeting!</body>
</note>
```



-   `<!ELEMENT note (from,to,heading,body)> `定义下的元素
-   `<!ELEMENT to      (#PCDATA)>` 元素和元素类型
-   `#PCDATA` 一种元素类型





### 外部文件声明

```xml
<!DOCTYPE 根元素 SYSTEM "文件名">
```

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

