# 对象的内存布局

>   指对象在堆中存放时的各个组成部分

```java
public class Student{
    private long id; // 8个字节
    private byte age; // 1个字节
    private String name; // 引用对象, 指针在64位电脑8个字节
}
```

## 指针压缩

**JVM中以小端存储**

64位的计算机, 一个指针32位, 太长了吧

开启指针压缩会将堆中**原本8个字节的指针压缩为4个字节**

默认开启指针压缩

关闭指针压缩

```shell
-XX:-UseCompressedOops
```

### 原理

将寻址的单位放大

原本两个指针之间的最小单位是1个字节, 现在可以按8字节寻址, 以8个字节为一个单位

###存在问题

Q: 如果有数据类型的占用空间小于8个字节, 则为之奈何?

A: 对象本来就是要内存对齐到8的倍数的, 至于基本数据类型? 

​	 至于基本数据类型? 根本没有指向基本数据类型的指针啊! Java不提供啊!

Q: 指针压缩之前, 8个字节做指针, 可以指向2^64^=16EB, 4个字节做指针, 8×2^32^=32GB的内存空间, 超出上限, 则为之奈何?

A: 指针压缩技术会关闭, 好消极, 悲







## JOL

使用Unsafe, JVMTI, Serviceability Agent(SA)等虚拟机技术来打印实际的对象内存布局

```xml
<dependencecy>
	<groupId>org.openjdk.jol</groupId>
    <artifactId>jol-core</artifactId>
    <version>0.9</version>
</dependencecy>
```

```java
ClassLayout.parseInstance(obj).toPintable();
```

###查看对象内存布局

64为不开指针压缩

![image-20240601210854225](../asset/Day15-%E5%A0%86%E4%B8%8A%E7%9A%84%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8/image-20240601210854225.png)



## 内存对齐

###设计原因

内存对齐为了解决并发条件下CPU缓存失效的问题

数据在CPU中, 存在缓存行中, 一个缓存行长度为8个字节

如果不做内存对齐, 并行环境下, 一个缓存行中就有可能存储多个数据

对于CPU缓存-内存来说, 如果数据进行了更新, 采用更改内存中的数据, 删除CPU缓存数据的策略

CPU中的缓存数据, 就是一个缓存行一个缓存行删除的, 全然不考虑缓存行中的数据是有几个对象, 是不是能一起删除

当一个缓存行中存在多个数据而其中又有数据需要被删除时, 整个缓存行失效

对于这个缓存行中的其他不需要被删除的数据来说, 发现该缓存行失效, 就阻塞, 重新从内存加载数据

### 字段重排列

在Hotspot中, 要求

-   每个属性偏移量Offset(字段地址-起始地址)必须是字段长度的N倍?????????

    ![image-20240601230625233](../asset/Day15-%E5%A0%86%E4%B8%8A%E7%9A%84%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8/image-20240601230625233.png)

    

    目的是在对象内部的数据之间不会出现同一个数据**跨CPU缓存行**存储的问题出现

    ![image-20240601231014616](../asset/Day15-%E5%A0%86%E4%B8%8A%E7%9A%84%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8/image-20240601231014616.png)

-   引用数据类型一定要在基本数据类型之后

-   综上两点要同时实现, 就有可能出现不只是在对象最后有对齐, 也有可能在中间也有对齐

    ![image-20240601231324467](../asset/Day15-%E5%A0%86%E4%B8%8A%E7%9A%84%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8/image-20240601231324467.png)

### 子类的内存偏移

子类继承父类, 就会完全保存一份父类的字段和数据, 属性和偏移量是一样的

```java
class A {
    long l;
    int i;
    String name;
}

class B extends A {
    long l;
    int i;
}

class C{
    long l1;
    int i1;
    String name;
    long l2;
    int i2;
}
```

![image-20240601231829654](../asset/Day15-%E5%A0%86%E4%B8%8A%E7%9A%84%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8/image-20240601231829654.png)

##对象头

-   标记字段
    -   Mark World
    -   锁, 垃圾回收等特定功能需要的数据
    -   在32位操作系统中4字节, 在64位系统中8字节
-   元数据的指针
    -   Klass Pointer
    -   指向方法区中的`InstanceKlass`, 包含当前对象的类对象
    -   在32位操作系统中4字节, 在64位系统中8字节
-   length
    -   对于数组对象存在的字段, 普通对象没有
    -   按照int存储



### 标记字段

在32位和64位布局不同, 其中64位又分有是否指针压缩

以64位开启指针压缩为例:

![image-20240601204226570](../asset/Day15-%E5%A0%86%E4%B8%8A%E7%9A%84%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8/image-20240601204226570.png)

以64位关闭指针压缩为例

![image-20240601211932840](../asset/Day15-%E5%A0%86%E4%B8%8A%E7%9A%84%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8/image-20240601211932840.png)

32位虚拟机

![image-20240601211952277](../asset/Day15-%E5%A0%86%E4%B8%8A%E7%9A%84%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8/image-20240601211952277.png)



####HashCode

```java
System.out.println(Integer.toBinaryString(student.hashCode()));
```

```
111_1100_1100_0011_0101_0101_1011_1110
```

###Klass Pointer

利用JOL打印对象的Klass Pointer

使用Klass Pointer的地址, 在hsdb(JDK11不可以使用)中使用Inspector找到InstanceKlass对象









##对象数据

-   实际字段
-   内容对齐填充
    -   