# 包与导入

## 可见性

-   默认为 `public`
-    `private`，文件内可见。
-    `internal`，它会在相同模块内可见。
    -   模块指编译在一起的一套Kotlin文件
    -   一个 IntelliJ IDEA 模块
    -   一个 Maven 项目
    -   一个 Gradle 源代码集(test可以访问main的internal)
    -   一次 `<kotlinc>` Ant 任务编译的一套文件
-   `protected` 修饰符不适用于顶层声明

## 包

源文件以包声明开头

```kotlin
package org.example
```

## 导入

可以导入

-   类
-   枚举常量
-   顶层函数
-   顶层属性
-   `object`声明中的属性和函数(优点类似于静态成员)

```kotlin
import org.example.Message 
```

或者导入全部内容(编译器会做分析, 其实是导入需要的内容)

```kotlin
import org.example.* 
```

可以使用`as`关键字在本地重命名来消除歧义

```kotlin
import org.example.Message 
import org.test.Message as TestMessage
```

## 默认导入

-   [kotlin.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html)
-   [kotlin.annotation.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/index.html)
-   [kotlin.collections.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/index.html)
-   [kotlin.comparisons.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.comparisons/index.html)
-   [kotlin.io.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/index.html)
-   [kotlin.ranges.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/index.html)
-   [kotlin.sequences.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/index.html)
-   [kotlin.text.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/index.html)

根据目标平台还会导入额外的包：

-   JVM:
    -   java.lang.*
    -   [kotlin.jvm.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/index.html)
-   JS:
    -   [kotlin.js.*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.js/index.html)

