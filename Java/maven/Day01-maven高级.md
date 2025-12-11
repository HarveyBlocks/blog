## 工程模块与模块划分

-   从前

    ```txt
    C:\USERS\27970\DESKTOP\SPRINGMVC
    |   pom.xml
    |
    +---.idea
    +---src
    |   +---main
    |   |   +---java
    |   |   |   \---com
    |   |   |       \---harvey
    |   |   |           +---controller
    |   |   |           +---dao
    |   |   |           +---domain
    |   |   |           +---service
    |   |   |           \---system
    |   |   |               \---exception
    |   |   +---resources
    |   |   \---webapp
    |   \---test
    |       +---java
    |       \---resources
    \---target
    ```

    -   domain=pojo

    现在,这是一个PC端的系统,我想要移动端的系统,不需要把所有的dao啊,service啊重复开发一遍,用新的controller就行了,毕竟服务端逻辑和时客户端是移动端还是PC端关系不大

    所以,可以把controller提出来,专门做成一个项目

    同理,可以把MVC各层都拆开来(甚至更细)

一个工程多个包,每个包都可以拆成模块

-   靠接口通信
-   主模块什么都不放
-   所有工程做成独立的模块
-   注意,模块里的依赖和类要与这个模块的作用紧密相关,不能把所有东西一股脑地往里面加

### tomcat与maven

我用tomcat7的插件强行运行tomcat9, 严重警告但是能运行

```txt
严重: Unable to process Jar entry [META-INF/versions/9/module-info.class] from Jar [jar:file:/C:/Users/27970/Desktop/IT/JDK/ssm/mvc/target/Spring-mvc-quickstart/WEB-INF/lib/jackson-core-2.13.5.jar!/] for annotations
org.apache.tomcat.util.bcel.classfile.ClassFormatException: Invalid byte tag in constant pool: 19
	at org.apache.tomcat.util.bcel.classfile.Constant.readConstant(Constant.java:133)
	at org.apache.tomcat.util.bcel.classfile.ConstantPool.<init>(ConstantPool.java:60)
	at org.apache.tomcat.util.bcel.classfile.ClassParser.readConstantPool(ClassParser.java:209)
	at org.apache.tomcat.util.bcel.classfile.ClassParser.parse(ClassParser.java:119)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsStream(ContextConfig.java:2134)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsJar(ContextConfig.java:2010)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsUrl(ContextConfig.java:1976)
	at org.apache.catalina.startup.ContextConfig.processAnnotations(ContextConfig.java:1961)
	at org.apache.catalina.startup.ContextConfig.webConfig(ContextConfig.java:1319)
	at org.apache.catalina.startup.ContextConfig.configureStart(ContextConfig.java:878)
	at org.apache.catalina.startup.ContextConfig.lifecycleEvent(ContextConfig.java:376)
	at org.apache.catalina.util.LifecycleSupport.fireLifecycleEvent(LifecycleSupport.java:119)
	at org.apache.catalina.util.LifecycleBase.fireLifecycleEvent(LifecycleBase.java:90)
	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5322)
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:150)
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1559)
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1549)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:829)

12月 02, 2023 9:11:52 下午 org.apache.catalina.startup.ContextConfig processAnnotationsJar
严重: Unable to process Jar entry [module-info.class] from Jar [jar:file:/C:/Users/27970/Desktop/IT/JDK/ssm/mvc/target/Spring-mvc-quickstart/WEB-INF/lib/jackson-annotations-2.13.5.jar!/] for annotations
org.apache.tomcat.util.bcel.classfile.ClassFormatException: Invalid byte tag in constant pool: 19
	at org.apache.tomcat.util.bcel.classfile.Constant.readConstant(Constant.java:133)
	at org.apache.tomcat.util.bcel.classfile.ConstantPool.<init>(ConstantPool.java:60)
	at org.apache.tomcat.util.bcel.classfile.ClassParser.readConstantPool(ClassParser.java:209)
	at org.apache.tomcat.util.bcel.classfile.ClassParser.parse(ClassParser.java:119)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsStream(ContextConfig.java:2134)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsJar(ContextConfig.java:2010)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsUrl(ContextConfig.java:1976)
	at org.apache.catalina.startup.ContextConfig.processAnnotations(ContextConfig.java:1961)
	at org.apache.catalina.startup.ContextConfig.webConfig(ContextConfig.java:1319)
	at org.apache.catalina.startup.ContextConfig.configureStart(ContextConfig.java:878)
	at org.apache.catalina.startup.ContextConfig.lifecycleEvent(ContextConfig.java:376)
	at org.apache.catalina.util.LifecycleSupport.fireLifecycleEvent(LifecycleSupport.java:119)
	at org.apache.catalina.util.LifecycleBase.fireLifecycleEvent(LifecycleBase.java:90)
	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5322)
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:150)
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1559)
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1549)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:829)

12月 02, 2023 9:11:54 下午 org.apache.catalina.startup.ContextConfig processAnnotationsJar
严重: Unable to process Jar entry [META-INF/versions/9/module-info.class] from Jar [jar:file:/C:/Users/27970/Desktop/IT/JDK/ssm/mvc/target/Spring-mvc-quickstart/WEB-INF/lib/jackson-databind-2.13.5.jar!/] for annotations
org.apache.tomcat.util.bcel.classfile.ClassFormatException: Invalid byte tag in constant pool: 19
	at org.apache.tomcat.util.bcel.classfile.Constant.readConstant(Constant.java:133)
	at org.apache.tomcat.util.bcel.classfile.ConstantPool.<init>(ConstantPool.java:60)
	at org.apache.tomcat.util.bcel.classfile.ClassParser.readConstantPool(ClassParser.java:209)
	at org.apache.tomcat.util.bcel.classfile.ClassParser.parse(ClassParser.java:119)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsStream(ContextConfig.java:2134)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsJar(ContextConfig.java:2010)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsUrl(ContextConfig.java:1976)
	at org.apache.catalina.startup.ContextConfig.processAnnotations(ContextConfig.java:1961)
	at org.apache.catalina.startup.ContextConfig.webConfig(ContextConfig.java:1319)
	at org.apache.catalina.startup.ContextConfig.configureStart(ContextConfig.java:878)
	at org.apache.catalina.startup.ContextConfig.lifecycleEvent(ContextConfig.java:376)
	at org.apache.catalina.util.LifecycleSupport.fireLifecycleEvent(LifecycleSupport.java:119)
	at org.apache.catalina.util.LifecycleBase.fireLifecycleEvent(LifecycleBase.java:90)
	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5322)
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:150)
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1559)
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1549)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:829)

```

