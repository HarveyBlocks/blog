# 组成

![JVM组成](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/jvm/基础/Day01-组成/JVM组成.png)

## 类加载器

>   Class Loader

将字节码文件中的内容加载到内存(运行时数据区域)

## 运行时数据区域

存放类-对象的内存区域

## 执行引擎

执行内存中的代码

将类中的字节码指令解释成机器码

即时编译, 垃圾回收机制

## 本地接口

Java程序在执行时需要调用底层用C/C++实现的代码, 不存在字节码文件中

![image-20240512134215674](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/jvm/基础/Day01-组成/image-20240512134215674.png)

执行引擎会执行本地接口的方法, 本地接口也会在运行数据区域创建对象

