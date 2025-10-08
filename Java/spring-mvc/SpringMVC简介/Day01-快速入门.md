# 步骤

## 写jsp文档

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
```

-   再写一遍,就怕你找不到了

## 导入SpringMVC坐标

```xml
<!--导入spring-mvc-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.29</version>
</dependency>
```

## 配置前端控制器DispatcherServlet

-   Web.xml

    ```xml
    <!DOCTYPE web-app PUBLIC
            "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
            "http://java.sun.com/dtd/web-app_2_3.dtd" >
    
    <web-app>
        <display-name>Archetype Created Web Application</display-name>
        <servlet>
            <servlet-name>DispatcherServlet</servlet-name>
            <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
            <!--初始化时,依据配置文件,创建容器-->
            <init-param>
                <param-name>contextConfigLocation</param-name>
                <param-value>classpath:spring-mvc.xml</param-value>
            </init-param>
        </servlet>
        <servlet-mapping>
            <servlet-name>DispatcherServlet</servlet-name>
            <url-pattern>/</url-pattern>
        </servlet-mapping>
    
    </web-app>
    ```

## 编写Controller

```java
package com.harvey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller//@Component的三个衍生注解,比Component更具备语义化
//交给Spring-MVC容器管理
public class QuickController {
    @RequestMapping("/show")
    public void show() {
        System.out.println(this.getClass().getSimpleName()+"::show()");
        return "/index.jsp";//返回前端文件的路径,其将跳转到前端文件
    }
}
```







### 配置映射地址

```java
@RequestMapping("/show")
```

### 交给SpringMVC的容器管理

```java
@Controller//@Component的三个衍生注解,比Component更具备语义化
//交给Spring-MVC容器管理
```





```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"

       xsi:schemaLocation=
               "http://www.springframework.org/schema/beans
                http://www.springframework.org/schema/beans/spring-beans.xsd
                http://www.springframework.org/schema/context
                http://www.springframework.org/schema/context/spring-context.xsd">
    <!--组件扫描-->
    <context:component-scan base-package="com.harvey"/>

</beans>
```

