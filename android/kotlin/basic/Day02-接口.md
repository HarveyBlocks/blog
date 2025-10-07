# 接口

成员的访问修饰符只允许`public`(默认)和`private`

接口的方法都默认加上`open`, 无论是否有默认实现, `private`方法不会open

```kotlin
interface MyInterface {
    fun method1()
    fun method2() {
      // 可选的方法体
    }
}
```

接口可以有属性但必须声明为抽象

```kotlin
interface MyInterface {
    val prop: Int // 抽象的

    fun foo() {
        print(prop)
    }
}
class Impl(override val prop: Int) : MyInterface{

}
```

## 函数式接口

>   Single Absctract Method
>
>   单一抽象方法

```kotlin
fun interface IntPredicate {
   fun test(i: Int): Boolean
}
```

能直接将函数传入函数式接口的构造器(Specially)参数中

```kotlin
val isEven = IntPredicate(
    function = fun(i: Int): Boolean {
        return i % 2 == 0
    }
)
```

可以简化成lambda表达式

```kotlin
val isEven1 = IntPredicate(function = fun(i: Int): Boolean {
    return i % 2 == 0
})
val isEven2 = IntPredicate(function = { i: Int -> i % 2 == 0 }) // lambda 表达式
val isEven3 = IntPredicate() { i: Int -> i % 2 == 0 } // 移出 参数列表
val isEven4 = IntPredicate { i: Int -> i % 2 == 0 } // 省略括号
```

调用函数式接口的对象

```kotlin
val isEven = IntPredicate { i: Int -> i % 2 == 0 }
println(isEven.accept(1))
```

