# 编码样式

## 标识符

-   接口名称以大写字母 `I` 开头。
-   属性类型以单词 Attribute 结尾。
-   枚举类型对非标记使用单数名词，对标记使用复数名词。
-   不包含两个连续的 `_` 字符。 这些名称保留给编译器生成的标识符。
-   类名和方法名称用大驼峰
-   对方法参数和局部变量使用小驼峰
-   字段和局部常量使用大驼峰
-   专用实例字段以`_`开头，后使用小驼峰
-    `private` 或 `internal`的静态字段以 `s_` 开头
-   线程静态的字段以`t_`开头
-   命名空间使用反向域名表示法

-   字母简称
    -   S 表结构
    -   C 表类
    -   M 用于方法
    -   v 表变量
    -   p 表参数
    -   r 表 ref 参数
-   泛型参数, 如果用`T`不足以表达其含义, 则参数需以`T`开头, 例如`TInput`, `TOutput`

## 编码约定

-   静态成员在被调用时, 在前加上其声明类型`ExampleClass.StaticMember`来避免继承同名静态成员冲突

-   尽量使用var 来隐式表示类型明显的局部变量

-   避免使用 var 来代替 dynamic

-   using 命名空间放在命名空间声明外

    ```csharp
    using bbb;
    namespace aaa{
    	// Correct    
    }
    ```

    ```csharp
    namespace aaa{
    using bbb;
    	// NG    
    }
    ```
    
    因为bbb是在aaa的命名空间里using, 可能定位到的是`bbb`, 也可能是`aaa.bbb`
    
    于是异常, 找不到`bbb`的成员`XXX`
    
    ```
    - error CS0246: The type or namespace name 'XXX' could not be found (are you missing a using directive or an assembly reference?)
    - error CS0103: The name 'XXX' does not exist in the current context
    ```
    
    

## 注释

-   将注释放在单独的行上，而非代码行的末尾
-   使用[XML文档注释](../高级/Day01-XML文档注释.md)
