# 扩展

## 扩展属性

由于拓展没有实际将成员插入类, 其定义对于类是静态的, 也不占有对象的空间. 因此, 扩展属性不能初始化, 只能通过Getter/Setter来定义

```kotlin
val String.lastChar: Char
    get() = this[this.length - 1]
```



## 扩展函数

为一个不能修改的、来自第三方库中的类或接口编写一个新的函数

这个新增的函数就像那个原始类本来就有的函数一样，可以用寻常方式调用

只是不能访问类内的被限制的字段(`private` or `protected`) 

```kotlin
fun Int.power(other: Int): Int {
    return Math.pow(this.toDouble(), other.toDouble()).toInt();
}
```

```kotlin
println(1.power(2))
```

## 可空Reciver

```kotlin
fun Any?.toString(): String = if (this == null)  "null" else toString()
```

```kotlin
fun main() {
    println(null.toString())
}
```



## 伴生对象的扩展

```kotlin
class MyClass {
    companion object { }  // 将被称为 "Companion"
}

fun MyClass.Companion.printCompanion() { println("companion") }

fun main() {
    MyClass.printCompanion()
}
```

## 作用域

在伴生对象/类内部对象声明的成员扩展时, 还是要import才能使用, 即使在同一个文件

```kotlin
import org.harvey.kotlin.learn.MyClass.Companion.extension // 必要

class MyClass {
    companion object {
        fun Int.extension() {
            print("A");
        }
    }
}



fun main() {
    1.extension();
}
```

## 扩展作为成员

当this冲突时, 使用`@`限定扩展内容

```kotlin
class Good {

}

class Store {
    fun Good.joinStringWith(): String {
        return this.toString()/*Good*/ + this@Store.toString();
    }
}
```

可以修饰`open`被子类覆盖

能在类内部作为方法使用