### pojo

-   domain一个包

### dao

把自己的资源项目当作资源引入

```xml
<!--导入资源文件pojo-->
<dependency>
    <groupId>com.harvey</groupId>
    <artifactId>mvc-pojo</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

![image-20231202173918669](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231202173918669.png)

把这里的复制过来

但没有这么简单,因为maven回去repositiry仓库找,找不到资源去中央仓库找,但是这个pojo不在上述任何一个地方,maven会找不到的

所以要**先用pojo的install插件**,把pojo上传了(**注意要上传jar包**,可能上传war包也行,但会麻烦,要配置一些别的东西,以后再说)

### service

### controller

这里如果是用xml文件配置,而需要的spring的配置文件有许多,再web.xml里配置spring的配置文件的时候,可以用模糊匹配

```xml
<context-param>
	<param-name>contextConfigLocation</param-name>
    <param-value>classpath*:applicationContext-*.xml</param-value>
</context-param>
```

可以匹配applicationContext-dao.xml和applicationContext-service.xml这种

## 聚合

### 多模块的构建维护

如果Dao模块更新,重新install了一次,其他模块知道吗?

->**用一个模块管理这些模块,一个更新全部更新**

这就叫**聚合**

### 聚合模块

>   **用一个模块管理被拆分的模块,一个更新全部更新**

-   聚合的工程不做其他的任何功能,仅仅管理其他工程

![image-20231203150138422](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203150138422.png)

-   没有代码,删除src

-   POM.xml

    ```xml
    <!--定义还工程用于进行构建管理-->
    <packaging>pom</packaging>
    <modules>
        <!--具体的工程名称-->
        <module>../pojo</module>
        <module>../dao</module>
        <module>../service</module>
        <module>../controller</module>
        <!--没有顺序,随便写-->
    </modules>
    ```

![image-20231203150736778](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203150736778.png)

```txt
[INFO] pojo ............................................... SUCCESS [  0.996 s]
[INFO] dao ................................................ SUCCESS [  2.108 s]
[INFO] service ............................................ SUCCESS [  1.907 s]
[INFO] controller Maven Webapp ............................ SUCCESS [  2.256 s]
[INFO] ssm ................................................ SUCCESS [  0.004 s]
```

-   对于这几个是线性关系,一条线的依赖,它会自动帮你拍好

## 继承

### 依赖冲突

>   service中需要依赖spring-context,使用版本5.2.0
>
>   dao中需要依赖spring-context,使用版本5.1.9
>
>   依赖冲突就此产生

-   一个新工程,管理这些工程的依赖,介于只有版本冲突会引发依赖冲突,被管理工程将**失去对版本的决定**,都交由总工程管理

![image-20231203161555356](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203161555356.png)

### 父工程与子工程

>   父工程引入的依赖子工程能用

#### 父工程

-   对依赖进行管理

    ```xml
    <!--声明此处进行依赖管理-->
    <dependencyManagement>
        <!--所有的依赖-->
        <dependencies>
            <!--具体的依赖-->

            <!--自己的依赖-->
            <dependency>
                <groupId>com.harvey</groupId>
                <artifactId>pojo</artifactId>
                <version>1.0-SNAPSHOT</version>
            </dependency>

            <!--test-->
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>4.13.2</version>
                <scope>test</scope>
            </dependency>

            ...
        </dependencies>
    </dependencyManagement>
    ```

-   对插件进行管理

    ```xml
    <!--插件管理-->
    <pluginManagement>
        <!--设置插件-->
        <plugins>
            <!--具体地插件-->
            <plugin>
                <groupId>org.apache.tomcat.maven</groupId>
                <artifactId>tomcat7-maven-plugin</artifactId>
                <version>2.2</version>
                <configuration>
                    <server>tomcat9</server>
                    <update>true</update>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
    ```

#### 子工程

-   定义该工程的父工程

    ```xml
    <parent>
        <!--理论上,父子工程应该有相同的群组ID-->
        <groupId>com.harvey</groupId>
        <artifactId>ssm</artifactId>
        <!--工程版本也应该与父工程保持一致-->
        <version>1.0-SNAPSHOT</version>
        <!--相对路径,填写父工程的项目文件-->
        <relativePath>../ssm/pom.xml</relativePath>
    </parent>
    ```

    由于父子工程有相同的群组id,**子工程的群组id可以不写**

    **工程版本同理**

-   将自己的依赖去掉版本

-   将自己的插件去掉版本和配置(对于tomcat-maven来说是port之类的)

### 注意

**启动的时候是启动父工程还是controller模块?**

controller模块!!!!!!!!!!

### 继承和聚合

可以在一个pom.xml也可以在两个

-   作用
    -   聚合用于快速构建项目
    -   继承用于快速配置
-   同
    -   打包方式均为pom
    -   均属于设计型模块,并无示例模块内容
-   异
    -   聚合在当前模块中配置关系, 可以感知到参组聚合的模块有哪些
    -   继承是在子模块中配置关系, 父模块无法感知到哪些模块继承自己

## 属性

### 版本同一问题

>   对于Spring全家桶里的依赖, 版本应该是一致的,但手贱不一致了呢?
>
>   一个地方改了版本,另一个地方没改,这不是要凉凉?

-   解决:属性(类似于变量)

### 自定义属性

```xml
<!--自定义属性-->

