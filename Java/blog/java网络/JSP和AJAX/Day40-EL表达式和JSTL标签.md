# EL表达式

>   Expression Language 表达式语言

-   用于简化JSP内的Java代码

## 语法

```jsp
${expresion}
```

-   获取域中存储键为expresion的值

-   Servlet0.java

    ```java
    package com.harvey.response;

    import ...

    /**
     * ...
     */
    @WebServlet(value = "/Servlet0")
    public class Servlet0 extends HttpServlet {
        @Override
        protected void doGet(...,...)throws ... {
            this.parseWebString(request, response);
        }
        protected void parseWebString(...,...)throws ... {
            List<Brand> brands = new ArrayList<>();
            brands.add(new Brand(1,"三只松鼠","三只松鼠食品有限公司",100,"三只松鼠,好吃不上火",true));
            brands.add(new Brand(2,"优衣库","优衣库",200,"优衣库,服适人生",true));
            brands.add(new Brand(3,"小米","小米科技有限公司",1000,"为发烧而生",true));
            //存入request域中
            request.setAttribute("brands",brands);
            //请求转发
            request.getRequestDispatcher("/hello.jsp").forward(request,response);
        }
        @Override
        protected void doPost(){...}
    }
    ```

-   hello.jsp

```jsp
<%@ page pageEncoding="UTF-8" %>
<html>
<body>
    ${brands}
</body>
</html>
```

## JavaWeb四大域对象

-   page
    -   当前页面有效
    -   太小没用
-   **request**
    -   **当前请求有效**
-   **session**
    -   **当前会话有效**
-   application
    -   当前应用有效
    -   太大了不用

![image-20231118221923354](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/JSP和AJAX/Day40-EL表达式和JSTL标签/image-20231118221923354.png)

***EL表达式获取数据,会依次从范围小到范围大的四个域中寻找,直到找到为止***

# JSTL标签

>   Jsp Standarded Tag Library JSP标准标签库 

-   使用标签取代JSP页面上的Java代码

[标签](https://www.runoob.com/jsp/jsp-jstl.html)

## 导入坐标

```xml
<dependency>
    <groupId>jstl</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>
<dependency>
    <groupId>taglibs</groupId>
    <artifactId>standard</artifactId>
    <version>1.1.2</version>
</dependency>
```

## 引入前缀

```jsp
<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
```

## if标签

```jsp
<c:if test="<boolean>" var="<string>" scope="<string>">
   ...
</c:if>
```

```jsp
<c:if test="true">
    <h1>true</h1>
</c:if>
<c:if test="false">
    <h1>false</h1>
</c:if>
```

-   boolean也是true/false啊

```jsp
<c:if test="${salary > 2000}"><!--使用"=="-->
   <p>我的工资为: <c:out value="${salary}"/><p>
</c:if>
```

## forEach标签

```jsp
<c:forEach
    items="<object>"
    begin="<int>"
    end="<int>"
    step="<int>"
    var="<string>"
    varStatus="<string>">

    ...
```

```jsp
<c:forEach var="i" begin="1" end="5">
   Item <c:out value="${i}"/><p>
</c:forEach>
```

```jsp
<c:forEach items="${brands}" var="brand">
    ${brand}<br><%--输出toString()--%>
    ${brand.toString()}<br><%--正常输出toString--%>
    ${brand.aadshjkcxz}<br><%--构造jsp时错误--%>
    ${brand.id}<br><%--Tomcat底层拆字符串,把id转换成getId(),获取到id--%>
</c:forEach>
```

```jsp
<c:forEach items="${brands}" var="brand" varStatus="i">
    <%--${brand.id}<br>这个id不具有实际意义,不好--%>
    ${i.index}<%--代表从0开始--%><br>
    ${i.count}<%--代表从1开始--%><br>
    ${brand.productName}<br>

</c:forEach>
```

