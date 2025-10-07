# 实体

-   用于定义引用普通文本或特殊字符的**快捷方式的变量**。
-   实体引用是对实体的引用。



##内部实体声明

```xml-dtd
<!ENTITY 实体名称 "实体的值">
```

```xml-dtd
<!ELEMENT note (#PCDATA)>
        <!ENTITY writer "&hello;">
        <!ENTITY hello "Hello World">
```

```xml
<note>&writer;&hello;</note>
```

-   可以调用到HelloWorld
-   不可以递归😓

## 外部实体声明

```xml-dtd
<!ENTITY writer SYSTEM "http://www.w3school.com.cn/dtd/entities.dtd">
<!ENTITY copyright SYSTEM "http://www.w3school.com.cn/dtd/entities.dtd">
```

## 预定义实体

| 实体引用 | 字符 |
| :------- | :--- |
| `&lt;`   | <    |
| `&gt;`   | >    |
| `&amp;`  | &    |
| `&quot;` | "    |
| `&apos`; | '    |