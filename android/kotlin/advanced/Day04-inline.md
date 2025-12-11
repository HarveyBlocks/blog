# 内联

## 内联类

声明一个内联类，在类名前使用 `value` 修饰符

```kotlin
@JvmInline // 特别地, JVM 环境下, 使用@JvmInline注解
value class Password(private val s: String)
```

```kotlin
val password: Password = Password("");
```

内联类必须含有唯一的一个属性在主构造函数中初始化

在运行时， 将使用这个唯一属性来表示内联类的实例

用于对这个属性进行一些优化和增强

```kotlin
@JvmInline
value class Name(private val fullName: String) {
    init {
        require(fullName.isNotEmpty()) {
            "Full name shouldn't be empty"
        }
    }

    constructor(firstName: String, lastName: String) : this("$firstName $lastName") {
        require(lastName.isNotBlank()) {
            "Last name shouldn't be empty"
        }
    }

    val length: Int
        get() = fullName.length

    fun greet() {
        println("Hello, $fullName")
    }
}
```

内联类属性不能有幕后字段

只能有简单的可计算属性（没有 `lateinit` /委托属性）

内联类允许去实现接口

禁止内联类继承类或者被继承

由于装箱拆箱的存在, 因此引用相等对内敛类无意义

inline也可以使用委托

```kotlin
class Derive(base: Base) : Base by base
```

## 内联函数

### 函数类型的消耗

使用函数类型进行传参或返回时, 每一个函数都是一个对象, 并且会捕获一个闭包

内存分配和虚拟调用会增加时间开销

```kotlin
fun advise(msg: String, runnable: (String) -> Unit) {
    println("run before:$msg")
    runnable("advised argument: $msg")
    println("run after:$msg")
}

fun main() {
    val message = "AAA"
    println("advise before")
    advise(message) { msg -> println("running $msg") }
    println("advise after")
}

```

### `inline`

让编译器不对函数进行调用, 而是进行内联, 在外层代码块生成代码

```kotlin
inline fun advise(msg: String, runnable: (String) -> Unit) {
    // ...
}
```

简单来说, `inline`使编译器将函数转变为

```kotlin
fun main() {
    val message = "AAA"
    println("advise before")

    // --- inlined advise(message) { ... } ---
    println("run before:" + message)
    // inline lambda 被拆开
    println("running " + "advised argument: " + message)
    println("run after:" + message)
    // ----------------------------------------

    println("advise after")
}
```

内联使编译后代码增加, 应该**避免内联过大函数**

内联只能对**有函数类型做参数**的函数才有比较好的优化

内联的lambda 表达式**被拆开**, 因此**只能**在内联函数内部**调用**或者**作为可内联的参数传递,** 而不能作为一个一般的变量传递

### `noinline`

如果一个inline的函数, 希望其的函数类型的参数能被作为变量赋值给一个变量或字段, 就在这个参数上, 修饰`noinline`

```kotlin
inline fun advise(
    msg: String, inlineRunnable: (String) -> Unit, noinline noinlineRunnable: (String) -> Unit
) {
    println("run before:$msg")
    inlineRunnable("advised inline function argument: $msg")
    noinlineRunnable("advised noinline function argument: $msg")
    println("run after:$msg")
}

fun main() {
    val message = "AAA"
    println("advise before")
    advise(
        msg = message,
        inlineRunnable = { msg -> println("running $msg with inline") },
        noinlineRunnable = { msg -> println("running $msg with noinline") })
    println("advise after")
}

```

编译器做的工作就是

```kotlin
fun main() {
    val message = "AAA"
    println("advise before")

    // ----------- inlined advise(...) ------------
    println("run before:" + message)

    // inline lambda 被拆开
    println("running " + ("advised inline function argument: " + message) + " with inline")

    // noinline lambda 保持一个函数类型
    val tmpNoinline: (String) -> Unit = { msg -> println("running $msg with noinline") }
    tmpNoinline("advised noinline function argument: " + message)

    println("run after:" + message)
    // --------------------------------------------

    println("advise after")
}
```

`noinline`就能作为传递给一般的变量, 而`inline`不行

```kotlin
typealias Runnable = () -> Unit

inline fun advise(inlineRunnable: Runnable, noinline noinlineRunnable: Runnable) {
    val inner1: Runnable = inlineRunnable // ERROR
    val inner2: Runnable = noinlineRunnable // OK
}
```

### `crossinline`

如果函数类型的参数是需要调用展开的, 且不需要作为noinline进行传递, 但是, 如果这个参数是在另一个闭包中被调用, 那么也是不允许`inline`的展开的

