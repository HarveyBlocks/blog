# 控制流程

## 顺序

### 变量

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

### const

编译期常量, 有助于编译器直接把值拷贝到此常量所使用的地方上

替代了`#define`的作用

-   必须位于顶层或者是 [`object` 声明或伴生对象](../高级/Day02-object#对象表达式)的一个成员
-   必须以 `String` 或原生类型值初始化
-   不能有自定义 [getter](Day02-类与对象#Getter 和 Setter)(如果有函数体编译期就不能获取值了)

```kotlin
const val SUBSYSTEM_DEPRECATED: String = "This subsystem is deprecated"
```

### 延迟初始化属性

使用`lateinit`修饰类成员

要求

-   不能是`val`的变量
-   不能是构造器属性
-   没有自定义Getter和Setter
-   可用于局部变量
-   可用于文件级变量
-   可用于类的属性成员

使用反射技术判断有关变量是否已经初始化

```kotlin
class Subject {
    lateinit var subject: String
    val isSubjectInitialized: Boolean
        get() = this::subject.isInitialized // :: 是反射技术
}
```

## 条件

### if 表达式

```kotlin
fun max(a:Int,b:Int): Int = if(a>b) a else b
```

还可以使用块(非常诡异)

```kotlin
fun max(a:Int,b:Int): Int = if(a>b) {
    print("max is a")
    a
} else {
    print("max is b")
    b
}
```

### when

对分支进行顺序比较, 符合条件立即匹配

```kotlin
val x = 2
when (x) {
    Random(0).nextInt() -> print("x == random") // OK
    1 -> print("x == 1")
    2 -> print("x == 2")
    // "X" -> print("x == x") ERROR
    else -> print("x is neither 1 nor 2")
}
```

也有添加大括号的写法

```kotlin
when (x) {
    Random(0).nextInt() -> {
        print("x == random") // OK 
    }
    1 -> {
        print("x == 1")
    }
    2 -> {
        print("x == 2")
    }
    else -> {
        print("x is neither 1 nor 2")
    }
}
```

对于when没有涵盖到的情况, 什么都不会发生

#### 主体

when 也可以不指明主体

```kotlin
when {
    x == 1 && y == 0 -> print("&&")
    x == 1 -> print("x == 1")
    y == 0 -> print("y == 0")
    else -> print("else")
}
```

在when使用存在主体的情况下, when的条件上, 支持使用多种表达式

```kotlin
when (x) {
    1 -> "1"
    2, 3 -> "逗号分割表示或"
    in 1..2 -> "使用in"
    !in 3..4 -> "使用!in"
    is String -> "使用is"
    !is Char -> "使用!is"
    is Int, !is Boolean -> "is 和 , 连用"
    in 5..6, !in 7..8 -> "in 和 , 连用"
}
```

在when的主体上, 可以进行声明语句

```kotlin
when (val a = x + y) {
    1 -> "1"
}
```

#### 返回值

when表达式也有返回值, 在要获取返回值的场景下, when 必须涵盖到所有情况(否则编译异常)

```kotlin
val a = when {
    x == 1 && y == 0 -> "&&"
    x == 1 -> "x == 1"
    y == 0 -> {
        print("x != 1")
        "y == 0" // 块也可以返回值, 非常诡异, 为了不和return冲突...
    }
    else -> ""
}
```

## 循环

### for-in

```kotlin
for (variable[: Type] in Iterable|Iterator){

} 
```

声明的变量variable可以是外面声明过的, 也可以是新的变量

如果是外面声明的变量, 则和外面的变量完全无关, 既不会被外面变量的声明或赋值影响, 也不会影响外面变量的值

```kotlin
val item:String? = null;
for (item:Int in 1..2) {
}
print(item); // null
```

数组index的遍历

```kotlin
for (i in array.indices) {
    print(array[i])
}
```

或者同时使用index和element的遍历

```kotlin
 for ((index, value) in array.withIndex()) {
    println("array[$index] = $value")
 }
```

### while

-   while
-   do-while

### break 和 continue

常规用法一致

### break标签

使用`identifier@`的语法定义标签, 标签指定跳跃的目标

对于break跳到标签, 语法是`break@label`标签必须定义在循环之前, 否则break找不到标签

```kotlin
id@for (i: Int in 1..10) {
    for (j: Int in 1..10) {
        break@id
    }
}
```

在跳出多重循环时有用

### return 标签

使用return跳到标签, 语法同break, 一般用于内连函数, 其他地方用, 即使不会有编译异常, 也不懂是什么机制

```kotlin
listOf(1, 2, 3, 4, 5).forEach(
    fun(item: Int): Unit {
        if (item == 3) {
            return
        }
        print(item)
    }
)
print("done")
```

这种时候, 输出`1245done`

转换成lambda, lambda的语法是`{ 参数列表 -> 函数体 }`

```kotlin
listOf(1, 2, 3, 4, 5).forEach{ item: Int ->
    if (item == 3) {
        return
    }
    print(item)
}
print("done")
```

此时输出`12`, 也就是说, 它直接就是外界函数的返回了! done 也没有执行

如果希望`return`起到**continue**的作用, 此时在lambda块`{`前加上标签

```kotlin
listOf(1, 2, 3, 4, 5).forEach each@{ item: Int ->
    if (item == 3) {
        return@each
    }
    print(item)
}
print("done")
```

此时输出`1245done`

这里可以使用**隐式标签**, 即该标签和forEach同名

```kotlin
listOf(1, 2, 3, 4, 5).forEach{ item: Int ->
    if (item == 3) {
        return@forEach
    }
    print(item)
}
```

如果希望`return`起到**break**的作用,则在外面套一个function

```kotlin
val function = loop@ fun() {
    listOf(1, 2, 3, 4, 5).forEach { item: Int ->
        if (item == 3) {
            return@loop
        }
        print(item)
    }
}
function();
print("done")
```

 输出`12done`

上述写法用于说明原理, 其实可以进一步简写, 详见[函数](Day01-函数)

### 带返回值和标签的return

此时, 跳出到哪个函数, 就表示要返回什么值

```kotlin
val outer = loop@ fun(): String {
    listOf(1, 2, 3, 4, 5).forEach { item: Int ->
        if (item == 3) {
            return@loop "x"
        }
        print(item)
    };
    return "y";
}
val result = outer()
print("done");
println(result);
```

输出`12donex`

## 异常机制

-   throw 异常对象
-   try-catch-finally
-   没有throws
-   没有 try-with-resouces, 因为在有关资源读写的函数内部就已经完成对资源的释放了(无论有无异常)

### precondition抛出异常

类似断言

| Precondition function     | Exception thrown           |
| ------------------------- | -------------------------- |
| `require(condition){msg}` | `IllegalArgumentException` |
| `check(condition){msg}`   | `IllegalStateException`    |
| `error(condition){msg}`   | `IllegalStateException`    |

返回值是`Unit`

一般用法是

```kotlin
require(count >= 0) {
    "Count must be non-negative. You set count to $count."
}
```

也可以在`{}`块里写一些代码

```kotlin
require(count >= 0) {
    println("ERROR!!!!")
    "Count must be non-negative. You set count to $count."
}
```

### 异常类

异常类是继承自`Exception`类的类

下面是其定义的源码

```kotlin
@SinceKotlin("1.1") public actual typealias Exception = java.lang.Exception
```

详见[OOP](Day02-类与对象)

### TODO()

使用TODO函数

```kotlin
fun notImplementedFunction(): Int {
    TODO("I AM LAZY!")
}
```

TODO函数实现源码

```kotlin
public inline fun TODO(reason: String): Nothing = throw NotImplementedError("An operation is not implemented: $reason")
```

