# Request&Response

![image-20231117180624023](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231117180624023.png)

-   请求数据就是一个字符串嘛
-   都是字符串嘛

```java
@WebServlet(urlPatterns = {"/application","/Application"})//精准匹配
public class Application extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("It's my doGet...");
        String name = request.getParameter("name");//url?name=Mike

        response.setHeader("content-type","text/html;charset=utf-8");
        response.getWriter().write("<h1>"+"你好呀,"+name+"!"+"</h1>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("It's my doPost...");
    }
}
```

![image-20231117181708837](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231117181708837.png)

# Request

## 继承体系

-   ServletRequest
    -   Java提供的请求对象根接口
-   HttpServletRequest
    -   Java提供的对Http协议分装的请求对象接口
-   RequestFacade
    -   Tomecat定义的实现类

## 获取请求数据

-   Htttp请求数据
    -   请求行
    -   请求头
    -   请求体

### 请求行

```http
GET/request-demo/req1?username=Mike HTTP/1.1
```

![image-20231117182818643](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231117182818643.png)

```java
String method = request.getMethod();
System.out.println(method);

String contextPath = request.getContextPath();
System.out.println(contextPath);

StringBuffer requestURL = request.getRequestURL();
System.out.println(requestURL);

String requestURI = request.getRequestURI();
System.out.println(requestURI);

String queryString = request.getQueryString();
System.out.println(queryString);
```

```
GET

http://localhost:8080/application
/application
name=Mike
```

-   我设置虚拟目录是**/**,它就不给显示contextPath了,很合理

### 请求头(GET)

```http
GET/request-demo/req1?username=Mike HTTP/1.1
User-Agent:Mozilla/5.0 Chrome/91.0.4472.106
```

```java
String UserAgent = request.getHeader("User-Agent//根据键获得值
System.out.println(UserAgent);
```

```http
GET/request-demo/req1?username=Mike HTTP/1.1
User-Agent:Mozilla/5.0 Chrome/91.0.4472.106
username=superbaby&password=123
```

```java
String header = request.getHeader("user-agent");//浏览器版本
System.out.println(header);
```

```
Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36
```

### 请求体(POST)

![image-20231117183720581](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231117183720581.png)

```java
@Override
protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    BufferedReader reader = request.getReader();
    System.out.println(reader.readLine());//读取数据

}
```

![image-20231117185016955](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231117185016955.png)

### 通过统一的方式获取请求参数

-   GET和POST的获取参数方式不一样
-   但是原理和数据处理全是拆字符串
-   如果要写两份代码,.....

-   **Request会把键值对存入Map集合**
    -   重复的键有多个值的,会形成一个数组

```java
Map<String,String[]>
```

-   方法

    ![image-20231117205435672](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231117205435672.png)

    ```java
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        // 获取所有参数的Map集合
        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((k,v)->System.out.println(k+"\t= "+ Arrays.toString(v)));
    }
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        this.doGet(request,response);
    }
    ```

![image-20231117211013430](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231117211013430.png)

-   底层还是朴素的BufferedReader(POST)和QueryString(GET)

### 解决中文数据乱码

-   POST

    ```java
    request.setCharacterEncoding("UTF-8");//设置字符输入流的编码
    ```

-   GET

    -   通过URL传输数据,但是嘞,浏览器也不懂中文,浏览器直接就把信息转成**URL编码**了

        ![image-20231117214851924](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231117214851924.png)

        所以,我们要做的不是给Java高七搞八,而是编写进行**URL解码**

    -   ![image-20231117234358326](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231117234358326.png)

        二进制一样的,懂了吧?

        ```java
        @Test
        public void test2() throws UnsupportedEncodingException {
            String s = "你好";
            //模拟在浏览器输入数据

            String encode = java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
            //模拟浏览器的操作:将UTF-8转URL
            System.out.println(encode);

            String decode = java.net.URLDecoder.decode(encode, StandardCharsets.ISO_8859_1);
            //模拟将Tomcat的默认操作:URL转ISO_8859_1
            System.out.println(decode);

            //解决乱码问题
            byte[] bytes = decode.getBytes(StandardCharsets.ISO_8859_1);
            //依据ISO_8859_1,将看不懂的中文转换成字节数组
            String str = new String(bytes, StandardCharsets.UTF_8);
            //字节数组依据UTF-8重新编码
            System.out.println(str);
        }
        ```

    -   Tomcat8之后换成UTF-8之后就把这个解决了啦,人家用了UTF-8了啦

    -   人家已经写了解码编码的该怎么办了啦

## 请求转发

-   服务器内部的资源跳转的方式

1.  资源A接收到浏览器的请求,将请求做了一部分处理
2.  **将request的处理转给了资源器B**
3.  资源B又依据request做了一些处理,将书记response给了浏览器

![image-20231118131517789](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231118131517789.png)

-   实现方法:

    -   资源A

        ```java
        request.getRequestDispatcher("资源B路径").forward(request,response)
        ```

    -   资源B:

        ```java
        @WebServlet(value = "/资源B路径")
        public class MyServlet extends HttpServlet{
            @Override
            protected void doGet(
                    HttpServletRequest request,
                    HttpServletResponse response)
                    throws ServletException, IOException {
                System.out.println("这里是资源B哒哒哒");
                this.parseWebString(request,response);
            }
            ...
        }
        ```

### 特点

-   浏览器地址栏路径不发生变化
-   只能转到服务器的内部
-   一次请求,可以在转发的资源键是用request共享数据

### 共享资源

![image-20231118001306302](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/java网络/网络/Day39-请求Request/image-20231118001306302.png)

-   是键值对哒

-   浏览器上路径不发生变化(**服务器**内部的变化)
-   不能转到百度(服务器**内部**的变化)
-   一次请求可以再转发的资源间是用request**共享数据**

