# 函数高级

## 中缀表达法

其实表现的结果就是"自定义运算符"一样, 非常牛逼

### 声明

使用`infix`修饰的函数可以使用中缀表达法, 也可以使用传统的调用函数的方式

-   中缀函数必须是成员函数或者[扩展函数](./Day02-extension)
-   必须只有一个参数
-   参数不能有默认值, 不能是不定参数

```kotlin
infix fun Int.shl(x: Int): Int { ... }

// 用中缀表示法
1 shl 2

// 等价
1.shl(2)
```

### 优先级

-   低于算数运算符, 类型转换, rangeTo操作符
-   高于逻辑运算符, is/in检测

## 尾递归

对使用尾递归编程风格的函数, 使用`tailrec`修饰函数, Kotlin编译器会优化递归, 留下一个基于循环的版本

```kotlin
val eps = 1E-10 // 误差

tailrec fun findFixPoint(x: Double = 1.0): Double =
    if (Math.abs(x - Math.cos(x)) < eps) x else findFixPoint(Math.cos(x))
```

优化后

```kotlin
val eps = 1E-10 // "good enough", could be 10^-15

private fun findFixPoint(): Double {
    var x = 1.0
    while (true) {
        val y = Math.cos(x)
        if (Math.abs(x - y) < eps) return x
        x = Math.cos(x)
    }
}
```

下列情况, 无法对递归进行优化

-   在递归调用后有更多代码
-   在 try-catch-finally 块中的递归
-   open 的函数

Q: 如果是嵌套了一个过渡函数, 造成了实质上的递归, 会有优化吗?

## 重载运算符

操作符具有预定义的符号表示与优先级

重载类型需要为相应的类型提供一个指定名称的**成员函数**或**扩展函数**

### 前缀运算符

| 表达式 | 翻译为           |
| ------ | ---------------- |
| `+a`   | `a.unaryPlus()`  |
| `-a`   | `a.unaryMinus()` |
| `!a`   | `a.not()`        |

```kotlin
data class Point(val x: Int, val y: Int)

operator fun Point.unaryMinus() = Point(-x, -y)
```

### incr/decr

| 表达式 | 翻译为    |
| ------ | --------- |
| `a++`  | `a.inc()` |
| `a--`  | `a.dec()` |

`inc()` 和 `dec()` 返回一个值，用于赋值给原变量

不应该在`inc()` 和 `dec()` 的函数体内修改原变量的值

### 算数运算符

| 表达式  | 翻译为            |
| ------- | ----------------- |
| `a + b` | `a.plus(b)`       |
| `a - b` | `a.minus(b)`      |
| `a * b` | `a.times(b)`      |
| `a / b` | `a.div(b)`        |
| `a % b` | `a.rem(b)`        |
| `a..b`  | `a.rangeTo(b)`    |
| `a..<b` | `a.rangeUntil(b)` |

### in

| 表达式    | 翻译为           |
| --------- | ---------------- |
| `a in b`  | `b.contains(a)`  |
| `a !in b` | `!b.contains(a)` |

### 索引访问操作符

| 表达式                 | 翻译为                    |
| ---------------------- | ------------------------- |
| `a[i]`                 | `a.get(i)`                |
| `a[i, j]`              | `a.get(i, j)`             |
| `a[i_1, ..., i_n]`     | `a.get(i_1, ..., i_n)`    |
| `a[i] = b`             | `a.set(i, b)`             |
| `a[i, j] = b`          | `a.set(i, j, b)`          |
| `a[i_1, ..., i_n] = b` | `a.set(i_1, ..., i_n, b)` |

### invoke 操作符

| 表达式             | 翻译为                   |
| ------------------ | ------------------------ |
| `a()`              | `a.invoke()`             |
| `a(i)`             | `a.invoke(i)`            |
| `a(i, j)`          | `a.invoke(i, j)`         |
| `a(i_1, ..., i_n)` | `a.invoke(i_1,..., i_n)` |

### 相等

相等判断并不使用operator重载, 而是对`Any#equals`进行重载

| 表达式   | 翻译为                            |
| -------- | --------------------------------- |
| `a == b` | `a?.equals(b) ?: (b === null)`    |
| `a != b` | `!(a?.equals(b) ?: (b === null))` |

`===` 和 `!==`不可重载

### 赋值

| 表达式   | 翻译为             |
| -------- | ------------------ |
| `a += b` | `a.plusAssign(b)`  |
| `a -= b` | `a.minusAssign(b)` |
| `a *= b` | `a.timesAssign(b)` |
| `a /= b` | `a.divAssign(b)`   |
| `a %= b` | `a.remAssign(b)`   |

