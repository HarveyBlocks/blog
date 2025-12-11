# 注解

## 声明

```kotlin
annotation class AnnotationIdentiifer
```

构造器里添加参数

```kotlin
annotation class MyAnnotation1 constructor(val msg: String)
annotation class MyAnnotation2(val msg: String)
```

-   只允许`public`
-   只允许`val`

```kotlin
annotation class MyAnnotation(val msg: String, val count: Int = 0, vararg val type: KClass<*>)
```

-   对应于 Java 原生类型的类型（Int、 Long等）
-   字符串
-   KClass
-   枚举
-   其他注解
-   以上类型的数组
-   **不允许nullable**

如果注解用作另一个注解的参数，则其名称不以 `@` 字符为前缀

```kotlin
@Target(AnnotationTarget.CLASS) // 元注解
annotation class MyAnnotation(vararg val anno: SomeAnnotation)

annotation class SomeAnnotation(val msg: String)

@MyAnnotation(
    SomeAnnotation("A"), // 不加@前缀
    SomeAnnotation("B"),
    SomeAnnotation("C"),
    SomeAnnotation("D"),
)
class MyClass
```

## 元注解

描述注解的注解

### `@Target`

指定允许该注解标注的目标元素类型

-   `AnnotationTarget.FILE` 文件
-   `AnnotationTarget.CLASS` 类
-   `AnnotationTarget.TYPE` 类型标注
-   `AnnotationTarget.TYPEALIAS` 类型别名 
-   `AnnotationTarget.FUNCTION`函数(不包括构造器)
-   `AnnotationTarget.FIELD`字段, 包括属性的幕后字段
-   `AnnotationTarget.PROPERTY`属性
-   `AnnotationTarget.PROPERTY_GETTER`属性Getter
-   `AnnotationTarget.PROPERTY_SETTER`属性Setter
-   `AnnotationTarget.LOACL_VARIABLE` 局部变量
-   `AnnotationTarget.CONSTRUCTOR` 构造器 (主构造器和次构造器)
-   `AnnotationTarget.TYPE_PARAMETER`  泛型参数
-   `AnnotationTarget.VALUE_PARAMETER`  函数参数
-   `AnnotationTarget.EXPRESSION` 表达式

```kotlin
@file:OnFile

package org.harvey.kotlin.learn


@Target(AnnotationTarget.CLASS)
annotation class OnClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class OnAnnotationClass

@Target(AnnotationTarget.TYPE_PARAMETER)
annotation class OnTypeParameter

@Target(AnnotationTarget.PROPERTY)
annotation class OnProperty

@Target(AnnotationTarget.FIELD)
annotation class OnFiled

@Target(AnnotationTarget.LOCAL_VARIABLE)
annotation class OnLocalVariable

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class OnValueParameter

@Target(AnnotationTarget.CONSTRUCTOR)
annotation class OnConstructor

@Target(AnnotationTarget.FUNCTION)
annotation class OnFunction

@Target(AnnotationTarget.PROPERTY_GETTER)
annotation class OnPropertyGetter

@Target(AnnotationTarget.PROPERTY_SETTER)
annotation class OnPropertySetter

@Target(AnnotationTarget.TYPE)
annotation class OnType

@Target(AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)// AnnotationTarget.EXPRESSION时, 必须SOURCE
annotation class OnExpression

@Target(AnnotationTarget.FILE)
annotation class OnFile

@Target(AnnotationTarget.TYPEALIAS)
annotation class OnTypeAlias

@OnClass
class MyClass<@OnTypeParameter T> @OnConstructor constructor(
    @OnValueParameter var msg: @OnType String,
    @OnValueParameter var count: @OnType Int,
    @OnValueParameter val t: @OnType T
) {
    @OnFiled
    @OnProperty
    val lengthInit = msg.length


    @OnFiled
    @OnProperty
    var countPlus1 = count + 1
        @OnPropertyGetter
        // @OnFunction ERROR
        get() = count + 1
        @OnPropertySetter set(value: Int) {
            field = value
            count = field - 1
        }

    // @OnFiled ERROR
    @OnProperty
    val length
        get() = msg.length

    @OnConstructor
    constructor(@OnValueParameter t: @OnType T) : this("", 0, t) {
        @OnLocalVariable val variable: @OnType String = @OnExpression ""
        @OnExpression return
    }

    @OnFunction
    fun method(): @OnType Int {
        @OnExpression return 0
    }
}

@OnTypeAlias
typealias IntMyClass = MyClass<Int>

@OnAnnotationClass
annotation class NewAnnotation
```

允许多选

