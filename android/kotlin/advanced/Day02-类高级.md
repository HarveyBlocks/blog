# 其他类

## 嵌套类

### 嵌套类

逻辑上是静态内部类

声明

```kotlin
class Outer {
    class Inner
    interface IInner
}
```

实例化

```kotlin
val inner = Outer.Inner()
```

### 内部类

逻辑上是实例内部类

```kotlin
class Outer {
    inner class Inner
}
```

实例化

```kotlin
val inner = Outer().Inner()
```

### 匿名内部类

使用[对象表达式](Day02-object#对象表达式)创建匿名内部类

-   对于接口的匿名内部类

    有关接口准备

    ```kotlin
    interface Animal {
        fun eat()
        fun alive(): Boolean;
    }
    
    fun life(animal: Animal) {
        while (animal.alive()) {
            animal.eat()
        }
    }
    ```

    表达式

    ```kotlin
    life(object : Animal {
        override fun eat() {
            print("eating")
        }
    
        override fun alive(): Boolean {
            return true;
        }
    });
    ```

-   对于抽象类的匿名内部类, 区别在于有构造器

    有关抽象类准备

    ```kotlin
    abstract class Animal(val alive: Boolean) {
        abstract fun eat()
    }
    
    fun life(animal: Animal) {
        while (animal.alive) {
            animal.eat()
        }
    }
    ```

    使用匿名内部类

    ```kotlin
    life(object : Animal(true) {
        override fun eat() {
            print("eating")
        }
    });
    ```







## 枚举

```kotlin
enum class Color {
    RED, GREEN, BLUE
}
```

使用构造函数的

```kotlin
enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF)
}
```

### 成员

使用`;` (必须)分离枚举常量和成员

```kotlin
enum class Color(val rgb: Int) {
    RED(0xFF0000), GREEN(0x00FF00), BLUE(0x0000FF);

    val hexString: String
        get() = String.format("#%06X", rgb)

    fun toRgbString(): String {
        return "rgb(${rgb.ushr(4).and(0xFF)},${rgb.ushr(2).and(0xFF)},${rgb.and(0xFF)})"
    }
}
```

### 匿名类

枚举常量, 每个常量可以对抽象方法进行各自的实现

```kotlin
enum class ProtocolState {
    WAITING {
        override fun signal() = TALKING
    },

    TALKING {
        override fun signal() = WAITING
    };

    abstract fun signal(): ProtocolState
}
```

所有枚举类默认实现 Comparable 接口。枚举类中的常量按自然顺序定义。

### 使用

枚举类的一些静态伴生成员

```kotlin
val colors: EnumEntries<Color> = Color.entries
val colorList: List<Color> = Color.entries
val colors0: Color = Color.entries[0]
val color: Color = Color.valueOf("RED")
```

有一些静态方法可供获取枚举成员

```kotlin
val colors: Array<Color> = enumValues<Color>() // 返回新数组
val colorList:  EnumEntries<Color> = enumEntries<Color>() // 成员不可写, 故可返回同一个列表
val color:Color = enumValueOf<Color>("RED")
```

也有实例成员

```kotlin
val name: String = Color.RED.name;
val ordinal: Int = Color.RED.ordinal;
```



## 解构

### 解构声明

```kotlin
val (name, age) = person
```

使用解构声明的代码,会编译成调用`componetN()`的形式

```kotlin
val name = person.component1()
val age = person.component2()
```

`componentN`需要`operator`标记

```kotlin
class Student(val name: String, val age: Int) {
    operator fun component1(): String {
        return name;
    }

    operator fun component2(): Int {
        return age
    }
}
```

### for-each中使用

```kotlin
for ((name, age) in studentList) {  }
```



### 多返回值

```kotlin
data class Result(val result: Int, val status: Status)
fun function(...): Result {
    // 各种计算
    return Result(result, status)
}

// 使用该函数
val (result, status) = function(...)
```

好令人无语的功能

### 映射

Map的成员类型`Map.Entry`有方法`component1`和`component2`

### 下划线ignore

可以用于ignore不需要的成员

```kotlin
val (_,age) = person // 前面的字段不想要可以用_i
val (name) = person // 后面的字段不想要可以直接不写
```



### lambda

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



## 数据类

数据类的有关实现, 依靠且仅依靠**主构造函数的参数**来进行

可以在类体里声明字段, 但不会被下面实现解析

-   var 字段的 Getter/Setter
-   val 字段的 Setter
-   compareTo() 实现
-   equals() 实现
-   hashCode() 实现
-   toString() 实现 格式是 `"User(name=John, age=42)"`
-   copy() 实现
-   componentN() 函数 按照属性声明顺序实现, 用于对象[解构](Day02-解构)



```kotlin
data class Student(val name: String, val age: Int) {
}
```



### 要求

要求数据类应当满足

-   主构造函数至少一个参数
-   主构造函数必须所有参数用`val`或者`var`标记为属性
-   数据类不能是abstract, open, sealed, or inner
-   如果类中自主实现, 或者类的父类中有有关具体实现相关方法, 则使用已实现的函数
-   不允许为 `componentN()` (N 大于 属性个数的componetN是允许的)以及 `copy()` 函数提供显式实现
-   如果父类存在`componentN`
    -   open 且 返回兼容的类型， 则override父类实现
    -   否则报错

### copy

浅拷贝

```kotlin
data class Score(val english: Int, val math: Int, val physics: Int)

data class Student(val name: String, val age: Int, val score: Score)

fun main() {
    val score = Score(92, 92, 92)
    val student = Student("A", 12, score)
    println(student.copy().score === score)// true
}
```

参数可以指定有关参数进行改变, 其余不改变

```kotlin
student.copy(age = 13)
```

### 标准数据类

标准库提供了 `Pair` 与 `Triple` 类, 都是数据类



## sealed密封

对继承进行限制, 只有在同包同模块下(子包也不行)的文件才能进行继承

密封类/接口与when表达式结合, 就一定能涵盖所有的实现/子类

sealed总是抽象的, 因此无法实例化

sealed的构造器有`protected`默认和`private`

sealed限制**不适用于间接子类**

### 用处

-   需要一个有限的子类集合
-   需要类型安全的架构设计
-   为库提供健壮且可维护的公共 API



## 类型别名

类型别名不会引入新类型。 它们等效于相应的底层类型。

```kotlin
class A {
    open inner class BInner
    open class CInner
}
typealias Predicate<T> = (T) -> Boolean
typealias FileTable<K> = MutableMap<K, MutableList<File>>
typealias ABInner = A.BInner
typealias ACInner = A.CInner

class D : ACInner() 
```

奇妙的是, 类型别名不允许作为成员, 也不允许在函数内局部定义, 只允许在文件级声明