-   没有单独的assign用于重载, Kotlin的赋值表达式也没有返回值
-   如果没有特别重载上面的赋值运算符
    1.  尝试将表达式解析成`a = a + b`之类后再次编译
    2.  `a + b` 的返回值应当是`a`  的类型的子类

-   否则

    -   重载的函数应当返回`Unit`

    -   注意到, 这里的assign实质上是不会改变变量引用的, 改变的其实对象内部的属性

        因此, 当变量为**var**的时候, 同时定义了`plusAssign`和**返回值为`assignable to A`的`plus`的类**, 将会产生冲突

        ```kotlin
        open class A {
            operator fun plus(other: A): A = A()  // returns assignable 
            operator fun plusAssign(other: A) { /* 对象内部改变 */ }
        }

        fun main() {
            val a: A = A()
            a += A()  // ERROR: 两个运算符之间存在冲突
        }
        ```

         如果not assignable, 就不会报错

        ```kotlin
        open class A<T> {
            operator fun plus(other: A<T>): A<Int> = A<Int>()  // returns not assignable 
            operator fun plusAssign(other: A<T>) { /* mutate */
            }
        }

        fun main() {
            var a: A<String> = A() 
            a += A()  // OK A<Int> not assignable to A<String>
        }
        ```

### 比较操作符

| 表达式   | 翻译为                |
| -------- | --------------------- |
| `a > b`  | `a.compareTo(b) > 0`  |
| `a < b`  | `a.compareTo(b) < 0`  |
| `a >= b` | `a.compareTo(b) >= 0` |
| `a <= b` | `a.compareTo(b) <= 0` |

`compareTo` 需要返回 `Int` 值

## 函数类型

### 语法

函数类型的参数列表里的参数名可以省略, 如果有的话, **返回类型不可忽略**, 可以使用Unit

```kotlin
fun function(argA: Int, argB: String): String {
    return "";
}

fun main() {
    val functionVal1: (arg1: Int, arg2: String) -> String = ::function // 使用引用
    val functionVal2: (Int, String) -> String = ::function // 使用引用
}
```

返回值类型Unit不可忽略

如果要表示函数类型可空, 可用`((Int)->Unit)?`表示

箭头表示法是右结合的, `(Int) -> (Int) -> Unit`等价于`(Int) -> ((Int) -> Unit)`

### 默认参数和不定参数的情况

有关默认参数

```kotlin
fun function(argA: Int, argB: String=""): String {
    return "";
}

fun main() {
    val functionVal1: (Int) -> String = ::function 
    val functionVal2: (Int, String) -> String = ::function 
}
```

有关不定参数

```kotlin
fun function(argA: Int,vararg argB: String): String {
    return "";
}

fun main() {
    val functionVal1: (Int) -> String = ::function
    val functionVal2: (Int, String) -> String = ::function
    val functionVal3: (Int, String, String) -> String = ::function
    val functionVal4: (Int, String, String,String) -> String = ::function
}
```

### Receiver

使用例如`A.(B)->C`的语法, 声明带Reciver的函数类型, 这种类型的函数中, 可以会将调用者传给`this`

```kotlin
typealias Reserve = String.(Int) -> Char; // 使用Receiver表达

fun reserve1(s: String, i: Int): Char { // Receiver作为第一个参数
    return s[s.length - 1 - i];
}

fun String.reserve2(i: Int): Char { // Receiver填入this
    return this[this.length - 1 - i];
}

fun main() {
    val f1: Reserve = ::reserve1
    val f2: Reserve = String::reserve2
    val c1 = "str".f1(1)
    val c2 = f2("str",1) // 同时支持两种调用方式
}
```

使用Receiver的函数类型可以和Reciver作为第一个参数的函数类型互相转化

```kotlin
fun function(s: String, i: Int): String = ""

val f1: (String, Int) -> String = ::function
val f2: String.(Int) -> String = ::function
val f3: String.(Int) -> String = f1 // arg1->Receiver
val f4: (String, Int) -> String = f2 // Receiver->arg1
```

### 实例化

-   使用Lambda

    ```kotlin
    val f1: String.(Int) -> String = { i -> this }
    val f2: (String, Int) -> String = { s, i -> "" }
    ```

    对于lambda来说, Reciver和arg1是有区别的

-   匿名函数

    ```kotlin
    val f1: String.(Int) -> String = fun(s: String, i: Int): String = s
    val f2: (String, Int) -> String = fun String.(i: Int): String = this
    ```

