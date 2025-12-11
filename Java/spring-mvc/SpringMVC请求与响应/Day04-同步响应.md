# 数据响应

-   传统的同步方式
    -   转发
    -   重定向
-   前后端分离的异步方式
    -   前端Ajax技术+Restful风格技术
    -   服务端进行Json格式为主的数据交互

## 传统的同步响应

![image-20231128205640812](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day04-同步响应/image-20231128205640812.png)

-   请求资源转发

    ```java
    return "index.jsp";
    ```

    也可以写成

    ```java
    return "forward:/index.jsp";
    ```

    地址不会变

-   资源重定向(**redirect**)

    ```java
    return "redirect:/index.jsp";
    ```

    地址变了

    ![image-20231128205746031](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day04-同步响应/image-20231128205746031.png)

-   响应模型数据request.setAttribute

-   直接写数据给客户端response.write()

## ModelAndView

-   Spring-Mvc 的 ModelAndView

-    ModelAndView封装**模型数据和视图名**
    -   模型数据:在页面展示的实体JavaBean
    -   视图:要往哪转发 

```java
//转发携带数据
@GetMapping("/res3")
public ModelAndView res3(ModelAndView modelAndView) {
    // 1. 设置模型数据
    User user = new User();
    user.setAge(12);
    user.setUsername("萨克洛夫");
    modelAndView.addObject("user",user);
    // 2. 设置视图名称
    modelAndView.setViewName("/index.jsp");
    // 3. 设置视图,在界面上显示模型
    return modelAndView;
}
@GetMapping("/res3_1")
public ModelAndView res3() {
    ModelAndView modelAndView = new ModelAndView();
    // 这样是和上面一样的
}
```

-   index.jsp

    ```jsp
    <p>
        <span>转发显示的模型数据是:</span>
        <span>${user.username}'s age is ${user.age}.</span
    </p>
    ```

-   测试启动

### 这里遇到EL表达式无法解析的问题

这是因为Servlet2.3之前的版本默认**忽略了EL表达式**

```jsp
<%@page isELIgnored="false" %>
```

-   这样设置让他解析EL表达式

-   也可以改变web-app的版本

    -   在web.xml设置

        ```xml
        <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xmlns="http://java.sun.com/xml/ns/javaee"
                 xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
                 http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
                 version="2.5">
        ```

    -   这样就不用给每个jsp文件前写一句话了

顺带着的,把utf-8的问题解决下

-   在web.xml文件里配置

    ```xml
    <!--配置所有jsp文件编码格式为utf-8-->
    <jsp-config>
        <jsp-property-group>
            <url-pattern>*.jsp</url-pattern>
            <page-encoding>UTF-8</page-encoding>
        </jsp-property-group>
    </jsp-config>
    ```

## @ResponseBody

>   作用: 告诉SpringMVC但会的字符串不是是视图名,是以响应体的方式响应的数据

### 标注在方法上

```java
//直接回写字符串
@GetMapping("/res4")
@ResponseBody
public String res4() {
    String string = "回写的字符串";
    return string;
}
```

-   没法显示中文,悲

### 标注在类上

>   该类内所有方法都将被指定以响应体的形式返回

```java
@Controller
@ResponseBody
public class MyResponse{

}
```

## @RestController

-   你这样,每个类都要注俩不觉得太麻烦了吗?

    `@RestController`一注,就顶俩

    ```java
    @RestController
    public class MyResponse {
        ...
    }
    ```

### 康康源码

![image-20231128235627206](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day04-同步响应/image-20231128235627206.png)