```kotlin
@file:OnAny

package org.harvey.kotlin.learn


@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.EXPRESSION,
    AnnotationTarget.FILE,
    AnnotationTarget.TYPEALIAS
)
annotation class OnAny

@OnAny
class MyClass<@OnAny T> @OnAny constructor(
    @OnAny var msg: @OnAny String,
    @OnAny var count: @OnAny Int,
    @OnAny val t: @OnAny T
) {
    @OnAny
    val lengthInit = msg.length


    @OnAny
    var countPlus1 = count + 1
        @OnAny get() = count + 1
        @OnAny set(value: Int) {
            field = value
            count = field - 1
        }

    @OnAny
    val length
        get() = msg.length

    @OnAny
    constructor(@OnAny t: @OnAny T) : this("", 0, t) {
        @OnAny val variable: @OnAny String = @OnAny ""
        @OnAny return
    }

    @OnAny
    fun method(): @OnAny Int {
        @OnAny return 0
    }
}

@OnAny
typealias IntMyClass = MyClass<Int>

@OnAny
annotation class NewAnnotation
```

不加`@Target`其实是允许标注所有

```kotlin
annotation class OnAny

@Target
annotation class OnNothing
```

### @Retention

是否存储在编译后的 class 文件中，以及它在运行时能否通过反射可见

-   `AnnotationRetention.SOURCE` 注解不会保留在二进制文件
-   `AnnotationRetention.BINARY` 注解保留在二进制文件, 但是在反射中不可见
-   `AnnotationRetention.RUNTIME` 注解保留在二进制文件, 且对于反射可见



### @Repeatable

允许在单个元素上多次使用相同的该注解

```kotlin
@Repeatable
@Target(AnnotationTarget.PROPERTY)
annotation class RepeatableAnno

@Target(AnnotationTarget.PROPERTY)
annotation class NormalAnno

@NormalAnno
//@NormalAnno ERROR
@RepeatableAnno
@RepeatableAnno
@RepeatableAnno
var num = 12;
```



### @MustBeDocumented

指明该注解是公有 API 的一部分，并且应该包含在生成的 API 文档中显示的类或方法的签名中



### @Inherited

父类/接口的注解将通过`extends`/`implements`的行为遗传给子类/实现类

但是==override==这一行为并不能继承注解



## 实例化

在Java中, Anno是可以被实例化的....?

```java
public @interface Anno {
}

class AnnoImpl implements Anno {
    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
```

在Kotlin中, 允许调用注解的构造函数

```kotlin
annotation class Anno(val info: String)

val anno: Anno = Anno("msg")
val info: String = anno.info
```

## Lambda

在Lambda上可以直接使用注解, 这个注解被应用于Lambda表达式的 `invoke()` 方法上

在Lambda表达式上的注解需要`AnnotationTarget.EXPRESSION`

```kotlin
val f: (Int) -> Int = @OnExpression("message") { x -> x + 1 }
```



## 使用处目标

当对**属性**或**主构造函数参数**进行注解时，从相应的 Kotlin 元素生成的 Java 元素会有多个

例如, 属性上注解一个, 可能是注解在属性上, 也可能是注解在字段上

例如, 在主构造函数上注解一个, 可能是注解在属性上, 可能是注解在字段上, 可能是注解在Getter/Setter上, 可能是注解在Parameter上

使用`@目标:注解`的语法标注使用处目标(Java 元素)

```kotlin
class Example(@field:Ann val foo,    // 标注 Java 字段
              @get:Ann val bar,      // 标注 Java getter
              @param:Ann val quux)   // 标注 Java 构造函数参数
```

目标可选择

-   `file`

-   `property` 对Java 不可见

-   `get`

-   `set`

-   `field`

-   `param` 构造函数参数

-   `setparam` 属性setter参数

-   `receiver` 扩展函数或扩展属性的接收者参数

    ```kotlin
    @Target(AnnotationTarget.VALUE_PARAMETER)
    annotation class OnValueParameter
    
    fun @receiver:OnValueParameter String.myExtension() {
        
    }
    
    // ERROR
    fun @OnValueParameter String.myExtension() {
        
    }
    ```

-   `delegate` 委托属性存储其委托实例的字段

如果一个目标下要有多个注解, 则可以使用`[]`来避免目标重复

```kotlin
class Example {
     @set:[Annotation1 Annotation2 Annotation3 ...]
     var props: String
}
```

如果不标注使用处目标, 且匹配多个Target可能, 则优先级如下:

-   `PARAMETER_VALUE`
-   `PROPERTY`
-   `FIELD`

