# KDoc

KDoc 注释以 `/**` 开头、以 `*/` 结尾

## 块标签

不支持`@deprecated`标签, 但有`@Deprecated`注解

### @param

-   函数参数的值
-   函数的参数的类型参数
-   类的类型参数
-   属性的类型参数
-   函数的类型参数

```kotlin
/**
 * @param name 描述。
 * @param[name] 描述。
 */
```

### @return

-   函数返回值

### @constructor

-   类的主构造函数

### @receiver

-   扩展函数的接收者

### @property *name*

-   类中具有指定名称的属性
-   在主构造函数中声明的属性

### @throw  *class*/@exception *class*

-   方法可能抛出的异常

### @sample *identifier*

显示如何使用该元素的示例

-   identifier 指带限定的名称的函数的主体嵌入到当前元素的文档中

### @see *identifier*

### @author

### @since *version*

### @suppress

从生成的文档中排除元素。可用于不是模块的官方 API 的一部分但还是必须在对外可见的元素

## 内联标记

常规Markdown

```kotlin
/**
 * 链接到方法 [foo]
 * 链接到[方法][foo]
 */
```

## Dokka

使用Dokka生成Kotlin文档

## 代码规范

### 幕后属性

如果一个用于细节实现的私有属性和一个共有属性同名, 在私有属性名前加`_`

### 缩写

-   对于两个字母的缩写，两字母都大写。例如，`IOStream`。
-   对于超过两个字母的缩写，只大写第一个字母。例如，`XmlFormatter` 或 `HttpInputStream`。

### 省略

-   尽量省略分号
-   无返回值就省略返回值类型
-   字符串模板能`$variable`的就不要花括号
-   简短、非嵌套的 lambda 表达式中建议使用 `it` 用法而不是显式声明参数。而在有参数的嵌套 lambda 表达式中，始终显式声明参数

