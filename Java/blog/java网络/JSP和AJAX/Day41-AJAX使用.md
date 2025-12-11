1.  编写AjaxServlet,并使用response输出字符串

```java
package com.harvey.ajax;

import ...

/**
 * ...
 */
@WebServlet(value = "/ajaxServlet")
public class AjaxServlet extends HttpServlet {
    @Override
    protected void doGet(...){...}
    @Override
    protected void doPost(...){...}

    protected void parseWebString(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {
        // 响应数据
        response.getWriter().write("Hello AJAX");
    }
}
```

1.  创建XMLHttpRequest对象
2.  向服务器发送请求
3.  获取服务器的响应数据

![image-20231119224436304](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/JSP和AJAX/Day41-AJAX使用/image-20231119224436304.png)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

</body>
<script>
    //1.创建核心对象
    var xhttp = new XMLHttpRequest();
    //2.发送请求
    xhttp.open("GET", "http://localhost:8080/ajaxServlet");
    // 第一个参数,"GET"/"POST"
    // 第二个参数,写全路径,因为以后前后端不在同一个服务器
    // 第三个参数布尔值,true表示异步(默认),false表示同步
    xhttp.send();
    //3.获取响应
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            alert(this.responseText);
       }
    };

</script>
</html>
```

![image-20231119225422322](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/JSP和AJAX/Day41-AJAX使用/image-20231119225422322.png)

![image-20231119225507593](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/JSP和AJAX/Day41-AJAX使用/image-20231119225507593.png)

## 结果

![image-20231119225900393](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/JSP和AJAX/Day41-AJAX使用/image-20231119225900393.png)

### xhr - 异步请求

>   XML HTTP Request

