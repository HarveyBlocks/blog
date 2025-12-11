# 模块拆分

## 模块熟悉

![image-20240106143654296](../../assets/Day02-%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%8B%86%E5%88%86/image-20240106143654296.png)



## 服务拆分原则

### 什么时候拆分

-   创业型项目
    -   成本低
    -   先采用单体架构, **快速开发**, 快速试错
    -   快速停止止损, 换个赛道
    -   随着规模扩大, 逐渐拆分
-   缺点的大项目
    -   资金充足, 目标明确, 可以直接选择微服务架构
    -   避免后续拆分的麻烦

### 怎么拆分

-   拆分目标
    -   高内聚
        -   单一职责
        -   包含业务相互关联度高, 完整度高
    -   低耦合
        -   每个微服务的功能相对独立
        -   尽量**减少**对其他微服务的依赖 
-   拆分方式
    -   横向拆分
        -   按照业务模块拆分
    -   纵向拆分
        -   抽取公共服务,提高复用性
        -   例如记录日志的风控分析
        -   例如短信发送的功能, 支付, 登录都需要短信提示

## 拆分实践

### 1. 决定依赖

#### 父工程

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.12</version>
    <relativePath/>
</parent>

<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <org.projectlombok.version>1.18.20</org.projectlombok.version>
    <spring-cloud.version>2021.0.3</spring-cloud.version>
    <spring-cloud-alibaba.version>2021.0.4.0</spring-cloud-alibaba.version>
    <mybatis-plus.version>3.4.3</mybatis-plus.version>
    <hutool.version>5.8.11</hutool.version>
    <mysql.version>8.0.23</mysql.version>
</properties>

<!-- 对依赖包进行管理 -->
<dependencyManagement>
    <dependencies>
        <!--spring cloud-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!--spring cloud alibaba-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>${spring-cloud-alibaba.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!-- 数据库驱动包管理 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        <!-- mybatis plus 管理 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <!--hutool工具包-->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>${hutool.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
<dependencies>
    <!--服务注册-->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <!-- lombok 管理 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${org.projectlombok.version}</version>
    </dependency>
    <!--单元测试-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>11</source> <!-- depending on your project -->
                    <target>11</target> <!-- depending on your project -->
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

#### 子工程

```xml
<parent>
    <groupId>com.heima</groupId>
    <artifactId>hmall</artifactId>
    <version>1.0.0</version>
</parent>
<dependencies>
    <!--common-->
    <dependency>
        <groupId>com.heima</groupId>
        <artifactId>hm-common</artifactId>
        <version>1.0.0</version>
    </dependency>
    <!--web-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!--数据库-->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    <!--mybatis-->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>

</dependencies>
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

-   从原来的单体项目里拷贝, 再一个一个的删

```xml
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

spring的打包插件, 把包打成FatJar, Jar包才能直接运行

### 2. 准备启动类

```java
package com.hmall.item;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.hmall.item.mapper")
@SpringBootApplication
public class ItemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class, args);
    }

}
```

### 3. 准备各种包

-   我选择原项目全部拷贝,再一个一个地删

### 4. 准备配置文件

1.  **改端口**

2.  给当前微服务起个名字

    ```yml
    spring:
      application:
        name: item-service
    ```

3.  微服务要独立, 数据库也要独立, 数据隔离

    一个服务独享一台mysql(我的垃圾电脑撑不住啦)

    ```yml
    spring:
      datasource:
        url: jdbc:mysql://${hm.item.db.host}:3306/hm_item?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
        driver-class-name: com.mysql.cj.jdbc.Driver
        username: root
        password: ${hm.item.db.pw}
    ```

    

### 5.代码

#### domain

#### DAO

#### Service

![image-20240106200647528](../../assets/Day02-%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%8B%86%E5%88%86/image-20240106200647528.png)

代码中的包路径得换(话说路径不是应该配成常量吗)

![image-20240106204827858](../../assets/Day02-%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%8B%86%E5%88%86/image-20240106204827858.png)

对别的服务产生了依赖

#### Controller

#### Config

#### utils

### 远程调用

