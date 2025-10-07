# this 表达式

在扩展函数, 带有Receiver的匿名函数, 带有Receiver的Lambda函数, 在内部类中, this的指代都有可能不明

如果 `this` 没有标签限定，它指的是最内层(如果作用域无Receiver, 则向更外一层, 直到有Receiver)的作用域

## 标签

使用标签限定, 使this指向其他作用域

```kotlin
class MyClass(val msg: String)
class OtherClass(val msg: String)

class Outer { // 隐式标签 @Outer
    val msg: String = "Outer"

    inner class Inner { // 隐式标签 @Inner
        val msg: String = "Inner"
        fun OtherClass.method() { // 隐式标签 @method
            val msg: String = "method variable"
            val function: MyClass.(Int) -> Char = function_label@ fun MyClass.(i: Int): Char {
                println(msg) // method 的 局部变量
                println(this.msg) // 指MyClass
                println(this@method.msg) // 指OtherClass
                println(this@function_label.msg) // 指MyClass
                println(this@Inner.msg)
                println(this@Outer.msg)
                return this.msg[i]
            }
            val lambda: MyClass.(Int) -> Char = lambda_label@{ i ->
                println(msg) // method 的 局部变量
                println(this.msg) // this 指MyClass
                println(this@method.msg) // 指OtherClass
                println(this@lambda_label.msg) // 指MyClass
                println(this@Inner.msg)
                println(this@Outer.msg)
                this.msg[i]
            }

            fun MyClass.innerFunction(i: Int): Char { // 隐式标签 @innerFunction
                println(msg) // method 的 局部变量
                println(this.msg) // this 指MyClass
                println(this@method.msg) // 指OtherClass
                println(this@innerFunction.msg) // 指MyClass
                println(this@Inner.msg)
                println(this@Outer.msg)
                return this.msg[i]
            }
            
            println(msg) // method 的 局部变量
            println(this.msg) // 指OtherClass
            println(this@method.msg) // 指OtherClass
            println(this@Inner.msg)
            println(this@Outer.msg)
        }
    }
}
```

## 省略this

上面的例子中, 如果去掉局部变量的`msg`, 那么所有的`println(msg)`中的`msg`, 都等价于同作用域中的`this.msg`

但如果声明没有冲突, 省略掉this之后, 依旧能调用到外部的成员

```kotlin
class Outer {
    val msg: String = "msg"

    inner class Inner {
        val count: Int = 0
        fun method() {
            println(msg) // OK
            println(count) // OK
        }
    }
}
```

如果希望能够对于非本类的成员严格使用标签化的this, 则使用用注解`@DslMarker`

```kotlin
@DslMarker
annotation class StrictThis

@StrictThis
class Outer {
    val msg: String = "msg"

    @StrictThis
    inner class Inner {
        val count: Int = 0
        fun method() {
            println(msg) // ERROR
            println(this@Outer.msg) // OK
            println(count)
        }
    }
}
```

使用`@DslMarker`标注的注解称为**DSL 标记**

继承/实现, 同样会继承有关注解