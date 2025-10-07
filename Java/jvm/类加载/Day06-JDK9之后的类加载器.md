# JDK9之后的类加载器

JDK8之前的版本中, 扩展类加载器和应用程序类加载器的源码位于`rt.jar`包中的`run.misc.Launcher.java`

##jmod

JDK9引入了module的概念, Java类会放到jmod文件, jmod会放在jmods的文件夹下

![image-20240518125932560](../asset/Day06-JDK9%E4%B9%8B%E5%90%8E%E7%9A%84%E7%B1%BB%E5%8A%A0%E8%BD%BD%E5%99%A8/image-20240518125932560.png)

## Bootstrap

启动类加载器不再使用c++编写, 用Java编写, 位于`jdk.internal.loader.ClassLoader`类中, 为`BootClassLoader`

`BootClassLoader`继承自BuiltinClassLoader, 实现从模块到要加载的字节码资源文件

**在JDK9之后的版本, 启动类加载器依然无法通过Java代码获取, 依旧返回null**

## PlatformClassLoader

扩展类加载器替换成了PlatformClassLoader(平台类加载器)

PlatformClassLoader遵循模块化方式加载字节码文件, 所以一继承关系从URLClassLoader编程了BuiltinCLassLoader

BuiltinClassLoader实现了从模块中加载字节码文件

PlatformClassLoader的存在更多的是为了与老版本的设计方案兼容, 本身没有什么特殊的逻辑