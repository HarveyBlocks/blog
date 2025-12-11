# 引用

## 反射

为了减少不使用反射功能的应用程序所需的运行时库大小, Kotlin的反射作为单独的工件 `kotlin-reflect.jar` 

```gradle
dependencies {
    implementation(kotlin("reflect"))
}
```
下面是不安装reflect包的, kotlin有关反射用法

## 类引用

```kotlin
val c: KClass<MyClass> = MyClass::class
println(c.simpleName);
println(c.qualifiedName);
```

## 可调用引用

可调用引用的公共超类型是 `KCallable<R>`

-   函数 R 是返回值类型
-   属性 R 是属性类型
-   构造函数 R 是构造目标类型

### 函数引用

继承自 `KFunction<R>`的子类型, 还有`KFunction<P1,P2,...,R>`, P 是 Parameter, 随函数的实际参数而变

```kotlin
fun function() { }

fun main() {
    val function1: KFunction0<Unit> = ::function
    val function2: () -> Unit = ::function
    function1.invoke()
    function2.invoke()
    function1()
    function2()
}
```

### 属性引用

泛型参数R是变量类型

```kotlin
var count: Int = 0;

fun main() {
    val variableGetter: ()->Int  = ::count
    val variable: KProperty<Int> = ::count
    val variable0: KProperty0<Int> = ::count
    val variableM: KMutableProperty<Int> = ::count
    val variableM0: KMutableProperty0<Int> = ::count
    variable0.get()
    variableM0.set(1)
}
```

### 类型成员引用

第一个参数是都是`this`, 也就是对象本身

```kotlin
class MyClass(var message: String) {
    fun method() {}
}

fun main() {
    val property: KMutableProperty1<MyClass, String> = MyClass::message
    property.get(MyClass(""));
    val method: KFunction1<MyClass, Unit> = MyClass::method
    method.invoke(MyClass(""));
}
```

对于扩展, 以扩展属性为例

```kotlin
val String.lastChar: Char
    get() = this[length - 1]

fun main() {
    println(String::lastChar.get("abc"))
}
```

如果是在类中引用, 无需显式指定 `this` 作为接收者：`this::foo` 与 `::foo` 是等价的

### 实例成员引用

或者直接用实例对象来进行引用, 省略第一个参数, 第一个参数作为Receiver

```kotlin
import kotlin.reflect.KFunction0
import kotlin.reflect.KMutableProperty0

class MyClass(var message: String) {
    fun method() {}
}

fun main() {
    val myClass = MyClass("")
    val property: KMutableProperty0<String> = myClass::message
    property.get();
    val method: KFunction0<Unit> = myClass::method
    method.invoke();
}
```

对于拓展, 以拓展方法为例

```kotlin
fun String.lastChar(): Char = this[length - 1]

fun main() {
    println("abc"::lastChar.invoke())
}
```

### 构造函数引用

```kotlin
class Outer {
    inner class DynamicInner
    class StaticInner
    companion object {

    }
}

fun main() {
    val oc1: () -> Outer = ::Outer
    val oc2: KFunction0<Outer> = ::Outer
    val o = Outer() // 实例化
    val dynamicInner1: KFunction1<Outer, Outer.DynamicInner> = Outer::DynamicInner
    val dynamicInner0: KFunction0<Outer.DynamicInner> = o::DynamicInner
    val staticInner: KFunction0<Outer.StaticInner> = Outer::StaticInner
    // val companion = Outer::Companion ERROR
}
```

