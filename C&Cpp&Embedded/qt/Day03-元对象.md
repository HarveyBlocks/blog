# 元对象

Qt 元对象系统实现了对象之间通信机制——信号和槽

提供了运行时类型信息和动态属性系统

元对象系统是 Qt 类库独有的功能，是 Qt 对标准 C++ 的扩展

## 元对象系统的前提

-   直接或间接地继承 `QObject` 

    -   这样才能利用元对象系统的功能
    -   Qt 的窗体和控件最顶层的基类都是 `QObject`。

-   将 `Q_OBJECT` 放在类声明的私有段落

    -   以启用元对象特性，如:

        动态属性, 信号 , 槽

-   元对象编译器（`Meta-Object Compiler`，`moc`）为每个 QObject 的子类提供必要的代码以实现元对象特性。

## 特性

-   `QObject::metaObject() `
    -   函数返回当前类对象关联的元对象（meta-object）。
-   `QMetaObject::className()` 
    -   返回当前对象的类名称字符串
    -   不需要 C++ 编译器原生的运行时类型信息（`run-time type information，RTTI`）支持。
-   `QObject::inherits()` 
    -   判断当前对象是否从某个父类派生
    -   判断某个父类是否位于从 `QObject` 到对象当前类的继承树上。
-   `QObject::tr()` 和 `QObject::trUtf8()` 
    -   负责翻译国际化字符串
    -   因为 Qt5 规定源文件字符编码是 UTF-8，所以这两个函数现在功能是一样的
-   `QObject::setProperty()` 和 `QObject::property()` 
    -   用于动态设置和获取属性
    -   都通过属性名称字符串来操作
-   `QMetaObject::newInstance() `
    -   构建一个当前类的新实例对象。
-   `qobject_cast()` 
    -   对基于 `QObject` 的类对象进行转换
    -   功能类似标准 C++ 的 dynamic_cast()
    -   优势在于不需要编译器支持 RTTI，支持跨动态链接库之间的转换
    -   子类对象指针，可以转为父类对象指针来用
    -   转换失败的情况都会得到 NULL 指针。