```kotlin
fun noinlineFunction(runnable: () -> Unit) {
    println("in pre")
    runnable()
    println("in post")
}

inline fun inlineFunction(runnable: () -> Unit) {
    println("out pre")
    noinlineFunction {
        println("execute pre")
        runnable() // ERROR
        println("execute post")
    }
    println("out post")
}

fun main() {
    inlineFunction {
        print("run")
    }
}
```

使用`crossinline`, 允许这种展开

```kotlin
inline fun inlineFunction(crossinline runnable: () -> Unit) {
    println("out pre")
    noinlineFunction {
        println("execute pre")
        runnable() // OK
        println("execute post")
    }
    println("out post")
}
```

最终编译器会进行下面的操作

```kotlin
fun main() {
    // --- inlineFunction 展开 ---
    println("out pre")
    // --- noinlineFunction 不展开 ---
    noinlineFunction {
        println("execute pre")
        // --- lambda 展开 ---
        print("run")
        // ------------
        println("execute post")
    }
    // ------------
    println("out post")
    // ------------
}
```

### return

在 lambda 表达式内部禁止使用不标注的 `return`，因为 lambda 表达式不能使包含它的函数返回

但是如果lambda传入的函数是inline的, 则return也可以inline

```kotlin
inline fun inlined(block: () -> Unit) {
    block()
}

fun main() {
    inlined {
        print("A")
        return // OK, 直接结束main函数
    }
    println("B") // 不会被运行
}
```

使用标签

```kotlin
inline fun inlined(flag: Boolean, block: (Boolean) -> Unit) {
    block(flag)
}

fun main() {
    inlined(true) lambda_label@{ useLabel ->
        if (useLabel) {
            println("true")
            return
        } else {
            println("false")
            return@lambda_label
        }
    }
    println("out") // true 时 不会被运行, false 时会运行
}
```

省略显式标签, 使用隐式标签

```kotlin
fun main() {
    inlined(false) { useLabel ->
        if (useLabel) {
            println("true")
            return
        } else {
            println("false")
            return@inlined
        }
    }
    println("out") // true 时 不会被运行, false 时会运行
}
```

当两个return都要有返回值时

```kotlin
inline fun inlined(flag: Boolean, block: (Boolean) -> String) {
    val message = block(flag)
    println(message)
}

fun foo(): Int {
    inlined(false) { useLabel ->
        if (useLabel) {
            println("true")
            return 1 // foo 的返回值
        } else {
            println("false")
            return@inlined "message" // lambda 的返回值
        }
    }
    return 0  // foo 的返回值
}
```

在`crossinline` 和 `noinline`的情况下, return也都是不允许的

### 泛型参数具体化

>   reified 具体化

内联支持参数进行匹配, 也支持泛型类型进行匹配

```kotlin
inline fun <reified T> Any?.cast(): T {
    if (this is T) {
        return this as T
    }
    val objType = if (this === null) "null" else this::class.qualifiedName
    val targetType = T::class.qualifiedName
    throw TypeCastException("cast from $objType to $targetType is not allowed")
}

fun main() {
    val double1: Double = (1.0 as Number).cast() // OK
    println(double1)
    val double2: Double = (1 as Number).cast() // ERROR
    println(double2)
}

```

在调用函数的时候, 泛型参数被具体化

但是由于泛型擦除, 同时也会存在无法被检测的运行时异常

```kotlin
class Container<T>(val data: T)

fun main() {
    val str: Container<String> = Container("message")
    val number: Container<Number> = str.cast();
    println("successful! ${number.data}") // successful! message
    val data = number.data // ERROR HERE
    // class java.lang.String cannot be cast to class java.lang.Number
    println("still successful! $data") // 无法被运行到此处了
}

```

## 内联属性

`inline` 可用于修饰**没有幕后字段**的属性的Getter/Setter

```kotlin
val foo: Foo
    inline get() = Foo()

var bar: Bar
    get() = ...
    inline set(v) { ... }
```

直接修饰在属性上，Getter/Setter都将`inline`

```kotlin
inline var bar: Bar
    get() = ...
    set(v) { ... }
```

在调用处，内联Getter/Setter如同内联函数一样内联

## 访问控制

在`public inline`或`protected inline` 里面调用有关`private`/`internal`声明是不允许的

```kotlin
private fun privateFunc(x: Int): Int = x * 2

inline fun publicInlineFunc(x: Int): Int {
    return privateFunc(x) // ERROR privateFunc
}
```

应当将小范围的访问修饰符改为`internal`并加上注释`@PublicedApi`

```kotlin
@PublishedApi 
internal fun privateFunc(x: Int): Int = x * 2

inline fun publicInlineFunc(x: Int): Int {
    return privateFunc(x) // ERROR privateFunc
}
```

保留`internal`而不是`public`保证了在另一模块的kotlin代码中无法直接调用`privateFunc`

使用`PublisedApi`注解, 让字节码解释器能够调用已经`inline`的函数`privateFunc`

