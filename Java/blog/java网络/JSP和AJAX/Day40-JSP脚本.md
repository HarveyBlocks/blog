# JSP脚本

>   JSP脚本用于在JSP页面中定义Java代码

## 三中JSP脚本

1.  `<%...%>`

    内容会直接放到`_jspSeervice()`方法中

2.  `<%=...%>`

    内容会放到`out.print()`中.作为out.print的参数,直接在页面上输出`<span>`包围的文本(就是纯文本)

3.  `<%!...%>`

    内容会放到`_jspService()`方法之外,被类直接包含

```jsp
<%@ page import="java.util.List" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.harvey.pojo.Brand" %>
<%@ page import="java.util.ArrayList" %>
<html>
<head>
    <title>Jsp</title>
</head>
<body>
<h2>Hello World!</h2>
<%
    //模拟查询数据库
    List<Brand> brands = new ArrayList<>();
    brands.add(new Brand(1,"三只松鼠","三只松鼠食品有限公司",100,"三只松鼠,好吃不上火",true));
    brands.add(new Brand(2,"优衣库","优衣库",200,"优衣库,服适人生",true));
    brands.add(new Brand(3,"小米","小米科技有限公司",1000,"为发烧而生",true));

    for (int i = 0; i < brands.size(); i++) {

        %><%=brands.get(i)+"\r\n"%><%
        //这里输出html信息
        //但是信息有很多,你不想写很多writer.write()
        %>
            <h4>Hello World!</h4>
            <h4>Hello World!</h4>
            <h4>Hello World!</h4>
            <h4>Hello World!</h4>
            <h4>------------</h4>
        <%
        //这样子截断,反正你已经看过底层了,也大概想想的出底层是怎样的离谱模样

    }

%>
</body>
</html>
```

