# Maven

-   项目管理工具
-   将项目开发和管理过程抽象成一个形目对象模型
-   POM(Project Object Model ):项目对象模型

![image-20231015112533228](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015112533228.png)

-   蓝方框之内是Maven
-   蓝虚线之上是我们要做的
-   蓝虚线之下是maven做好的

## 仓库

### 本地仓库

-   本机

### 远程仓库

#### 私服

-   公司的
-   不一定开源-有版权的
-   访问速度快

#### 中央仓库

-   在国外
-   开源的

[Central Repository: (maven.org)](https://repo1.maven.org/maven2/)

### 镜像仓库

## 坐标

文件吧

[Central Repository: (maven.org)](https://repo1.maven.org/maven2/)

### 坐标组成

-   groupId
    -   组织ID 
    -   定义当前Maven项目隶属目录
    -   通常是域名反写 org.mybatis
-   artifactId  
    -   项目ID
    -   定义当前Maven项目名称
    -   通常是模块名称,例如CRM,SMS
-   version 
    -   版本号
-   packaging 
    -   打包方式

[仓库坐标查询](https://mvnrepository.com/)

### 坐标作用

使用唯一标识,唯一性地定位资源位置,通过标识可以将资源的识别与下载工作交给机器完成

## DOS命令

![image-20231015135254982](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015135254982.png)

### 插件

-   去一个空文件

![image-20231015141714496](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015141714496.png)

```Dos
mvn archetype:generate -DgroupId=com.harvey -DartifactId=web-project -DarchetypeArtifactId=maven-archetype-webapp -Dversion=0.0.1-snapshop -DinteractiveMode=false
```

-   Web工程

## 装插件

-   mybatis不是插件,只是我不知道有哪些插件

```xml
<!--构建-->
<build>
  <!--设置插件-->
  <plugins>
    <!--具体的插件位置-->
    <plugin>
      <!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.5.6</version>
    </plugin>
  </plugins>
</build>
```

## 依赖

### 配置依赖

-   注入jar包?,这个部分:

```xml
<dependencies>

    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>

    <dependency>
      <!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.5.6</version>
    </dependency>

  </dependencies>

```

![image-20231015155312631](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015155312631.png)

### 可选依赖

![image-20231015155248895](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015155248895.png)

### 排除依赖

![image-20231015155603445](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015155603445.png)

-   不写版本,所有版本统统去掉

### 依赖范围

```xml
<dependencies>
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>3.8.1</version>
    <scope>test</scope><!--这一部分-->
  </dependency>
</dependencies>
```

![image-20231015160042990](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015160042990.png)

-   依赖范围的传递性

![image-20231015161032456](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015161032456.png)

-   这里的间接依赖是指子,指被依赖的,又依赖了jar包的
-   这里的直接依赖是指父,是依赖别人的 

## 生命周期

![image-20231015161606980](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015161606980.png)

1.  clean
    -   pre-clean
    -   ...
2.  default
    -   上表
3.  site

![image-20231015161822213](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231015161822213.png)

例如:执行test,前面全会执行

### 依赖冲突

![image-20231202215555984](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/maven/Day01-maven概述/image-20231202215555984.png)

-   只要指定依赖范围为**provide**即可

### 插件

-   插件与对应生命周期绑定
-   默认插件又其绑定功能
-   通过插件可以自定义其他功能

## 打Jar包

装插件

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>1.4</version>
            <configuration>
                <createDependencyReducedPom>true</createDependencyReducedPom>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <transformers>
                            <transformer
                                    implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>org.harvey.juc.Main</mainClass>
                            </transformer>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

