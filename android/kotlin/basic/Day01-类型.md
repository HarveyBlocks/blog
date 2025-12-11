# 类型

-   数字及其无符号

-   Bool

-   字符

-   字符串

-   Any

    -   所有Kotlin Class 的基类, 和Object类的区别是, Any甚至是数字类,Bool, 字符类等的基类
    -   equals()
    -   hashCode()
    -   toString()

-   Nothing

    -   没有示例
    -   一个函数返回值是Nothing时, 表示其永远不会返回, 例如抛出异常或者死循环

    ```kotlin
    public class Nothing private constructor()
    ```

    ```kotlin
    val none: Nothing? = null;
    val number: Int? = none;
    ```

-   Unit

## 数字

整型

-   Byte
-   Short
-   Int
-   Long
-   UByte
-   UShort
-   UInt
-   ULong

浮点型

-   Float
-   Double

### 字面量

-   支持浮点数的科学表示法

后缀

-   `L` 表长整形
-   `F` 表单浮点型
-   `U`表无符号
-   `uL` 表示无符号长整形

前缀

-   `0x` 十六进制
-   `0b` 二进制

允许(任何数字类型的字面量)使用下划线增加字面量可读性

### 装箱和缓存

一般的Int等数字类型使用原型存储, 在使用`Int?`时自动装箱

作为泛型时会装箱

在作为数组的泛型时也会装箱, 故应当使用类型对应的数组, 例如`UIntArray`

### 数字类型转换

-   `toByte(): Byte` (deprecated for Float and Double)
-   `toShort(): Short`
-   `toInt(): Int`
-   `toLong(): Long`
-   `toFloat(): Float`
-   `toDouble(): Double`

### 浮点数的比较

-   NaN总是和NaN相等
-   NaN 比任何其他(包括POSITIVE_INFINITY)元素大
-   `-0.0`小于`0.0`
-   浮点数的区间可用于比较, 不可用于迭代

## Boolean

-   `true`
-   `false`

## 字符

>   Char

字面量用`''`包裹

转义字符

-   `\t`——制表符
-   `\b`——退格符
-   `\n`——换行（LF）
-   `\r`——回车（CR）
-   `\'`——单引号
-   `\"`——双引号
-   `\\`——反斜杠
-   `\$`——美元符

编码Unicode字符`\uFF00`

`digitToInt`转字面量数字

```kotlin
println('1'.digitToInt() + 1); 
```

## 类型检测

>   is 和 !is

### if 分支

```kotlin
if (obj !is String) { // 与 !(obj is String) 相同
    print("Not a String")
} else {
    print(obj.length)
}
```

### when 表达式

```kotlin
when (x) {
    is Int -> print(x + 1)
    is String -> print(x.length + 1)
    is IntArray -> print(x.sum())
}
```

## 类型转换

### 隐式转换

一般情况下转换是隐式的, 例如if分支会自动进行类型转换

`!is` + `&&`, `&&` 后使用变量类型转换

```kotlin
if (x is String && x.length > 0/*类型自动转换*/) {
    print(x.length)
}
```

`is` + `||`, `||` 后使用变量类型转换

```kotlin
if (x !is String || x.length == 0/*自动类型转换*/) return
```

`||`的两边进行类型判断, 则会取共同上级

```kotlin
val y: Any = ArrayList<Int>();
if (y is ArrayList<*> || y is LinkedList<*>) {
    println(y.size)
}
```

隐式转换的可用情形

| 变量           | 情形                                                         |
| -------------- | ------------------------------------------------------------ |
| `val` 局部变量 | 总是可以，[局部委托属性](../高级/Day04-delegate)除外。       |
| `val` 属性     | 如果属性是 `private`、 `internal`，或者该检测在声明属性的同一模块中执行。 隐式转换不能用于 `open` 的属性或者具有自定义 getter 的属性。 |
| `var` 局部变量 | 如果变量在检测及其使用之间未修改、没有在会修改它的 lambda 中捕获、并且不是局部委托属性。 |
| `var` 属性     | 决不可能，因为该变量可以随时被其他代码修改。                 |

### 显式转化

显式转换的语法

```kotlin
val x: String = y as String
```

如果转换不可能，编译器会抛出异常, 如果y是null, 也抛出异常(`val x: String? = y as String?`这样是成功)

也可以写作以下形式, 转换错误会返回null(无论什么样的转换错误)

```kotlin
val x: String? = y as? String
```

