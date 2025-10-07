# XML 文档构建模块

所有的 XML 文档（以及 HTML 文档）均由以下简单的构建模块构成：

-   元素
-   属性
-   实体
-   PCDATA
-   CDATA

## 元素

>   Element

标签那个东西

```xml
<note>
    <from>John</from>
    <to>George</to>
    <heading>Reminder</heading>
    <body>Don't forget the meeting!</body>
</note>
```

-   `<note>`
-   `<from>`
-   `<to>`
-   `<body>`
-   `<heading>`

元素内的值可以是字符串, 其他元素, 或空

## 属性

```xml
<img src="computer.gif" />
```

-   `img`  元素的名称
-   `src`  属性的名称
-   `computer.gif` 属性的值, 总是被引号包围
-   元素本身为空，它被一个 `/` 关闭

## 实体

实体是用来定义**普通文本的变量**。实体引用是对实体的引用。

大多数同学都了解这个 HTML 实体引用："`&nbsp;`"。这个“无折行空格”实体在 HTML 中被用于在某个文档中插入一个额外的空格。

当文档被 XML 解析器解析时，实体就会被展开。

### 预定义实体

| 实体引用 | 字符 |
| :------- | :--- |
| `&lt;`   | <    |
| `&gt;`   | >    |
| `&amp;`  | &    |
| `&quot;` | "    |
| `&apos`; | '    |

### PCDATA

>   parsed character data 被解析的字符数据

可把字符数据想象为 XML 元素的开始标签与结束标签之间的文本。

**PCDATA 是会被解析器解析的文本。这些文本将被解析器检查实体以及标记。**

文本中的标签会被当作标记来处理，而实体会被展开。

不过，被解析的字符数据不应当包含任何 &、< 或者 > 字符；需要使用 &amp;、&lt; 以及 &gt; 实体来分别替换它们。

### CDATA

>   character data 的意思是字符数据

*CDATA 是不会被解析器解析的文本。*

在这些文本中的标签不会被当作标记来对待，其中的实体也不会被展开。

算是一种多行注释

