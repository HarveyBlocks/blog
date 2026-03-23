# Code list

## 歧义

现代语言由于丰富的语法(前向声明, 模板, 类型推断), 往往无法一遍式完成编译

因此我想了以下编译方法, 单文件完成词法分析和语法分析, 多文件连接时完成语义分析

假设有以下类

```java
// D.java
public interface D { }
// C.java
public class C<T extends C<? extends D>> {}
// A.java
public class A extends C<B> implements D { }
// B.java
public class B extends C<A> implements D { }
```

泛型上下限采用 **in out** 的概念理解
解析A的声明需要知道B的类型是否 `in C<in D>`
查看B发现B是 `C<A> `的子类, 也就是判断 `C<A>` 是否是`in C<in D>`
确实是的, 于是编译成功
实现?----
那这个过程需要怎么知道跨文件信息?

1. 解析 A, 得出 A 是 C 的子类, 实现 D, 暂时不解析泛型(不做语义分析)
2. 检查 A 的父类 C<B> 的泛型, 发现 B, 于是去符号表里查 B. B 此时未解析, 于是解析 B
3. 解析 B, 得出 B 是 C 的子类, 实现 D, 暂时不解析泛型(不做语义分析)
4. 检查 B 的父类 C<A> 的泛型, 发现 A, 符号表中已经有 A.
5. 检查 A 是否能够作为 C 的泛型
6. 查看符号表 C 的定义是 C<T in C<in D>>, 也就是说, A需要满足 `A in C<in D>`
7. A 要满足 `A in C<in D>`, 也就是要满足 `D in C<in D>` 或 `C<B> in C<in D>`
8. `D in C<in D>` 显然错误
9. 要满足 `C<B> in C<in D>`, 需要满足 B in D, 确实满足
10. 向上返回, 得出 A 确实能作为 C 的泛型
11. 向上返回, 得出 B 的声明正确, B 的父类使用 C<A> 也是正确的
12. 向上返回, 此时需要判断 B 是否能作为 C 的泛型, 也就是判断是否 `B in C<in D>`
13. B extends C<A> implement D, 也就是说, 只需要保证 `D in C<in D>` 或 `C<A> in C<in D>`
14. `D in C<in D>`显然错误, `C<A> in C<in D>`, 需要进一步判断 `A in D` 的正确性, 确实正确
15. 向上返回, 得出 A 的父类 C<B> 是正确的类型形式
16. 最终得出, A 是正确的声明

总结: 有一些命题, 比如 "A 是 `C<B>` 的子类", 以及 "`C<B>` 是合法的". 在检查出 "`C<B>` 是合法的" 之前, 命题 "A 是 `C<B>` 的子类" 已经成立了. 在使用信息时, 即使"`C<B>` 是合法的"暂时没办法得出, 但 "A 是 `C<B>` 的子类" 依旧可以作为条件使用.

坏处: 这个符号表是跨文件的. 

那么, 如何对符号的信息进行分层呢?

 1. id(A) -> class A
 2. class A -> A extends C implements D (not check super generic)
 3. -> A's statement is correct (`C<B>` is correct);

那么编译的过程就是

1. 解析文件 A (此阶段不进行多文件的连接), 有类声明 A, 向符号表注册 id(A)->class

2. 记录 class A extends C implements D
     - 语法没问题
     - 不确定 C 是否是类
     - 不确定 D 是否是接口
     - 语义C<B>部分未检查, 但由于其在类声明中, 可以得知此处需要模板, 因此是模板上下文
     
3. 记录需要检查语义的模板上下文: C, D, C<B>, 正确的模板上下文的文法应该是:
     $$
     \begin{array}{1}
         template &\to& raw\_class\\
             &|& raw\_class<template>\\
             &|& raw\_class<in\; template>\\
             &|& raw\_class<out\; template>\\
         raw\_class &\to& identifier\\
             &|& identifier.raw\_class
     \end{array}
     $$

4. 在连接之前, 不去跨文件判断identifier是否是真的, 但单文件依旧可以做一些检查

     - 本文件存在这个identifier的声明, 语义上先不检查, 也就是说, 语法分析阶段, 即使能在本文件看出identifier不是类, 也不报错
     - 存在一个导入(import)指示这个identifier(先不论导入得对不对, 但一定要有导入)
     - identifier是根包/是SDK里的根包/是默认导入/是某个扩展的根包, 这个语法阶段不去分析

     关于这里, identifier是否正确, 我认为在语法阶段只去判断**这是一个identifier**, 保留"希望进一步了解identifier声明"的需求

     然后在后面**语义分析**的时候, 完成这个需求的信息, 然后进一步检查

     因此, **语法分析**阶段, 不去检查identifier, 只去保留需求, 只要文法通过, 语法分析阶段就算通过

     由于语法分析就会维护一个符号表, 用于将identity转换为编号, 其实语法分析阶段就会尝试分析id是谁
     在上面分析的三种id情况, 如果不是前两种情况, 我们姑且就认为其是第三种情况来保守猜测, 这样就可以脱离其他文件来进行了

     然后关于identifier的判断, 考虑作用域, 可以采用栈, 如果栈顶无法判断, 则往下尝试



关于模板上下文的正确判断, 我认为不要让语法分析阶段去判断identifier是类型还是变量, 留到语义分析再做. (`a<b>(c)`和`a<b<c>>(d)`, 都是存在歧义的),可能的情况是大于号/右移和后缀之间的矛盾. 语法分析阶段可以给出局部的两棵树, 来表示歧义的两种情况, 然后到了语义分析阶段再做考量

 