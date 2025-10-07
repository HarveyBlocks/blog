# 解析XML

在assets目录下创建XML文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<school>
    <students>
        <student>
            <id>1</id>
            <name>Student A</name>
            <age>15</age>
            <score>
                <math>99</math>
                <english>63</english>
                <science>100</science>
            </score>
        </student>
        <student>
            <id>2</id>
            <name>Student B</name>
            <age>15</age>
            <score>
                <math>82</math>
                <english>93</english>
                <science>90</science>
            </score>
        </student>
        <student>
            <id>3</id>
            <name>Student C</name>
            <age>16</age>
            <score>
                <math>92</math>
                <english>72</english>
                <science>82</science>
            </score>
        </student>
        <student>
            <id>4</id>
            <name>Student A</name>
            <age>15</age>
            <score>
                <math>52</math>
                <english>63</english>
                <science>40</science>
            </score>
        </student>
    </students>
    <teachers>
        <teacher>
            <id>1</id>
            <name>Teacher A</name>
            <subject>science</subject>
        </teacher>
        <teacher>
            <id>2</id>
            <name>Teacher B</name>
            <subject>english</subject>
        </teacher>
        <teacher>
            <id>3</id>
            <name>Teacher C</name>
            <subject>science</subject>
        </teacher>
        <teacher>
            <id>4</id>
            <name>Teacher A</name>
            <subject>math</subject>
        </teacher>
        <teacher>
            <id>5</id>
            <name>Teacher D</name>
            <subject>math</subject>
        </teacher>
        <teacher>
            <id>6</id>
            <name>Teacher E</name>
            <subject>english</subject>
        </teacher>
    </teachers>
</school>
```

## SAX

### 工具

目标是将XML文档里的各个节点变成各集合的形式

```kotlin
interface Node {
    override fun toString(): String
}

open class ElementNode(
    val tag: String
) : Node {
    val inner: MutableList<Node> = mutableListOf()

    override fun toString(): String {
        val builder = StringBuilder()
        inner.forEach { builder.append(it) }
        return "<$tag>$builder</$tag>"
    }
}

class TextNode(val text: String) : Node {
    override fun toString(): String {
        return text
    }
}

class Doc() { // 最终目标
    val docNodes: MutableList<ElementNode> = mutableListOf()
    override fun toString(): String = StringBuilder().apply {
        docNodes.forEach { append(it) }
    }.toString()
}
```

由于下面有一个pull的复用性, 故使用装饰器模式

```kotlin
private class SimpleDocumentNodeHandler(private val skipWhitespace: Boolean) {
    private val docs = Doc()
    private val stack: LinkedList<ElementNode> = LinkedList()
    val document: Doc
        get() {
            require(stack.isEmpty()) { "There are some tags $stack that are not closed" }
            return docs
        }

    fun startDocument() {
    }

    fun startElement(localName: String) { // 开启节点
        val nodeName = localName
        val node = ElementNode(nodeName)
        if (stack.isEmpty()) {
            docs.docNodes.add(node)
        } else {
            stack.last().inner.add(node)
        }
        stack.addLast(node)
    }


    fun characters(text: String) { // 文本
        require(stack.isNotEmpty()) { "text should after before tag" }
        val trim = if (skipWhitespace) text.trim() else text
        if (trim.isNotEmpty()) {
            // 去除空格
            stack.last().inner.add(TextNode(trim))
        }
    }

    fun endElement(localName: String) { // 关闭节点
        require(stack.isNotEmpty()) { "end tag should after before tag" }
        val top = stack.removeLast()
        val nodeName = localName
        require(top.tag == nodeName) {
            "There are no matching tags, with start is ${top.tag} while end is $nodeName"
        }
    }

    fun endDocument() {
    }
}
```

装饰器装饰

```kotlin
class DocumentNodeHandler(skipWhitespace: Boolean = true) : DefaultHandler() {
    private val simpleHandler = SimpleDocumentNodeHandler(skipWhitespace)

    val document: Doc = simpleHandler.document

    override fun endDocument() {
        simpleHandler.endDocument()
    }

    override fun startDocument() {
        simpleHandler.startDocument()
    }

    override fun startElement(
        uri: String, localName: String, qName: String, attributes: Attributes
    ) {
        simpleHandler.startElement(localName)
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        simpleHandler.characters(String(ch, start, length))
    }

    override fun endElement(
        uri: String, localName: String, qName: String
    ) {
        simpleHandler.endElement(localName)
    }

}
```

封装流程

```kotlin
val saxFactory: SAXParserFactory = SAXParserFactory.newInstance()

fun saxParse(inputSource: InputSource, skipWhitespace: Boolean = true): Doc {
    val xmlReader = saxFactory.newSAXParser().xmlReader
    val handler = DocumentNodeHandler(skipWhitespace)
    // 将ContentHandler的实例设置到XMLReader中
    xmlReader.contentHandler = handler
    xmlReader.parse(inputSource)
    return handler.document
}
```



### 使用

```kotlin
val xml: InputStream = assets.open("my_xml_test.xml")
val document = XmlUtils.saxParse(InputSource(xml))
logger.info(document.toString()) // logger是自己写的
```

## pull

### 工具

```kotlin
val pullFactory: XmlPullParserFactory = XmlPullParserFactory.newInstance()