<properties>
    <!--名称可以随便命名-->
    <spring.version>5.3.29</spring.version>
    <!--一般是:技术名称.version-->

</properties>
```

### 引用属性

`${spring.version}`

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>${spring.version}</version>
    <scope>compile</scope>
</dependency>
```

### 内置属性

例如:`${version}`表示当前工程的版本

```xml
<dependency>
    <groupId>com.harvey</groupId>
    <artifactId>controller</artifactId>
    <version>${version}</version>
</dependency>
```

-   `${version}`,等价于`${projiect.version}`

-   `${settings.localReposity}`可以读到**setting.xml里的属性**值了

-     **系统属性**也可用如`${sun.jnu.encoding}`

    **环境变量属性**也可以用`${env.JAVA_HOME}`

    ```java
    public static void main(String[] args) {
        System.out.println("--------------System Properties-------------");
        System.getProperties()
                .forEach((k,v)-> System.out.println(k+"="+v));
        System.out.println("------------Environment Variables-------------");
        System.getenv()
                .forEach((k,v)-> System.out.println(k+"="+v));
    }
    ```

    其中一个属性是`sun.jnu.encoding:GBK`这样的

## Maven高级功能

### 版本管理

>   对于子工程,想要有不同与父工程的版本号,直接加\<version就好了\>

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.harvey</groupId>
        <artifactId>ssm</artifactId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../ssm/pom.xml</relativePath>
    </parent>

    <artifactId>pojo</artifactId>
    <!--例如这个样子,但还是分开来写的好,毕竟一个项目的版本不能等同一个模块的版本-->
    <version>${parent.version}</version>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