-   顶层、局部、成员、扩展**函数的引用**

    ```kotlin
    // function
    fun getLastChar(s: String, i: Int): Char = s[s.length - 1 - i]
    val function: (String, Int) -> Char = ::getLastChar // 顶层函数的引用

    // method
    class MyClass(val msg: String = "") {
        fun method(i: Int): Char = this.msg[this.msg.length - 1 - i]
    }
    val method: (MyClass, Int) -> Char = MyClass::method  // 类成员函数的引用

    // extention
    fun String.last(i: Int): Char = this[length - 1 - i]
    val extension1: (String, Int) -> Char = String::last // 扩展函数的引用
    val extension2: (Int) -> Char = "abc"::last // 实例对象的扩展函数的引用
    ```

    局部函数的引用

    ```kotlin
    fun main() {
        fun inner(s: String, i: Int): Char = s[s.length - 1 - i]
        fun String.inner(i: Int): Char = this[this.length - 1 - i]
        val innerFunction1: (String, Int) -> Char = ::inner
        val innerFunction2: String.(Int) -> Char = String::inner
        val innerFunction3: (Int) -> Char = "abc"::inner
    }
    ```

-   顶层, 成员, 扩展**属性的引用**

    ```kotlin
    val lastChar: Char = '0'
    // val value: () -> Char = ::lastChar // 顶层属性的引用

    class MyClass(val msg: String = "")

    val props1: MyClass.() -> String = MyClass::msg // 类的成员引用
    val props2: (MyClass) -> String = MyClass::msg // 类的成员引用

    val String.lastChar
        get() = this[this.length - 1]
    val extensionVal: String.() -> Char = String::lastChar // 扩展属性的引用
    ```

-   特定实例对象的成员引用

    ```kotlin
    class MyClass(val msg: String = "") {
        fun method(i: Int): Char = this.msg[this.msg.length - 1 - i]
    }
    val obj:MyClass = MyClass("message: abc")
    val method: (Int) -> Char = obj::method  // 实例对象成员函数的引用
    val props: () -> String = obj::msg  // 实例对象的成员属性引用
    ```

-   **构造器的引用**

    ```kotlin
    class Outer {
        inner class DynamicInner
        class StaticInner
    }

    val outerConstructor: () -> Outer = ::Outer // 一般的类型
    val outer: Outer = Outer()
    val outerDynamicInnerConstructor1: (Outer) -> Outer.DynamicInner = Outer::DynamicInner // 内部类
    val outerDynamicInnerConstructor2: () -> Outer.DynamicInner = outer::DynamicInner // 特定对象的内部类
    val outerStaticInnerConstructor: () -> Outer.StaticInner = Outer::StaticInner // 静态内部类
    ```

-   **实现**函数类型接口

    ==没错, 在kotlin中允许实现函数类型==

    ```kotlin
    class IntTransformer: (Int) -> Int {
        override operator fun invoke(x: Int): Int = -x
    }

    val intFunction: (Int) -> Int = IntTransformer()
    ```

### 调用

函数类型的逻辑可以通过`invoke()`显式调用, 同时`invoke()`是对函数调用运算符`()`的重载, 也可以直接使用`()`运算符调用

```kotlin
fun getLastChar(s: String, i: Int): Char = s[s.length - 1 - i]

fun main() {
    val f: (String, Int) -> Char = ::getLastChar
    println(f("abc", 2))
    println(f.invoke("abc", 2))
}
```

如果是带用Receiver的函数类型调用时, 第一个参数可以以Receiver的形式调用, Receiver也可以放在第一个参数传参

同时, 将Receiver作为第一个参数的函数类型则只能中规中矩地调用

```kotlin
fun getLastChar(s: String, i: Int): Char = s[s.length - 1 - i]

fun main() {
    val f1: (String, Int) -> Char = ::getLastChar
    println(f1("abc", 2))
    println("abc".f1(2)) // ERROR
    val f2: String.(Int) -> Char = ::getLastChar
    println("abc".f2(2))
    println(f2("abc", 2))
}
```

## 匿名函数

所谓**函数字面值的代码块**即**匿名函数**和**lambda**表达式

匿名函数的返回值推测和具名函数一致: 函数体下Unit可以省略, 否则不能省略; 表达式下可以依靠编译器推测, 也可以自己指明

匿名函数表达式的类型是函数引用

```kotlin
// 使用表达式主体
val reserver1: (String, Int) -> Char = fun(s: String, i: Int): Char = s[s.length - 1 - i]
// 使用函数体主体
val reserver2: (String, Int) -> Char = fun(s: String, i: Int): Char {
    return s[s.length - 1 - i]
}
// 使用扩展
val reserver3: String.(Int) -> Char = fun String.(i: Int): Char = this[this.length - 1 - i]
```

