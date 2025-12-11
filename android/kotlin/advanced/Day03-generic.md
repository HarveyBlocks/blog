# 泛型

如果类型参数可以推断出来，例如从构造函数的参数或者从其他途径， 就可以省略类型参数

## 型变

Consumer in , Producer out

### 型变注解 out

为了保证类型安全, 以下操作被禁止

```kotlin
open class Ancestor
open class Parent : Ancestor()
class Child : Parent()
data class Container<T>(var value:T)

fun main() {
    val childContainer: Container<Child> = Container(Child());
    val container: Container<Parent> = childContainer // ERROR
}
```

在Kotlin中的处理方法是, 将泛型参数标记为`out`

```kotlin
data class Container<out T>(val value:T) // 必须是val
```

`out`泛型的有关字段便标记为只读, 同时能让 泛型有继承关系的实例能够assignable

```kotlin
data class Container<out T>(val value: T)

fun main() {
    val childContainer: Container<Child> = Container(Child());
    val parentContainer: Container<Parent> = childContainer // OK
    val value: Parent = parentContainer.value
}
```

 同时`out T` 不能作为标记为`T`的参数

```kotlin
data class Container0<T>(val value: T)
data class Container<out T>(val value: Container0<T>) // Container0<T> ERROR
```

`out`不仅可以标记在泛型声明上, 还可以标记在泛型使用上

```kotlin
data class Container<T>(var value: T) // 仍保留var

fun main() {
    val childContainer: Container<Child> = Container(Child());
    val parentContainer: Container<out Parent> = childContainer // OK
    val value: Parent = parentContainer.value
    parentContainer.value = Parent(); // ERROR 'Parent' 不能赋值给 'Nothing'
}
```

### 逆变注解 in

例如`Comparable`, 使用了`in`

```kotlin
fun interface Comparable<in T> {
    operator fun compareTo(other: T): Int
}
```

```kotlin
val x: Comparable<Number> = Comparable<Number> { n -> 0 }
val y: Comparable<Double> = x // OK
x.compareTo(1.2)
y.compareTo(1.2)
```

泛型的属性变成只可写了

## 使用处`*`投影

`Function <in T, out U>`

-   `Function<*, String>` 表示 `Function<in Nothing, String>`。
-   `Function<Int, *>` 表示 `Function<Int, out Any?>`。
-   `Function<*, *>` 表示 `Function<in Nothing, out Any?>`。

## 泛型函数

```kotlin
fun <T> singletonList(item: T): List<T> {
    // ...
}

fun <T> T.basicToString(): String { // 扩展函数
    // ...
}
```

## 泛型约束-上界

一般的`out T` 表示`T extends Any?`, 规定上界的语法如下

```kotlin
class Foo<out T : Parent> (val t: T)
```

用于提示编译器, t允许使用哪些API

声明的时候, 只有上界, 不能声明下界

对于多个上界, 使用`where`在外声明

```kotlin
fun <T> foo(t:T): Unit
    where T : Serializable,
          T : Comparable<T> {}
```

其中`Any`作为上界能保证传入的泛型不可为空类型

```kotlin
class Box0<T>(val value: T) // nullable
class Box1<T : Any>(val value: T) // not null
class Box2<T : Any?>(val value: T) // nullable
```

## 类型擦除

所有实例类型都会擦除为`ParameterizedClass<*>`

## 类型检测和类型转换

由于类型擦除, 因此不能进行实质上的对泛型参数的类型转换和类型转换

只能对`Type<*>`进行检查

```kotlin
if (something is List<*>) {
    something.forEach { println(it) } // 每一项的类型都是 `Any?`
}
```

编译器会对不涉及泛型参数的部分进行转换, 在这种情况下，会省略`<>`

```kotlin
fun handleStrings(list: MutableList<String>) {
    if (list is ArrayList) {
        // `list` 智能转换为 `ArrayList<String>`
    }
}
```

类型转化`list as ArrayList`也是一样

### 内联函数

内联函数会把泛型参数进行**替换(reified)**, 因此没有进行**擦除**, 泛型也能像类型一样进行检测和转换

```kotlin
inline fun <reified A, reified B> Pair<*, *>.asPairOf(): Pair<A, B>? {
    if (first !is A || second !is B) return null
    return first as A to second as B
}
```

但如果填入内联函数泛型参数的类型, 是被擦除的泛型, 则无法通过编译

```kotlin
val somePair: Pair<Any, Any> = "items" to listOf(1, 2, 3)

val stringToSomething = somePair.asPairOf<String, Any>()
val stringToInt = somePair.asPairOf<String, Int>()
val stringToList = somePair.asPairOf<String, List<*>>()
val stringToStringList = somePair.asPairOf<String, List<String>>() // 编译通过, 但是破坏了类型安全
println(stringToStringList?.second?.forEach() {it.length}) // 抛出异常 ClassCastException 
```

## `_`自行判断泛型

当泛型列表的其他类型已经确定, 这个`_`的位置来自动推断参数的类型

```kotlin
abstract class Executor<T> {
    abstract fun execute(): T
}

class SomeImpl : Executor<String>() {
    override fun execute(): String = "Test"
}
fun <S : Executor<T>, T> run(s:S): T {
     return s.execute();
}

fun main() {
    val s = run<SomeImpl, _>(SomeImpl())
}
```