![image-20231203170026899](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203170026899.png)

-   SNAPSHOT
    -   快照版本
-   RELEASE
    -   发布版本
-   以及各个企业都用的不太一样

#### 工程版本号的约定

-   `<主版本>.<次版本>.<增量版本>.<里程碑版本>`

    -   主版本:项目重大架构的变更

        如:spring5相较于spring4的迭代

    -   次版本:有较大的功能增加和变化 , 或者全面地修复漏洞

    -   增量版本:表示有漏洞的修复

    -   里程碑版本:表明一个版本的内容。这样的版本同下一个这个是版相比不是很稳定，有待更多测试

-   例如:

    `5.1.9.RELEASE`

### 资源配置

>   想在POM.xml里配置jdbc.url

1.  spring.xml
2.  spring-mvc.xml
3.  jdbc.properties
4.  AccountMapper.xml

![image-20231203172553742](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203172553742.png)

-   pom.xml

```xml
<build>   
    ...

	<resources>
        <resource>
            <!--从pom.xml开始找-->
            <directory>../dao/src/main/resources</directory>
            <!--对配置资源的加载过滤-->
            <filtering>true</filtering>
        </resource>
    </resources>
</build>

<!--自定义属性-->
<properties>
    <jdbc.url>jdbc:mysql://localhost:3306/company</jdbc.url>

    ...
</properties>
```

-   jdbc.properties

```properties
jdbc.url=${jdbc.url}
```

![image-20231203173326014](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203173326014.png)

#### 动态配置路径

```xml
<directory>${project.basedir}/dao/src/main/resources</directory>
```

