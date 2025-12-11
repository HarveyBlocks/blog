# 类

## 声明

```kotlin
class Person { /*……*/ }
```

没有类体的, 可以省略`{}`

```kotlin
class Empty
```

### 成员

-   属性
-   方法
-   构造函数
-   初始化块
-   嵌套类和对不累
-   对象声明
-   伴生对象

### 访问控制

-   `private` 成员在这个类内部（包含其所有成员）可见
-   `protected` private+子类可见
-   `internal`  类声明的*本模块内*的任何客户端都可见
-   `public` 默认

在继承的过程中, override了一个`protected`或者`internal`成员, 则与原成员具有相同的可见性

```kotlin
open class Parent {
    protected open val a = 1
    protected open val b = 2
    internal open val c = 3
}

class Child : Parent() {
    override val a = 5   // a 为 protected
    public override val b = 6   // b 修改为 public
    internal override val c = 7   // 'c' 为 internal, internalk
}
```

鉴于**开闭原则,** 子类只能将父类的成员override后, 扩大访问控制, 而不能缩小

## 初始化块

使用`init`标记的代码块, 类似于匿名代码块

```kotlin
class Animal {
    init {
        println("initializing...");
    }
}
```

在对象初始化时运行, 与字段按照源码的顺序依次加载

```kotlin
fun show(msg: String): String {
    println("filed $msg is initializing...")
    return msg;
}

class Animal {
    val a: String = show("a")

    init {
        println("initializing 1 ...");
    }

    val b: String = show("b")

    init {
        println("initializing 2 ...");
    }
}
```

实例化时输出

```console
filed a is initializing...
initializing 1 ...
filed b is initializing...
initializing 2 ...

```

## 属性

属性可以用`val`和`var`声明, `val`表示只读

```kotlin
var|val <propertyName>[: <PropertyType>] [= <property_initializer>]
    [<getter>]
    [<setter>]
```

一般的属性必须给定初始值,  但可以不指定类型, 由编译器来分析类型(从初始值或者Getter返回值)

```kotlin
class Person {
    var name: String = "Harvey"
    var age: Int = 18
    private val idCard: String = "123456"
    private var address = "London"
}
```

标记为`abstract`的属性不用给定初始值, 或者在构造函数中初始化的不需要赋初始值

### Getter 和 Setter

只读属性不允许使用setter

```kotlin
class Person {
    private var _age = 0;
    var age // 类型可以不给出, 从 get 推测
        get() = _age // 用 = 这种表达式
        set(value/*不用给出, 类型必须和属性类型一致, 参数名可以变*/) /*set 的返回值必须是Unit*/ { // 用代码块
            if (value in 0..250) {
                this._age = value
            }
        }
}
```

使用Getter和Setter

```kotlin
val p = Person()
println(p.age)
p.age = 3;
```

采用Getter和Setter的默认实现, 就是一个公共的字段

```kotlin
class Person {
    var name = "?"
        get
        set
}
```

getter和setter也可以用有独立的访问控制

```kotlin
class Person {
    internal var name = "?"
        /*getter 必须和属性一致*/ get
        private /*setter 不能比属性访问范围大*/ set
}
```

### 幕后字段

Getter和Setter没有幕后字段, 则一定会递归

使用`field`**标识符**来进行赋值

```kotlin
class Person {
    var age = 0 // 这个初始化过程直接向field赋值
        get():Int = field
        set(value) {
            field = value
        }
}
```

上述的Getter/Setter代码其实就是其默认实现

field是标识符, 不是关键字, 因此可以在其他地方作为函数名, 变量名等

```kotlin
class Person {
    val field = 3;

    val age = 0
        get():Int = field/*并不会引用到外面的field, 但如果有一个filed局部变量, 那么这个field标识符就不生效了*/
}
```

### 幕后属性

就是人为地定义一个字段, 而使用另一属性的Getter和Setter暴露这个属性

```kotlin
class Person {
    private var _age = 0;
    var age
        get() = _age 
        set(value) {
            if (value in 0..250) {
                this._age = value
            }
        }
}
```

## 构造器

一个类有一个*主构造函数*并可能有一个或多个*次构造函数*

不显示地声明任何构造器, 会有一个`public`的主构造器

### 主构造函数

主构造函数在类头中声明，它跟在类名与泛型后

```kotlin
class Person constructor(name: String, age: Int) {}
```

主构造函数可以加一些属于自己的访问修饰符和注解

```kotlin
class Person private constructor(name: String, age: Int) { }
```

当没有访问修饰符和注解时, `constructor`关键字可以省略

```kotlin
class Person (name: String, age: Int) { }
```

主构造器中的参数, 可以直接被字段initialization和init块使用

```kotlin
class Person(firstName: String, lastName: String) {
    val name = "$firstName.$lastName"

    init {
        print("first name is $firstName, and last name is $lastName")
    }
}
```

构造器也允许使用使用默认参数, 和不定参数

```kotlin
class Person(firstName: String,lastName: String, age: Int=0, vararg child: Person)
```

在构造器中声明属性, 在参数名前加上`var|val`, `vararg`无法直接作为声明的属性, 只能赋值给一个类内部的字段

```kotlin
class Person(val firstName: String,val lastName: String,var age: Int=0, vararg child: Person){ ... }
```

也可以在声明为属性之后加上访问修饰符

```kotlin
class Person(private val firstName: String,private val lastName: String,private var age: Int=0){ ... }
```

### 次构造函数

次构造器可以单独在无注构造器的类中声明, 可以加上修饰符

