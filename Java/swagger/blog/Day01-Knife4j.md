# Knife4j

>国产, 好使

## 引入依赖

```xml
<dependency>
	<groupId>com.github.xiaoymin</groupId>
	<artifactId>knife4j-openapi2-spring-boot-starter</artifactId>
	<version>4.1.0</version>
</dependency>
```

## 启动项注解

```java
@EnableSwagger2WebMvc
```

因为整合的版本是2.10.5的Springfox`@EnableSwagger2`已经不再使用

## 配置类和注解

>   完全一致

## 配置

>   取代配置类和启动注解

```yaml
knife4j:
  enable: true # 代替启动注解
  # 代替配置类
  openapi:
    title: 评分系统管理接口文档
    description: "评分系统管理接口文档"
    email: harvey.blocks@outlook.com
    concat: Harvey Blocks
    url: https://www.baidu.com
    version: v1.0.0
    group:
      default:
        group-name: default
        api-rule: package
        api-rule-resources:
          - com.harvey.review_system.controller
```

## Interceptor过滤

```java
"/webjars/**",
"/favicon.ico",
"/error",
"/swagger-resources",
"/doc.html",
"/doc.html#/**",
```