## lambda表达式

### 语法

lambda表达式总是在一个`{}`中

```kotlin
{参数列表->函数体}
```

Lambda表达式的类型的一个函数引用

Lambda表达式的返回值, 由表达式推测. 在赋值的左边有返回值类型的限制时, 返回值类型则由赋值的左边决定

参数列表的各个参数可以给类型, 可以给一部分参数类型, 也可以不给类型, 但是赋值的左边一定要有响应的类型

```kotlin
// 返回值的表示由赋值的左边决定
val sum1: (Int, Int) -> Int = { x, y -> x + y }
val sum2: (Int, Int) -> Unit = { x, y -> x + y }

// 自动推测sum3的类型是`(Int,Int)->Int`
val sum3 = { x: Int, y: Int -> x + y }

// 赋值的左边足够完整, 右边则可以随意添加类型, 仅作为提示
val sum4: (Int, Int) -> Int = { x: Int, y -> x + y }  // 只给出一部分
val sum5: (Int, Int) -> Int = { x, y: Int -> x + y }  // 只给出一部分
val sum6: (Int, Int) -> Int = { x: Int, y: Int -> x + y }  // 完整给出
val sum7: (Int, Int) -> Int = { x, y -> x + y } // 都不给出

// Producer out, Consumer in 原则
// 可以将参数设定为赋值左边的upper, 返回值设定为赋值左边的lower
val sum8: (Int, Int) -> Number = { x: Number, y: Number -> 1 }
```

### 拖尾 Lambda 表达式

参数末尾的Lambda表达式可以移出参数列表

例如

```kotlin
foo({ name -> println("hello $name") }) // 最原始的, 传入lambda表达式作为参数
foo(callback = { name -> println("hello $name") }) // 可以作为具名参数传入
foo() { name -> println("hello $name") } // lambda 表达式移出参数列表外
foo { name -> println("hello $name") } // 参数列表是无参的情况, 直接省略()
```

有不定参数的情况也能用

```kotlin
fun shuffle(
    vararg array: Int,
    callback: () -> Unit
) { /*……*/
}
```

```kotlin
shuffle(1, 1, 2, 3, 4) { print("") }
```

### it 标识符

当Lambda表达式只有一个参数的时候, 则可以省略参数列表和`->`的部分,只保留函数体, 而 it 标识符将指代这个参数

```kotlin
val positivePredict: (Int) -> Boolean = { it > 0 }
val negativePredict: (Int) -> Boolean = { it < 0 }
val zeroPredict: (Int) -> Boolean = { it == 0 }
```

### `_` ignore parameters

直接省略是不允许的, 类型不匹配

```kotlin
val lambda: (Int, Int) -> Int = { x -> 0 } // ERROR
```

使用`_`

```kotlin
val lambda1: (Int, Int) -> Int = { x, y -> 0 }
val lambda2: (Int, Int) -> Int = { _, y -> 0 }
val lambda3: (Int, Int) -> Int = { x, _ -> 0 }
val lambda4: (Int, Int) -> Int = { _, _ -> 0 }
```

### lambda参数的解构

```kotlin
{ a -> ... } // 一个参数
{ a, b -> ... } // 两个参数
{ (a, b) -> ... } // 一个解构对
{ (a, b), c  -> ... } // 一个解构对以及其他参数
```

区分两个参数和解构对的区别

解构对的类型可以一起指定, 也可以分别指定

```kotlin
map.mapValues { (key, value): Map.Entry<Int, String> -> "$value!" }

map.mapValues { (key, value: String) -> "$value!" }
```

### 从 lambda 返回

lambda和if 表达式一样, 最后一行表示表达式返回值

```kotlin
fun function(): Int {
    val predicate: (Int) -> Boolean = { it: Int ->
        val result = it > 0
        print("---")
        result
    }
    return 1
}
```

在lambda中使用`return@label`的形式显式返回lambda的值

```kotlin
val function: (Int) -> Int = lambda_label@{ it: Int ->
    val result = it > 0
    if (result) {
        return@lambda_label 1
    }
    0
}
```

如果这个lambda表达式作为参数传递, 则对应函数名, 可以直接作为label

```kotlin
fun myFunction(str: String, callback: (Int) -> Int) {
    // ...
}

fun main() {
    myFunction("abc", lambda_label@{ it: Int ->
        val result = it > 0
        if (result) {
            return@lambda_label 1
        }
        0
    })// 自定义label
    myFunction("abc", { it: Int ->
        val result = it > 0
        if (result) {
            return@myFunction 1
        }
        0
    }) // 直接以函数名作为label
}
```

