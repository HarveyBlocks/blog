# 函数

## 声明

使用`fun`关键字声明

```kotlin
fun double(x: Int): Int {
    return 2 * x
}
```

使用常规方法调用

```kotlin
val result = double(2)
```

成员函数使用`.`调用

```kotlin
Stream().read() // 创建类 Stream 实例并调用 read()
```

### 必选参数

使用 Pascal 表示法定义——*name*: *type*

参数用逗号隔开， 每个参数必须有显式类型

允许尾部逗号

```kotlin
fun powerOf(
    number: Int,
    exponent: Int, // 尾部逗号
) { /*……*/ }
```

### 具名实参



```kotlin
fun foo(
    arg1: Int,
    arg2: Int,
    arg3: Int,
    arg4: Int,
) { /*……*/
}
```

在填入实参数值的时候, 有时为了增加可读性, 就把形参名写出来

```kotlin
foo(1, 2, 3, 4)
foo(1, arg2 = 2, 3, 4)
foo(1, arg2 = 2, 3, arg4 = 4)
```

具名实参可以调换顺序而不顾函数声明的参数顺序, 但调换位置的具名实参, 之后的所有参数全部必须是具名实参

```kotlin
foo(1, arg4 = 2, 3, arg2 = 4)// ERROR
foo(1, arg4 = 2, arg3 = 3, arg2 = 4)// OK
foo(1, arg3 = 2, arg2 = 3,  4)// ERROR
```

调用Java代码的时候, 不能使用具名实参, 因为Java的字节码不保存函数参数名称

### 默认参数

使用在参数后加`=`, 为参数添加默认值, 默认值允许复杂的表达式

```kotlin
fun shuffle(
    array: ByteArray,
    off: Int = 0,
    len: Int = array.size,
    seed: Int = Random(System.currentTimeMillis()).nextInt()
) { /*……*/
}
```

不应该出现使编译器难以判断参数匹配的情况

```kotlin
shuffle(ByteArray(1), len = 1, 1); // ERROR, 鉴于具名参数可以改变顺序, 后面的1不知给off还是seed
shuffle(ByteArray(1), len = 1, seed = 1) // OK
shuffle(ByteArray(1), 1, len = 1) // OK
shuffle(ByteArray(1), 1, len = 1, 1) // OK
```

如果默认参数在必选参数之前, 则此默认参数后的必选参数必须以具名参数的形式传参

```kotlin
fun foo(
    arg1: Int,
    arg2: Int = 0,
    arg3: Int,
) { /*……*/
}
```

```kotlin
foo(1, arg3 = 3)
foo(arg1 = 1, arg3 = 3) // OK
foo(arg3 = 3, arg1 = 1) // OK
foo(1, arg2 = 2, arg3 = 3) // 覆盖默认
foo(1, arg3 = 3, arg2 = 3) // 覆盖默认
```



子类override父类的方法, 父类的方法的参数有默认参数的时候, 子类的对应参数上不能给默认值

调用子类的该方法的时候, 即使不在子类定义默认参数, 也使用父类的默认参数

```kotlin
open class A {
    open fun foo(i: Int = 10) { /*……*/
    }
}

class B : A() {
    override fun foo(i: Int) { /*……*/
    }  // 不能有默认值
}

fun main() {
    val b: B = B();
    b.foo();
}
```



### 不定参数

函数的参数的最后一个, 使用`vararg`修饰, 表示不定参数

```kotlin
fun shuffle(
    len: Int = array.size, // 居然能前向引用....
    vararg array: Int
) { /*……*/
}
```

但这种不定参数和默认参数结合的情况, 默认参数将会失效, 必须手动指定默认参数的参数值了

```kotlin
shuffle(1, 1, 2, 3, 4) // OK
shuffle(1, 2, 3, 4) // OK
shuffle(1, *intArrayOf(1, 2, 3, 4)) // 使用展开运算符, OK
shuffle(1, *intArrayOf(1, 2, 3, 4), 5, 6, 7) // OK
shuffle(1, intArrayOf(1, 2, 3, 4)) // ERROR, Java允许传入数组的, kotlin不允许了, 必须使用具名实参
shuffle(1, array = intArrayOf(1, 2, 3, 4)) // OK
```

默认参数也可以在不定参数之后声明, 但可读性不友好....

### lambda参数

在参数列表的**最后一个参数**是Lambda表达式时, 那么可以在括号外传入

```kotlin
fun foo(
    callback: (name: String) -> Unit,
) { /*……*/
}
```

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

### 单表达式函数

当函数体由单个表达式构成时，可以省略`{}`并且在 `=` 符号之后指定代码

```kotlin
fun double(x: Int): Int = x * 2
```

对于单表达式函数的返回值类型可由编译器推断时，显式声明返回类型是可选的

```kotlin
open class A {
    fun af() {}
}

class B : A() {

    fun bf() {}
}

fun create(): A = B(); // 手动指明返回值类型来进行封装
fun create() = B();
```

```kotlin
createA().af();// OK
createA().bf();// ERROR
createB().bf();// OK
createB().bf();// OK
```

### 返回值

-   无返回值类型标记的, 指的都是返回Unit, 返回Unit
-   能获取Unit的对象, 也就是说不会编译异常, 但是也什么都做不到
-   有返回值的, 应当进行返回值类型标记
-   Kotlin 不推断具有块代码体的函数的返回类型



## 泛型函数

通过在函数名前使用尖括号指定, 详见[泛型](../高级/Day03-generic)

```kotlin
fun <T> singletonList(item: T): List<T> { /*……*/ }
```





## 作用域

Kotlin支持以下函数声明位置

-   顶层函数(文件级函数)
-   局部函数
-   成员函数
-   扩展函数

局部函数可以访问外部函数(闭包)的局部变量

特别的, 局部函数内也支持扩展函数

```kotlin
fun main() {
    fun inner(s: String, i: Int): Char = s[s.length - 1 - i]
    fun String.inner(i: Int): Char = this[this.length - 1 - i]
    println(inner("abc", 3))
    println("abc".inner(3))
}
```

## 重载

支持重载, 但是不一定有默认参数方便

```kotlin
fun a(x: Int= 3) {}

fun a(x: String = "3") {}
```

此时两个默认参数都失效了
