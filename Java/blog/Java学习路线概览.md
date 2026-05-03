# Java学习路线概览
## 语言基础

### Java环境搭建与入门

学习如何搭建Java开发环境，理解Java程序的运行机制。

- JDK的下载、安装与环境变量配置
- Java文件的运行原理
- 第一个Java程序：Hello World的编写与详解
- IDE的安装与使用

### 基础语法

掌握Java语言的基本语法规则，这是编写任何Java程序的前提。

- 注释
- 变量、常量
- 数据类型（基本类型与引用类型）
- 运算符
- 字符串的基本操作
- 包机制

### 流程控制

学习如何控制程序的执行流程。

- 顺序
- 选择结构（if、switch）
- 循环结构（while、do-while、for、增强for）

### 方法

方法是代码复用的基本单位，数组是第一个数据容器。

- 方法的定义、调用与重载



### 数组

- 数组的静态初始化与动态初始化
- 数组的常见操作
- Arrays 工具类



## 面向对象编程

面向对象是Java的核心思想，本阶段是整个Java学习的重中之重。

### 类与对象

理解面向对象的基本概念，学会从现实世界抽象出类。

- 面向对象与面向过程的区别
- 类与对象的关系
- 对象的创建与构造器
- 方法的回顾与加深(方法与函数)
- 工厂方法模式

### 三大特性

封装、继承、多态是面向对象的三大支柱。

- 封装：隐藏实现细节，对外提供公共接口
- 继承：子类对父类的扩展，super关键字的使用
- 多态：对象多态与行为多态，编译看左运行看右

### 面向对象进阶

在三大特性之上，进一步学习Java面向对象的高级特性。

- 抽象类：约束与框架的设计
- 接口：规范与契约的定义，实现多继承效果
- static与final关键字
- 代码块（静态代码块、实例代码块）
- 内部类（成员内部类、静态内部类、局部内部类、匿名内部类）
- 类之间的关系
- 枚举类型
- instanceof与类型转换

### 异常机制

学习如何处理程序运行中的意外情况。

- Error与Exception的区别
- 运行时异常与编译时异常
- 异常处理框架（try-catch-finally）
- 自定义异常

### 常用类

Java提供了大量工具类，熟练使用它们能极大提高开发效率。

- **Object类**：所有类的根基
- **包装类**：基本类型与引用类型的桥梁
- **String类**：不可变字符串及其常用方法
- **可变字符串**（StringBuilder、StringBuffer）
- 字符串与基本类型的相互转换
- 整数缓存区
- BigDecimal：精确的浮点运算
- Date、Calendar、LocalDate：日期时间处理
- SimpleDateFormat：日期格式化
- Math与Random：数学运算与随机数
- **System类**
- Pattern 和 **正则表达式**

### 泛型

泛型主要使用在集合上, 可以简单学习泛型后, 开始上手集合, 学习泛型如何在集合上发挥作用, 然后回头深入学习泛型.

- 泛型概述：参数化类型的思想
- 泛型类
- 泛型接口
- 泛型方法
- 类型通配符
- 类型擦除：理解泛型在JVM层面的实现
- 泛型与反射(太难了, 没必要)

## 集合

集合是Java中存储和操作数据的核心工具，是日常开发中使用频率最高的部分。

### 集合体系

理解集合的整体架构，掌握各类集合的特点与使用场景。

- 集合的概念与体系结构
- Collection接口
- List体系：有序、有下标、可重复
  - ArrayList：数组列表（重点掌握）
  - LinkedList：链表
  - Vector：已过时
- Set体系：无序、无下标、不重复
  - HashSet
  - TreeSet
- Map体系：键值对存储
  - HashMap（重点掌握）
  - TreeMap
  - Properties
- 泛型集合
- Collections工具类
- 数组与List的相互转化

### 流式编程

- Lambda表达式：简化匿名内部类
- 方法引用：进一步简化Lambda
- Stream流：集合与数组的函数式操作
- Lombok：自动生成样板代码
- Optional：优雅处理空指针


## IO流

IO流是Java处理输入输出的核心机制，涵盖文件操作、数据传输等场景。

### IO流

- File类：文件与目录的表示
- IO流概述：输入流与输出流的分类体系
- 文件字节输入输出流
- 文件字符输入输出流
- 字节字符转换流
- 缓冲流：提升读写效率
- 序列流：对象的序列化与反序列化
- 打印流
- 通讯流
- 释放资源：try-with-resources
- Files工具类
- IO框架：第三方封装的IO工具



## 多线程

多线程是Java并发编程的基础，理解线程模型对编写高效程序至关重要。

### 多线程

- 基本概念：进程与线程、并行与并发
- 线程的创建方式（继承Thread、实现Runnable、实现Callable）
- 并发问题：线程安全与数据不一致
- 线程同步与锁机制
- 线程池（ThreadPoolExecutor、Executors工具类）
- CAS与乐观锁
- 死锁与饥饿
- 多线程模型
  - 哲学家就餐
  - 生产者-消费者
  - 顺序控制



### 虚拟线程

感兴趣的可以去了解一下



## 高级特性

### 注解

- 注解的定义与使用
- 元注解
- 自定义注解

### 反射