### LINQ 风格

>   Language Integrated Query

一种链式调用的代码风格

```kotlin
itemList.filter { it.score > 5 }.sortedBy { it.id }.map { it.name.uppercase() }
```

## 匿名函数与lambda表达式的区别

-   返回值类型
    -   匿名函数可以使用编译器推测, 也可以自己指明
    -   Lambda表达式不能自己指明
-   移出括号的简写
    -   匿名函数作为参数传递时，必须放在括号内
    -   允许将函数留在圆括号外的简写语法仅适用于 lambda 表达式
-   return
    -   lambda 表达式中的 `return` 将从**包含它的函数返回**
    -   匿名函数中的 `return` 将从**匿名函数自身返回**

## 闭包

允许在函数内部声明局部的`变量/函数/扩展/类/匿名函数/对象表达式/Lambda表达式`

这些在函数内部声明的`变量/函数/扩展/类/匿名函数/对象表达式/Lambda表达式`可以互相访问, 但在函数外界不可见, 此谓之闭包

也就是说, 可以用闭包将函数内的`变量/函数/扩展/类/匿名函数/对象表达式/Lambda表达式`带出函数外, 而函数外不可访问

```kotlin
typealias Counter = () -> Int

fun counter(initializer: Int = 0, step: Int = 1): Counter {
    var count = initializer;
    fun add(): Int {
        val before = count
        count += step
        return before
    }
    return ::add
}

fun main() {
    val oddCounter: Counter = counter(1, 2)//奇数计数器
    // 无法访问到count变量, 只能无情地进行add这一个操作
    println(oddCounter()) // 1
    println(oddCounter()) // 3
    println(oddCounter()) // 5
    println(oddCounter()) // 7
    println(oddCounter()) // 9
    println(oddCounter()) // 11
}
```

很难想象, 编译器为了做到这一点, 要做多少工作.....必须把count从值变成引用, 或者将函数add从栈移到堆中, 在堆对象中存储count这一值, 而且要避免count被回收, 所以会有什么弱引用巴拉巴拉

Java就是非常简单的匿名内部类, 就是一个对象, 就很好理解, 把count放入这个对象的存储空间了

但与Java不同, Kotlin的闭包内**允许发生值的改变**, 且会**对外界的值产生影响**

```kotlin
fun main() {
    var count = 0;
    fun add() = count++
    add() // 0
    add() // 1
    add() // 2
    add() // 3
    add() // 4
    add() // 5
    println(count) // 6 产生了影响
}
```

## 标准函数

使用lambda等语法糖, 使代码更简洁

-   with 
-   apply
-   run

例如

```kotlin
val list = listOf("Apple", "Banana", "Orange", "Pear", "Grape") 
val builder = StringBuilder() 
builder.append("Start eating fruits.\n") 
for (fruit in list) { 
    builder.append(fruit).append("\n") 
} 
builder.append("Ate all fruits.") 
val result = builder.toString() 
println(result) 
```

builder 反复调用多次

使用with, 将他们聚合在一起

```kotlin
val list = listOf("Apple", "Banana", "Orange", "Pear", "Grape") 
val result = with(StringBuilder()) { 
    // 此时的上下文this, 是StringBuilder, 借助省略this, 来简化代码
    append("Start eating fruits.\n") 
    for (fruit in list) { 
        append(fruit).append("\n") 
    } 
    append("Ate all fruits.") 
    toString()  // 返回值
} 
println(result)
```

使用run完成相同的效果

```kotlin
val list = listOf("Apple", "Banana", "Orange", "Pear", "Grape") 
val result = StringBuilder().run { 
    // 此时的上下文this, 是StringBuilder, 借助省略this, 来简化代码
    append("Start eating fruits.\n") 
    for (fruit in list) { 
        append(fruit).append("\n") 
    } 
    append("Ate all fruits.") 
    toString()  // 返回值
} 
println(result)
```

使用apply, apply和run的区别在于, apply返回调用的对象, 而回调函数没有返回值

```kotlin
val list = listOf("Apple", "Banana", "Orange", "Pear", "Grape") 
val builder = StringBuilder().run { 
    // 此时的上下文this, 是StringBuilder, 借助省略this, 来简化代码
    append("Start eating fruits.\n") 
    for (fruit in list) { 
        append(fruit).append("\n") 
    } 
    append("Ate all fruits.") 
} 
println(builder.toString())
```

