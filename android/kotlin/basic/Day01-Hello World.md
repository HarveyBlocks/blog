# Hello World

>   简单介绍一些语法和语法糖

-   有无分号无所谓, 如果一定要把多个语句写一行, 就会加上分号

## 注释

-   单行注释`// 注释`
-   多行注释`/*注释*/`

## 包的定义与导入

包的声明应该源文件的顶部

```kotlin
package org.harvey.kotlin.learn
```

## 程序入口

### 无参

```kotlin
fun main() {
    println("Hello World");
}
```

## 有参

```kotlin
fun main(args: Array<String>) {
    println(args.contentToString()) // []
}
```

## 标准输入输出

### 输出

无换行

```kotlin
print("Hello");
print(" ");
print("World");
// Hello World
```

输出并换行

```kotlin
println("Hello World");
```

### 输入

API:

```kotlin
val word = readln()
println(word);
```

## 函数

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b;
}
```

函数体是表达式, 返回值类型靠推断

```kotlin
fun add(a: Int, b: Int) = a + b
```

很奇妙, 写了函数体的, 没办法做类型推断

无返回值的函数

```kotlin
fun printHello(name: String): Unit {
    println("Hello " + name);
}
```

or 直接不写返回值

Unit甚至是一个类

```kotlin
package kotlin

/**
 * The type with only one value: the `Unit` object. This type corresponds to the `void` type in Java.
 */
public object Unit {
    override fun toString(): String = "kotlin.Unit"
}
```

这样甚至能访问无返回值函数的返回值, 而不会编译错误

```kotlin
fun printHello(name: String) {
    println("Hello " + name);
}

fun main() {
    print(printHello("Trump"));
}
```

## 变量

用`val` 或者`var` 定义变量

`val`还有只读的效果

```kotlin
var|val identifier[: type] = initialization;
```

Kotlin居然不支持声明和初始化分开, 还必须在声明的时候初始化

可以在文件层声明变量

```ts
fun printHello(): Int {
    println("printHello"); // 先
    return 0;
}

val A: Int = printHello();

fun main() {
    println("main"); // 后
}
```

文件级的变量是在什么时机被加载的呢? 文件加载的时候

import文件级变量的时候, 会在import 的时候加载吗? 不, 是在第一次尝试读取变量的值的时候加载

## 字符串模板

对于单独的变量, 使用`$variable`在字符串中插值

```kotlin
"hello $name"
```

对于需要对字符串进行一定运算的, 使用`${caculate...}`在字符串中运算

```kotlin
"hello ${student.name}"
```

## if 表达式

代替了三元表达式

```kotlin
if (condition) result1 else result2;
```

## range

>   区间

```kotlin
val range = start..end;
```

区间是两边包含的

原型是IntRange

```kotlin
val ints: IntRange = IntRange(1, 2);
```

IntRange实现了ClosedRange, 此外还有LongRange和CharRange

### in

>   重载operator fun contains()

判断在区间内

```kotlin
if (2 in 1..2) {
    println("contains")
}
```

判断在区间外

```kotlin
if (2 !in 1..2) {
    println("not contains")
}
```

### 迭代

>   实现Iterable或者就是Iterator或者operator fun Iterator()

```kotlin
for (x in 1..5) {
    print(x)
}
```

这样也可以

```kotlin
for (x in (1..5).iterator()) {
    print(x)
}
```

### downTo

换向

`10..1`还是递增, 而且不会产生编译错误, 甚至不会警告

```kotlin
val ints: IntProgression = 10 downTo 1; // IntProgression不允许直接实例化
val a = 3;
if (a in ints) {
    println("yes")
}
for (x in ints) {
    print(x)
}
```

### step

```kotlin
for (x in 1..10 step 2) {
    print(x)
}
println()
for (x in 9 downTo 0 step 3) {
    print(x)
}
```

## 自定义运算符

>   infix

downTo和step其实都是自定义运算符( or 理解成 在参数中间的函数调用 )

自定义一个add

```kotlin
public infix fun Int.add(to: Int): Int {
    return this + to;
}

fun main() {
    val ints: Int = 1 add 2;
    print(ints);
}
```

```kotlin
package org.harvey.kotlin.learn

import kotlin.random.Random

enum class Sex {
    MALE, FEMALE;
}

class Person(private val sex: Sex) {
    infix fun add(to: Person): Person {
        if (this.sex == to.sex) {
            throw Exception("woc, 同!");
        }
        return Person(sex = if (Random(System.currentTimeMillis()).nextFloat() > 0.5) Sex.FEMALE else Sex.MALE);
    }
}
fun main() {
    val child: Person = Person(Sex.FEMALE) add Person(Sex.MALE);
}
```

## 空值与空值检测

空值不再是任何类型的子类, 可以赋值给任何类型, 必须使用`?`指示一个类型可以被`null`赋值

-   变量可以被赋值空

    ```kotlin
    val|var identifier:type?=null;
    ```

-   函数签名上的类型使用空值

    ```kotlin
    fun function(str: String?): Int? {
        return null;
    }
    ```

在kotlin中, 不对可能是null值的变量进行检查, 会产生编译错误

除非使用`!!`忽略

```kotlin
fun function(str: String?): Int? {
    return str!!.length;
}
```

## is 类型检查

在使用is检查的分支内自动类型转换

```kotlin
if (obj is String && obj.length > 0) {
    return obj.length
}
```

否定

```kotlin
if (obj !is String ) {
    return null
}
// 在其后进行自动类型转换
return obj.length;
```

## 语法糖

太多了, 以后再说

-   DTO (数据类)

    ```kotlin
    data class Customer(val name: String, val email: String)
    ```

    -   Getter和Setter
    -   equals/hashCode/toString/copy

-   函数默认实参

-   安全的标准读取

    ```kotlin
    val wrongInt = readln().toIntOrNull()
    println(wrongInt)
    // null

    val correctInt = readln().toIntOrNull()
    println(correctInt)
    // 13
    ```

-   类型判断

    ```kotlin
    when (x) {
        is Foo -> ……
        is Bar -> ……
        else   -> ……
    }
    ```

-   只读list/map

    ```kotlin
    val list = listOf("a", "b", "c")
    val map = mapOf("a" to 1, "b" to 2, "c" to 3)
    ```

-   映射

    ```kotlin
    println(map["key"])
    map["key"] = value
    ```

-   遍历map/list\<pair\>

    ```kotlin
    for ((k, v) in map) {
        println("$k -> $v")
    }
    ```

-   lazy属性

    ```kotlin
    val p: String by lazy { // 该值仅在首次访问时计算
        // 计算该字符串
    }
    ```

-   扩展函数, 在类声明外创建有关方法, 增强类

    ```kotlin
    fun String.spaceToCamelCase() { …… }

    "Convert this to camelcase".spaceToCamelCase()
    ```

