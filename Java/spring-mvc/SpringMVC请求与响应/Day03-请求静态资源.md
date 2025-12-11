# 请求静态资源

## 静态资源无法找到的问题

在不用Spring-mvc整合的java-web中,
tomcat的conf文件夹下有一个web.xml文件,
是对所有web项目生效的配置

![image-20231126204901443](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day03-请求静态资源/image-20231126204901443.png)

其中配置的DefaultServlet

![image-20231126204935431](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day03-请求静态资源/image-20231126204935431.png)

其url-pattern为**/**

![image-20231126205000760](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day03-请求静态资源/image-20231126205000760.png)

tomcat默认会把在url中的路径信息当成一个Servlet路径
先去Servlet中找,找不到了,就认为是静态资源,然后就去访问静态资源
url-pattern为/,/就是范围最大的"兜底"的路径

可是我们现在自己也配置了一个Spring-mvc的Servlet
DispatcherServlet前端控制器
url-pattern也为/
它会优先使用我们的web.xml的配置

![image-20231126205034484](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day03-请求静态资源/image-20231126205034484.png)

但是**DispatcherServlet没有加载静态资源的能力**
我们就无法加载静态资源了

-   无法访问静态资源?

    -   **不要使用index.jsp做这个实验**

    ![image-20231126205127822](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day03-请求静态资源/image-20231126205127822.png)

    tomcat默认了index.jsp为开始界面,是可以加载出来的静态资源

	-     对曾经已经加载过的静态资源,记得**清除浏览器缓存**
-     我使用了从未被使用的aaa.txt

![image-20231126204659672](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day03-请求静态资源/image-20231126204659672.png)

![image-20231126204647193](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day03-请求静态资源/image-20231126204647193.png)

## 解决静态资源无法加载的问题

### 法一:再次加载DefaultServlet

```xml
<!--再次激活DefaultServlet-->
<!--使用servlet-mapping,映射的对象是tomcat默认配置里的DefaultServlet-->
<servlet-mapping>
    <!--名字应该和默认配置里的一致,才能完成映射-->
    <servlet-name>default</servlet-name>
    <!--把url-pattern配置得更精确,使得不会去马上匹配/,而是加载静态资源-->
    <url-pattern>*.html</url-pattern>
    <!--后缀名匹配-->
</servlet-mapping>
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.txt</url-pattern>
</servlet-mapping>
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>/img/*</url-pattern>
    <!--文件路径匹配-->
</servlet-mapping>
```

-   虽然乱码,但找到资源了

![image-20231126210651667](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day03-请求静态资源/image-20231126210651667.png)

### 法二:Spring-MVC配置路径映射

![image-20231127195240423](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/Java/spring-mvc/SpringMVC请求与响应/Day03-请求静态资源/image-20231127195240423.png)

```xml
<!--配置静态资源的映射路径-->
<!--映射,网络URL地址映射到本地文件夹地址查找文件-->
<mvc:resources mapping="/image/*" location="/img/"/>
<!--http://location:8080/spring-mvc/img/A.jpg 在本地webapp的img文件夹下的A.jpg-->
<mvc:resources mapping="/txt/*" location="/txt/"/>
```

### 法三配置default-servlet-handler(优先)

-   注册了一个DefaultHttpRequestHander处理器
-   静态资源的访问都用这个处理器去处理

```xml
<mvc:default-servlet-handler/>
```

-   结束

#### 优势

-   不用一一映射,写一行就行

