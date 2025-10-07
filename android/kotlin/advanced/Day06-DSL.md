# DSL

> Domain Specific Language

编程语言提供的一种特殊能力, 用于编写出一些看似脱离其原始语法结构的代码，从而构建出一种专有的语法结构



## 构建器

采用半声明方式构建复杂层次数据结构领域专用语言

-   复杂层次数据结构领域专用语言, 例如XML / HTML / Groovy

-   半声明方式: 混合使用Lambda表达式, 尾Lambda等语法糖的一种代码模式

    [半声明方式示例](#用法)



## Groovy

```groovy
dependencies { 
    implementation 'com.squareup.retrofit2:retrofit:2.6.1' 
    implementation 'com.squareup.retrofit2:converter-gson:2.6.1' 
} 
```

用kotlin模仿这种格式编写

```kotlin
class Dependencies {
    private val dependencies: MutableList<String> = mutableListOf()

    infix fun Dependencies.implementation(dependence: String) {
        dependencies.add(dependence)
    }

    companion object {
        fun dependencies(block: Dependencies.() -> Unit): List<String> {
            val dependencies = Dependencies()
            dependencies.block()
            return dependencies.dependencies.toImmutableList()
        }
    }
}

```

使用

```kotlin
val lib: List<String> = dependencies {
    this implementation "v"
    this implementation "v"
    this implementation "v"
    this implementation "v"
    implementation("o")
    implementation("o")
    implementation("o")
    implementation("o")
    implementation("o")
}
```

## HTML

### API实现

```kotlin
package org.harvey.kotlin.learn

interface Element {
    /**
     * 渲染
     * @param indent 缩进
     */
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append(indent).append(text).append('\n')
    }
}

@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
abstract class Tag(val name: String) : Element {
    val children = arrayListOf<Element>()
    val attributes = hashMapOf<String, String>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        // 1. 加前缀
        builder.append(indent).append("<").append(name)
        renderAttributes(builder) // 1.2 加属性
        builder.append(">\n")
        // 2. 递归渲染子标签
        for (c in children) {
            c.render(builder, "$indent  ")
        }
        // 3. 加后缀
        builder.append(indent).append("</").append(name).append(">\n")
    }

    private fun renderAttributes(builder: StringBuilder): StringBuilder {
        for ((attr, value) in attributes) {
            builder.append(' ').append(attr).append('=').append('"').append(value).append('"')
        }
        return builder
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

/**
 * 内部能填写文本的标签
 */
abstract class TagWithText(name: String) : Tag(name) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

class HTML : TagWithText("html") {
    fun head(init: Head.() -> Unit) = initTag(Head(), init)

    fun body(init: Body.() -> Unit) = initTag(Body(), init)
}

class Head : TagWithText("head") {
    fun title(init: Title.() -> Unit) = initTag(Title(), init)
}

class Title : TagWithText("title")

abstract class BodyTag(name: String) : TagWithText(name) {
    fun b(init: B.() -> Unit) = initTag(B(), init)
    fun p(init: P.() -> Unit) = initTag(P(), init)
    fun h1(init: H1.() -> Unit) = initTag(H1(), init)
    fun a(href: String, init: A.() -> Unit) {
        val a = initTag(A(), init)
        a.href = href
    }
}

class Body : BodyTag("body")
class B : BodyTag("b")
class P : BodyTag("p")
class H1 : BodyTag("h1")

class A : BodyTag("a") {
    var href: String
        get() = attributes["href"]!!
        set(value) {
            attributes["href"] = value
        }
}

fun html(init: HTML.() -> Unit): HTML {
    val html = HTML()
    html.init()
    return html
}

```



### 用法

```kotlin
// 参数改变, result改变
val args: List<String> = listOf("a", "b", "c")

// 最终用法
fun result() =
    html {
        head {
            // head {  } ERROR, 因为DSL标记
            // this@html.head {  } OK 
            title { +"XML encoding with Kotlin" }
        }
        body {
            h1 {
                +"XML encoding with Kotlin"
            }
            p { +"this format can be used as an alternative markup to XML" }

            // 一个具有属性和文本内容的元素
            a(href = "https://kotlinlang.org") { +"Kotlin" }

            // 混合的内容
            p {
                +"This is some"
                b { +"mixed" }
                +"text. For more see the"
                a(href = "https://kotlinlang.org") { +"Kotlin" }
                +"project"
            }
            p { +"some text" }

            
            p {
                // 用代码动态生成内容
                for (arg in args) +arg
            }
        }
    }

fun main() {
    val html = result();
    println(html)
}

```