反射是框架设计的基石，理解反射有助于理解Spring等框架的底层原理。

- 反射的概念与功能
- 获取Class对象
- 反射成员变量
- 反射构造方法
- 反射成员方法
- 动态代理：无侵入式地增强功能
- MethodHandler：方法句柄
- 运行时类型

### 特殊文件与日志

- 属性文件（Properties）
- XML文件
- 日志技术（Logback等）

### 单元测试

- JUnit框架的使用
- 断言机制
- 测试规范与命名约定



## 网络编程

### 网络基础

- 网络通信三要素：IP地址、端口号、网络协议
- TCP与UDP协议
- InetAddress类
- 基于TCP的Java网络编程（Socket）
- 基于UDP的Java网络编程（DatagramSocket）
- 群聊实现



## Web开发

本阶段将前面所学知识综合运用，进入Java Web开发领域。

### Web基础

- HTTP协议：请求与响应的结构
- Tomcat：Web服务器
- Servlet：动态Web资源开发
- 请求（Request）与响应（Response）

### 会话跟踪

- 会话跟踪技术的概念与必要性
- Cookie：客户端会话跟踪
- Session：服务端会话跟踪
- 验证码实现

### JSP与前端交互

- JSP：Java服务端页面
- JSP脚本与缺点
- EL表达式与JSTL标签
- AJAX：异步请求
- JSON：数据交换格式
- Axios：AJAX的封装框架

### Web组件与架构

- Filter：过滤器
- Listener：监听器
- MVC模式与三层架构
- 实体类命名规范
- Postman：接口测试工具

## 掌握程度

熟悉并掌握

- 语言基础
- 面向对象
- 集合
- 多线程
- 单元测试

看过一遍有个影响, 有需要的时候再去查资料

- IO流
- 注解
- 反射
- 特殊文件与日志 (后面用处大, 完成第一轮的用处小)

偏底层, 感兴趣的去看一看. 看了对第二轮的理解有利(现在不看, 以后也是要看的就是了)

- 网络编程
- Web 开发

## 资料

[阿里巴巴Java开发规范](https://github.com/west2-online/learn-java/blob/main/etc/blog/%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C.pdf)

[单元测试](https://github.com/west2-online/learn-java/blob/main/etc/blog/%E5%8D%95%E5%85%83%E6%B5%8B%E8%AF%95.md)

[ Java 环境简单配置+IDEA](https://blog.csdn.net/qq_33215972/article/details/73693140)

强烈建议使用[IntelliJ IDEA](https://www.jetbrains.com/zh-cn/idea/)进行编程

- IntelliJ Idea(之后简称 Idea)可以通过[福州大学邮件系统 (fzu.edu.cn)](https://fzu.edu.cn/coremail/index.jsp)进行[Idea 学生认证](https://www.jetbrains.com/shop/eform/students/)后可免费使用一年(到期再次申请即可)
- [2022 JetBrains 开发工具——学生授权免费申请指南 | Company Blog](https://blog.jetbrains.com/zh-hans/blog/2022/08/24/2022-jetbrains-student-program/)

注意代码格式规范，可使用 Idea 自带的格式化快捷键 *ctrl+alt+L* 进行代码格式化

### 参考视频（建议刚接触编程语言的跟着视频走）

- [JavaSE 教程 已完结 (IDEA 2025 最新版) 4K 蓝光画质+杜比音效 零基础入门一套搞定 入门到入土 基于 Java25 讲解_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV163GGz2E8c/?vd_source=dff8e8da3e782503dba2b80a888e026c)
- [黑马程序员 Java 零基础视频教程](https://www.bilibili.com/video/BV17F411T7Ao?vd_source=e7a1a430689d9d09f914db65fcdea382)
- [动力节点](https://www.bilibili.com/video/BV1Rx411876f?share_source=copy_web&vd_source=7d2fd3963c594f890889ebd454ef8d1c)

### 基础参考教程

- [廖雪峰 Java 入门教程 (liaoxuefeng.com)](https://www.liaoxuefeng.com/wiki/1252599548343744) **不建议跟着用 Eclipse，关于 IDE 的推荐具体看下面的提示**
- [编程狮](https://www.w3cschool.cn/java/)
- [个人的学习笔记](https://harveyblocks.github.io/personal-space/Java/blog). **不建议跟着学, 因为是只是我个人做的笔记, 只针对我个人的情况. 思路比较零散, 语言比较碎片化, 没有做过整理. **

### 推荐书籍

- 《Head First Java》(图解多、讲的比较简单、内容不够全面，可以来读读提高兴趣)
- 《Java 核心技术卷 Ⅰ》
- 《Java 核心技术卷 Ⅱ》
- 《Thinking In Java》 (人称 Java 圣经(可能会有些难懂))](https://www.kotlincn.net/docs/reference/basic-syntax.html)

### 公开课

- [Main | CS 61B Spring 2021](https://sp21.datastructur.es/index.html) (很推荐各位在大一的时候多去刷刷公开课，拿 61b 来说，61b 使用 java 语言教学数据结构，他的前几轮 disc 是对 java 的 intro，很适合初学者入门)
- [CS 自学指南](https://csdiy.wiki/)