-   不用去想相对路径了
-   <span style="color:red">**注意,这个可能会不对!**经我测试${project.basedir}替代的路径就是pom.xml所在的目录,所以这个是无法找到指定的resources的了,我后面依旧用了**../**</span>

#### 配置test里面的resources

```xml
<!--配置资源文件对应的信息-->
<resources>
    <resource>
        <directory>${project.basedir}/dao/src/main/resources</directory>
        <filtering>true</filtering>
    </resource>
</resources>
<!--配置测试资源文件对应的信息-->
<testResources>
    <testResource>
        <directory>${project.basedir}/dao/src/test/resources</directory>
        <filtering>true</filtering>
    </testResource>
</testResources>
```

### 多环境开发配置

>   例如,我们在个人电脑上开发,测试的时候, 把最大线程写了5,因为我们的个人电脑比较逊
>
>   但是给服务器的线程也是5,这不是杀鸡用牛刀吗?
>
>   可是,把程序放到服务器之前要把所有配置全部改一遍, 想想就让人秃头....

-   生产环境
-   开发环境
-   测试环境

#### 创建多环境

```xml
<!--创建多环境-->
<profiles>
    <!--定义具体提的环境-->

    <!--生产环境-->
    <profile>
        <!--定义环境对应的唯一名称-->
        <id>produce_env</id>
        <!--定义环境中专用的属性值-->
        <properties>
            <jdbc.url>jdbc:mysql://localhost:3306/company</jdbc.url>
        </properties>
    </profile>

    <!--开发环境-->
    <profile>
        <!--定义环境对应的唯一名称-->
        <id>develop_env</id>
        <!--定义环境中专用的属性值-->
        <properties>
            <jdbc.url>jdbc:mysql://127.0.0.1:3306/company</jdbc.url>
        </properties>
    </profile>
</profiles>
```

![image-20231203185018362](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203185018362.png)

![image-20231203184951408](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203184951408.png)

#### 设置默认环境

```xml
<!--生产环境-->
<profile>
    <!--定义环境对应的唯一名称-->
    <id>produce_env</id>
    <!--定义环境中专用的属性值-->
    <properties>
        <jdbc.url>jdbc:mysql://localhost:3306/company</jdbc.url>
    </properties>

    <!--设置默认启动项-->
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
</profile>
```

### 跳过测试

>   就是我明知道这个测试会报错, 可能是我没做完, 我只关系部分功能是否正确
>
>   如果全部测试, install起来也要半天

***测试是很重要的 ! 知道跳过测试就行 , 不要滥用 !***

#### idea实现

![image-20231203195728412](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203195728412.png)

#### 指令实现

```bash
mvn -install -D skipTests
```

#### 配置实现

```xml
<profile>
    <id>test_env</id>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
                <configuration>
                    <!--设置跳过测试-->
                    <skipTests>true</skipTests>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

-   指定需要跳过的测试

    ```xml
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <!--包含-->
                <includes>
                    <!--任意包-->
                    <include>**/DaoTest.java</include>
                </includes>
                <!--排除-->
                <excludes>
                    <!--<exclude>**/ServiceTest.java</exclude>-->
                </excludes>
            </configuration>
        </plugin>
    </plugins>
    ```

    支持通配符

    `**/Service*Test.java`

## 私服

>   公共服务器建立关系

![image-20231203202103192](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203202103192.png)

>   注意把私服和中央的服务器区分开

私服的范围应该是公司的

服务器的范围应该是整个互联网的

**由于测试成本太高 , 私服这部分的内容没有被我测试过**

### Nexus

-   Nexus是Sonatype公司的一款maven私服产品

[下载地址](https://help.sonatype.com/repomanager3/product-information/download)

![image-20231203203144883](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203203144883.png)

![image-20231203203321049](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203203321049.png)

```bash
nexus /run 服务器名称
```

![image-20231203203507782](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203203507782.png)

-   成功

在浏览器上访问`localhost:8081`

![image-20231203203557241](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203203557241.png)

`D:\IT_study\maven\nexus\nexus-3.62.0-01\bin\nexus /run nexux`

#### 右上角登录

![image-20231203205327569](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203205327569.png)

跟着提示做

#### 修改访问服务器的配置

[修改配置的配置文件](D:\IT_study\maven\nexus\nexus-3.62.0-01\etc\nexus-default.properties)

可以配端口号和IP地址

![image-20231203203852410](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203203852410.png)

#### 服务器的相关配置

[服务器的配置文件](D:\IT_study\maven\nexus\nexus-3.62.0-01\bin\nexus.vmoptions)

![image-20231203204112380](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203204112380.png)

### 私服资源的获取

公司的项目资源从私服拿

依赖的资源从中央仓库拿

这样**不统一**啊

![image-20231203204356102](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203204356102.png)

#### 私服管理仓库

拓展一下私服的功能

![image-20231203204511355](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203204511355.png)

再建立一个仓库,管理测试版的

![image-20231203204635678](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203204635678.png)

仓库组管理仓库

![image-20231203204726931](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203204726931.png)

#### 仓库分类

-   宿主仓库hosted
    -   保存无法从中央仓库获取的资源
        -   自主研发
        -   非第三方开源项目
-   代理仓库proxy
    -   代理远程仓库 , 通过nexus访问其他公共仓库 , 例如中央仓库
-   仓库组group
    -   将若干个仓库组成一个群组 , 简化配置
    -   仓库组不能保存资源 , 属于设计仓库

### Idea与私服

Idea能连接本地仓库, 我们需要让本地仓库连接私服

上传需要:

1.  访问私服的用户名密码,配置到本地仓库
2.  上传位置(宿主地址),配置到项目(每个项目的上传地址不一样)

下载需要:

1.  访问用户名密码,配置到本地仓库
2.  下载的地址(组地址),配置到本地

598e0cd5-9ae2-482e-8877-57a0904167f8

#### 本地仓库访问私服

-   maven的setting.xml

```xml
<servers>
	<server>
    	<!--配置访问服务器的权限-->
    	<id>nexus-harvey-release</id>
    	<username>admin</username>
    	<password>密码</password>
	</server>
</servers>
```

```xml
<mirrors>
	<mirror>
    	<id>nexus-harvey</id>
    	<mirrorOf>*</mirrorOf>
    	<!--访问public最方便-->
    	<url>http://localhost:8081/repository/maven-public/</url>
    	<blocked>true</blocked>
	</mirror>
</mirrors>
```

然后可以把阿里云的干掉了(也可以不干掉)

#### 本地工程发布到私服

pom.xml

```xml
<!--发布配置管理-->
<distributionManagement>
    <!--1. 发布到谁上面去-->
    <repository>
        <!--id要对应-->
        <id>nexus-harvey-release</id>
        <!--要去localhost:8081上看-->
        <url>http://localhost:8081/repository/harvey-release/</url>
        <!--使用url发布项目, 但是需要密码,它就会使用id 对应的harvey-release找用户名密码-->
    </repository>

</distributionManagement>
```

然后使用deploy发布

![image-20231203223145557](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven高级/image-20231203223145557.png)

