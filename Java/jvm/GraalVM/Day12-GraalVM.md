# GraalVM

GraalVM是Oracle推出的高性能JDK

[官网](https://www.graalvm.org)

```shell
# GraalVM JDK with Native Image
docker pull container-registry.oracle.com/graalvm/native-image:17

# GraalVM JDK without Native Image
docker pull container-registry.oracle.com/graalvm/jdk:17
```

现在已经可以到JDK22了

可以通过Truffle框架跑JS, Python, Ruby

Linux上的GraalVM功能更全, Windows的不全, 其实自己开发的时候用什么都无所谓, 真上线了用GraalVM就行

查看CPU架构

```shell
arch
```

在SpingBoot3全面支持了GraalVM

## 模式

### JIT模式

>   Just In Time

-   Write One, Run Anywhere
-   预热之后, 通过Graal即时编译器优化热点代码
-   生成比Hotspot JIT更高性能的机器码



关闭GraalVM中的Graal编译器

```shell
-XX:UseJVMCICompiler
```

### AOT模式

>   Ahead Of Time

提前编译模式, 通过源代码,为特定频台创建可执行文件

例如Windows下生成exe文件. 可以获得高性能但**不具备跨平台性**

这种模式生成的文件称为==native-image==

#### 使用步骤

1.  安装本地镜像需要的[库](https://www.graalvm.org/latest/reference-manual/native-image/)

    ```shell
    To use Native Image on Windows, install Visual Studio 2022 version 17.6.0 or later, and Microsoft Visual C++ (MSVC). 
    There are two installation options:
    * Install the Visual Studio Build Tools with the Windows 11 SDK (or later version)
    * Install Visual Studio with the Windows 11 SDK (or later version)
    
    Native Image runs in both a PowerShell or Command Prompt and will automatically set up build environments on Windows, given that it can find a suitable Visual Studio installation.
    ```

    

2.  制作Class字节码文件

    ```shell
    javac 类名.java
    ```

3.  使用程序`native-image`来制作本地镜像

    Linux

    ```shell
    graalvm/bin/native-image 类名
    ```

    ```shell
    native-image [options] -jar jarfile [imagename]
    ```

    windows

    ```shell
    # Ring the terminal bell (press Ctrl+G to enter ^G)
    native-image.exe -jar App.jar & echo ^G
    
    # Open an info dialog box with text
    native-image.exe -jar App.jar & msg "%username%" GraalVM Native Image build completed
    ```

    

4.  运行本地镜像(可以脱离JDK, 在没有任何配置的机器上运行)

### 存在问题

-   跨平台问题
-   使用框架后编译时间长
-   AOT在编译时需要知道可访问的所有类, 但反射和动态代理需要在运行时创建
    -   反射和动态代理在Spring中被大量使用

### Spring

对于复杂的项目和框架(例如Spring)编译时间会很长且很占用内存资源

生成Spring3项目, 加入依赖**GraalVM Native Support**

编译插件

```shell
mvn -Pnative clean native:compile
```