```kotlin
class Person {
    private val firstName: String
    private val lastName: String
    private var age: Int

    internal constructor(firstName: String, lastName: String, age: Int = 0) {
        // 可以不在字段后立刻实例化, 在构造器中实例化也行
        this.firstName = firstName
        this.lastName = lastName
        this.age = age
    }
}
```

多个构造器之间可以使用`this()`进行提前调用构造器进行初始化

构造器如果没有函数体, 也可以不写`{}`

```kotlin
class Person {
    private val firstName: String
    private val lastName: String
    private var age: Int

    constructor(firstName: String, lastName: String, age: Int = 0) {
        this.firstName = firstName
        this.lastName = lastName
        this.age = age
    }

    constructor(fullName: String, age: Int = 0) : this(
        fullName.substringBefore("."),
        fullName.substringAfter("."),
        age
    )
}
```

次构造可以和主构造器一起使用, 只需要次构造器能在初始化之前调用次构造器

```kotlin
class Person private constructor(
    private val firstName: String,
    private val lastName: String,
    private var age: Int = 0,
    vararg child: Person
) {
    constructor() : this("") {

    }

    constructor(fullName: String) : this(fullName.substringBefore("."), fullName.substringAfter(".")) {
    }
}
```

## 实例化

-   一般的实例化

    调用构造函数即可

    ```kotlin
    val customer = Customer("Joe Smith")
    ```

-   内部类的实例化

-   匿名内部类的实例化

一般的实例化过程中的运行顺序

1.   主构造器/次构造器上的`this`(this指向主构造器的情况)
2.   字段声明初始化/init初始化块 按照声明顺序同时进行
     -   初始化块中的代码实际上会成为主构造器的函数体
3.   次构造器上的`this`(this指向次构造器的情况)的函数体
4.   次构造器的函数体

## 继承

所有类都共同继承自超类`Any`, 没有父类, `Any`就是默认父类

`Any`有方法`hashCode()`, `equals()`, `toString()`

Kotlin 不支持多继承

### 语法

默认情况下Kotlin的类是不能被继承的, 要被继承就用`open`关键字标注

```kotlin
open class Parent // 该类开放继承
```

可以用主构造器继承

```kotlin
open class Parent(p: Int)

class Child(p: Int) : Parent(p)
```

也可以用次构造器继承

```kotlin
class Child : Parent {
    constructor(p: Int) : super(p)
}
```

私有父类不允许拥有共有子类

```kotlin
private open class Parent

class Child : Parent() // ERROR
```

### override 方法

方法一般是封闭的, 需要`open`才能进行`override`, `override`关键字是必须的

```kotlin
open class Parent {
    open fun show() {}
}

class Child : Parent() {
    override fun show() {}
}
```

即使父类没有open, 子类也不能出现同签名的方法

### override 属性

可以用一个 `var` 属性覆盖一个 `val` 属性, 反之则不行

因为一个 `val` 属性本质上声明了一个 `get` 方法， 而将其覆盖为 `var` 只是在子类中额外声明一个 `set` 方法

重载也包括了重载Getter和Setter

```kotlin
open class Parent {
    open val value: Int = 0
        get() {
            println("in super, filed is $field")
            return field + 2
        }
}

class Child : Parent() {
    override val value: Int = 4
        get() {
            println("super value is ${super.value}")
            println("in child, filed is $field")
            return field + 2
        }
}
```

使用getter

```kotlin
val child = Child()
println("child value is ${child.value}")
```

输出结果是

```console
in super, filed is 0
super value is 2
in child, filed is 4
child value is 6

```

在主构造函数里也可以使用open和override

```kotlin
open class Parent(
    open val value: Int = 0
)

class Child(
    override val value: Int = 4
) : Parent(value)
```

但是主函数体里不能有Getter/Setter, 这就很难绷了

### 派生类初始化顺序

1.   基类初始化
2.   子类初始化

由于是基类先初始化的, open的成员不会走子类的重载逻辑, 可能导致一些错误, 仁者见仁, 智者见智吧

### 调用super成员

使用`super`关键字调用super成员

基本用法略

可以使用`super@OuterClassName`的形式, 调用外部类的超类成员

```kotlin
open class Parent {
    open fun show() {
        println("show parent");
    }
}

class Child : Parent() {
    override fun show() {
        println("show child");
    }

    inner class Inner {
        fun show() {
            println("show inner");
        }

        fun showAll() {
            this.show();
            this@Child.show();
            super@Child.show();
        }
    }
}
```

调用`showAll`

结果

```console
show inner
show child
show parent

```

### 菱形继承

使用`super<UpperClass>`的形式调用父类的未被override的成员

```kotlin
package org.harvey.kotlin.learn

interface Drawable {
    fun draw() {
        println("draw drawable");
    }
}

interface Graphic {
    fun draw() {
        println("draw graphic");
    }
}

class Square() : Drawable, Graphic {
    override fun draw() {
        println("draw square");
    }

    fun drawAll() {
        this.draw() // draw square
        super<Drawable>.draw() // draw drawable
        super<Graphic>.draw() // draw graphic
    }
}

```

## 抽象类和抽象方法

不需要oppen标注抽象类及其抽象函数

```kotlin
abstract class Parent {
    abstract fun show()
}
```

可以用一个抽象成员覆盖一个非抽象的open成员

```kotlin
open class Animal {
    open fun show() {
        println("show");
    }
}

abstract class Person : Animal() {
    abstract override fun show() // override
}
```

