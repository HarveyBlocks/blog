# object

## 对象声明

### 文件级对象声明

全是静态成员的类

成员也允许使用修饰符

声明一个`object`

```kotlin
object MyMath {
    private val PI: Double = 3.14
    fun add(a: Int, b: Int): Int = a + b;
}
```

直接用类型调用

```kotlin
println(MyMath.add(1, 2))
```

对象声明可以进行继承, 但不能有子类

```kotlin
interface Interface {
    fun method()
}

object DefaultInterface : Interface {
    override fun method() {
        print("?");
    }
}

fun main() {
    DefaultInterface.method()
}
```

对象声明不能在局部作用域

### 数据类对象

object对象也继承自Any类, 也有hashCode, equals, toString方法

可以使用`data`修饰`object`声明, 将有关方法重写

-   不能为 `data object` 提供自定义的 `equals` 或 `hashCode` 实现
-   `toString()` 返回`对象全类名@hashCode`=>重载后返回数据对象的Simplified名称
-   `equals()` / `hashCode()`  使其能应用于有关集合
-   没有`copy`
-   没有 `componentN()`

### 类成员的对象声明

对象声明可以作为类成员, 和类没有编译上的强关系, 仅仅作为逻辑上对有关对象的整理编排

类内的成员不可以访问对象声明里的私有成员

```kotlin
class MyClass private constructor() {
    object Factory {
       fun create(): MyClass = MyClass()
    }
}

fun main() {
    MyClass.Factory.create()
}
```

## 伴生对象

kotlin删除了静态成员, 因为静态成员不属于对象, 而是属于类, 不符合面向对象的思想

伴生对象用于

-   单例
-   工厂方法
-   类型安全设计

一个了可以有多个对象声明作为成员

```kotlin
class MyClass private constructor() {
    object Factory {
        fun create(): MyClass = MyClass()
    }

    object Builder {
        fun build(): MyClass = MyClass()
    }
}

fun main() {
    MyClass.Factory.create()
    MyClass.Builder.build()
}
```

但只能有一个伴生对象, 伴生对象用`companion`修饰, 能直接从类名调用

```kotlin
class MyClass {
    companion object Static {
        fun xorHash(a: MyClass, b: MyClass) = a.hashCode() xor b.hashCode()
    }
}

fun main() {
    val xorHash1 = MyClass.Static.xorHash(MyClass(), MyClass())
    val xorHash2 = MyClass.xorHash(MyClass(), MyClass())
}
```

伴生对象可以直接忽略名称

```kotlin
class MyClass {
    companion object  {
        fun xorHash(a: MyClass, b: MyClass) = a.hashCode() xor b.hashCode()
    }
}
```

在伴生对象中的private成员, 对于外部类可见

```kotlin
class MyClass {
    companion object {
        private fun xorHash(a: MyClass, b: MyClass) = a.hashCode() xor b.hashCode()
    }

    fun xor(other: MyClass) = xorHash(this, other)
}
```

类名单独使用时, 指向其伴生对象的引用

```kotlin
class User1 {
    companion object
}
val reference1 = User1 // OK, refer to companion

class User2 

val reference2 = User2 // ERROR
```

## 对象表达式

类似创建一个暂时的对象

在局部使用

```kotlin
val helloWorld = object {
    val hello = "Hello"
    val world = "World"
    override fun toString() = "$hello $world"
}

println(helloWorld)
println(helloWorld.hello)
println(helloWorld.world)
```

### 继承或实现

继承类的对象表达式

```kotlin
abstract class HelloWorld {
    abstract fun hello(): String
}

fun main() {
    val helloWorld: HelloWorld = object : HelloWorld() {
        override fun hello() = "hello world"
    }

    print(helloWorld.hello())
}
```

实现接口的对象表达式

```kotlin
interface HelloWorld {
    fun hello(): String
}

fun main() {
    val helloWorld: HelloWorld = object : HelloWorld {
        override fun hello() = "hello world"
    }

    print(helloWorld.hello())
}
```

实现两个接口

```kotlin
interface Hello {
    fun hello(): String
}

interface World {
    fun world(): String
}

fun main() {
    val helloWorld =object : Hello, World {
        override fun hello() = "hello"
        override fun world() = "world"
    }

    print(helloWorld.hello())
    print(helloWorld.world())
}
```

### 作为返回值

如果不涉及继承/实现, 则自动分析返回值类型Any

继承/实现一个, 则分析为该类/接口的类型

实现了多个接口后, 必须指定返回值的类型, 只能选其中一个类型....

```kotlin
fun get(): Hello = object : Hello, World {
    override fun hello() = "hello"
    override fun world() = "world"
}
```

## 有关object声明的区别

-   对象表达式是在使用他们的地方初始化
-   对象声明是在第一次被访问到时*延迟* 初始化
-   伴生对象在类被加载时初始化