fun pullParse(reader: Reader, skipWhitespace: Boolean = true): Doc {
    val handler = SimpleDocumentNodeHandler(skipWhitespace)
    val xmlPullParser = pullFactory.newPullParser()
    xmlPullParser.setInput(reader)
    var eventType = xmlPullParser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> handler.startElement(xmlPullParser.name)
            XmlPullParser.TEXT -> handler.characters(xmlPullParser.text)
            XmlPullParser.END_TAG -> handler.endElement(xmlPullParser.name)
            XmlPullParser.START_DOCUMENT -> handler.startDocument()
            // XmlPullParser.END_DOCUMENT -> handler.endDocument()
            else -> throwUnmatchedWhen("XmlPullParser.EventType", eventType)
        }
        eventType = xmlPullParser.next()
    }
    return handler.document
}
```

### 使用

```kotlin
val xml: InputStream = assets.open("my_xml_test.xml")
val docs = XmlUtils.pullParse(InputStreamReader(xml))
logger.info(docs.toString())
```

## 代码清单

```kotlin
object XmlUtils {

    interface Node {
        override fun toString(): String
    }

    open class ElementNode(
        val tag: String
    ) : Node {
        val inner: MutableList<Node> = mutableListOf()

        override fun toString(): String {
            val builder = StringBuilder()
            inner.forEach { builder.append(it) }
            return "<$tag>$builder</$tag>"
        }
    }

    class TextNode(val text: String) : Node {
        override fun toString(): String {
            return text
        }
    }

    class Doc() {
        val docNodes: MutableList<ElementNode> = mutableListOf()
        override fun toString(): String = StringBuilder().apply {
            docNodes.forEach { append(it) }
        }.toString()
    }

    private class SimpleDocumentNodeHandler(private val skipWhitespace: Boolean) {
        private val docs = Doc()
        private val stack: LinkedList<ElementNode> = LinkedList()
        val document: Doc
            get() {
                require(stack.isEmpty()) { "There are some tags $stack that are not closed" }
                return docs
            }

        fun startDocument() {
        }

        fun startElement(
            localName: String
        ) {
            val nodeName = localName
            val node = ElementNode(nodeName)
            if (stack.isEmpty()) {
                docs.docNodes.add(node)
            } else {
                stack.last().inner.add(node)
            }
            stack.addLast(node)
        }


        fun characters(text: String) {
            require(stack.isNotEmpty()) { "text should after before tag" }
            val trim = if (skipWhitespace) text.trim() else text
            if (trim.isNotEmpty()) {
                // 去除空格
                stack.last().inner.add(TextNode(trim))
            }
        }

        fun endElement(localName: String) {
            require(stack.isNotEmpty()) { "end tag should after before tag" }
            val top = stack.removeLast()
            val nodeName = localName
            require(top.tag == nodeName) {
                "There are no matching tags, with start is ${top.tag} while end is $nodeName"
            }
        }

        fun endDocument() {
        }
    }

    class DocumentNodeHandler(skipWhitespace: Boolean = true) : DefaultHandler() {
        private val simpleHandler = SimpleDocumentNodeHandler(skipWhitespace)

        val document: Doc = simpleHandler.document

        override fun endDocument() {
            simpleHandler.endDocument()
        }

        override fun startDocument() {
            simpleHandler.startDocument()
        }

        override fun startElement(
            uri: String, localName: String, qName: String, attributes: Attributes
        ) {
            simpleHandler.startElement(localName)
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            simpleHandler.characters(String(ch, start, length))
        }

        override fun endElement(
            uri: String, localName: String, qName: String
        ) {
            simpleHandler.endElement(localName)
        }
    }

    val saxFactory: SAXParserFactory = SAXParserFactory.newInstance()

    fun saxParse(inputSource: InputSource, skipWhitespace: Boolean = true): Doc {
        val xmlReader = saxFactory.newSAXParser().xmlReader
        val handler = DocumentNodeHandler(skipWhitespace)
        // 将ContentHandler的实例设置到XMLReader中
        xmlReader.contentHandler = handler
        xmlReader.parse(inputSource)
        return handler.document
    }

    val pullFactory: XmlPullParserFactory = XmlPullParserFactory.newInstance()

    fun pullParse(reader: Reader, skipWhitespace: Boolean = true): Doc {
        val handler = SimpleDocumentNodeHandler(skipWhitespace)
        val xmlPullParser = pullFactory.newPullParser()
        xmlPullParser.setInput(reader)
        var eventType = xmlPullParser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> handler.startElement(xmlPullParser.name)
                XmlPullParser.TEXT -> handler.characters(xmlPullParser.text)
                XmlPullParser.END_TAG -> handler.endElement(xmlPullParser.name)
                XmlPullParser.START_DOCUMENT -> handler.startDocument()
                // XmlPullParser.END_DOCUMENT -> handler.endDocument()
                else -> throwUnmatchedWhen("XmlPullParser.EventType", eventType)
            }
            eventType = xmlPullParser.next()
        }
        return handler.document
    }
}
```