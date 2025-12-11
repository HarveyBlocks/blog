# 配置文件加载顺序

## 内部配置

>   项目内部的配置文件的加载顺序

1.  file:./config/application.properties

    当前**项目**的/config目录下(不会打进Jar包)

    名字不对(不是`application{-profiles}.properties`)需要命令行参数

2.  file:./application.properties

    当前项目的根目录(不会打进Jar包)

3.  classpath:/config/:

    classpath的config目录

4.  classpath:/

    classpath的根目录

*resources目录以后会打包成classpath目录*

*对于相同的属性会有加载顺序之别, 不同的属性依旧会加载*

## 外部配置

[Spring官网spring-boot配置文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)

![image-20231206000313637](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-boot/配置/Day02-配置生效顺序/image-20231206000313637.png)

命令行优先级4(比内部配置都高)

```bash
java -jar .\spring-boot-0.0.1-SNAPSHOT.jar --server.port=8083
```

```bash
java -jar .\spring-boot-0.0.1-SNAPSHOT.jar --server.port=8083 --server.servlet.context-path=/hi
```

```bash
java -jar .\spring-boot-0.0.1-SNAPSHOT.jar --spring.config.location=d://application.properties
```

使用和jar包同级目录下的文件

`application.properties`被自动读取

## 内部和外部文件配置的意义

内部文件的配置不尽人意时,在外部配置文件.覆盖掉内部的配置文件

内外部的文件形成互补

