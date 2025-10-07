# 方法区

线程共享



-   元信息
    -   类的基本信息
-   运行时常量池
-   字符串常量池
    -   不同版本, 字符串常量池的存储位置多有改动

方法区是《Java虚拟机规范》中设计的虚拟概念, 每款Java虚拟机在实现上各有不同

对于HotSpot虚拟机

-   JDK7之前的版本, 方法区存放在**堆区域的永久代空间(ps_perm_gen)**, 堆的大小由虚拟机参数来配置
-   JDK8之后的版本, 方法区存放在**元空间(maetaspace)**中, 元空间位于操作系统的直接内存中, 默认情况下只要布草过操作系统的承受上限, 可以一直分配

##限制元空间大小

防止将机器的内存全部占满

```shell
-XX:MaxMetaSpaceSize=256M
```

大概数十万个类的加载

## 元信息

>   Mete Data

一般称之为`InstanceKlass`, 在类的加载阶段完成

-   类名等类信息
-   常量引用
-   类方法引用
-   类字段引用
-   虚方法表(用于多态)

## 运行时常量池

字节码文件中通过诸如`#6`的编号查表的方式找到常量, 此乃 **静态常量池**

常量池加载到内存中之后, 应当通过内存地址来定位常量池中的内容, 此乃 **运行时常量池**

##方法区溢出

ByteBuddy框架, 动态生成字节码数据, 加载到内存中, 不断死循环加载到方法区

引入依赖

```xml
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy</artifactId>
    <version>1.12.23</version>
</dependency>
```

```java
String name = "Class" + count;
ClassWriter classWriter = new ClassWriter(0);
classWriter.visit(
        Opcodes.V1_8, Opcodes.ACC_PUBLIC, name,
        null/*signature*/,
        "java/lang/Object"/*super*/,
        null /*interface*/
);
byte[] bytes = classWriter.toByteArray();
myClassLoader.defineClass(name, bytes, 0, bytes-.length);
```

-   1.7之前`OutOfMemory: PerGen space`
-   1.8及之后, 打开任务管理器, 逐渐炸裂qwq



## 字符串常量池

>   String Table

存储常量字符串等

```java
String s1 = new String("abc");// heap
String s2 = "abc";  // string table
```



-   JDK7之前
    -   字符串常量池属于运行时常量池的一部分, 存储的位置一致, 可以存储类名常量或整数常量
    -   即在永久带
-   JDK7
    -   字符串常量池放入了堆中
    -   运行时常量池里的其他东西还在永久带
-   JDK8及以后
    -   字符串常量池还在堆
    -   运行时常量池里的东西放到了元空间

```java
String a = "1"; // string table
String b = "2"; // string table
String c = "12"; // string table
String d = a + b; // heap
System.out.println(d == c);// false
```

![image-20240518221220278](../asset/Day06-%E6%96%B9%E6%B3%95%E5%8C%BA/image-20240518221220278.png)

```java
String a = "12"; // string table
String b = "1" + "2"; //string table
System.out.println(a == b);// true
```

![image-20240518221520522](../asset/Day06-%E6%96%B9%E6%B3%95%E5%8C%BA/image-20240518221520522.png)

###将字符串放入字符串常量池

适合在文件或网络中读取到重复的内容

```java
String#intern()
```

不改变当前String对象的字符串存放位置返回字符串在字符串常量池中的位置

```java
String s1 = new StringBuilder().append("321").append("123").toString();

System.out.println(s1.intern() == s1);
System.out.println(s1.intern() == s1.intern());

 // JVM在启动时就会将java存入常量池
String s2 = new StringBuilder().append("ja").append("va").toString();

System.out.println(s2.intern() == s2);
```

-   JDK6中是false, false;
-   JDK8中是true, false;
    -   JDK7之后的版本由于字符串常量池在堆上, `intern()`会把第一次遇到的字符串**引用**放入字符串常量池
    -   要确保是第一次引用, 是不是每次都要遍历字符串常量池?

