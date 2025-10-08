JavaEE三层架构

-   Web层
    -   注入Service
-   service层
    -   注入Dao(Mapper)
-   持久层
    -   Mapper
    -   与数据库的连接

Web层使用Serclet技术充当的化,需要在Servlet中获得Spring对象

在Web层(Servlet)获得Spring容器的引用,从而通过Spring容器getBean()的方式获得应用层的Service对象

-   导入servlet包

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
</dependency>
```

-   这段代码就在Web层(Servlet)

```java
// web层调用service层,获得AccountService,accountService存在applicationContext中
ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(SpringConfig.class);
//反复创建容器,不好
AccountService accountService = applicationContext.getBean(AccountService.class);
accountService.transMoney("tom", "lucy", 10);
response.getWriter().write("OK");
```

## 诉求:

-   Application只创建一次
-   配置类只加载一侧
-   Web应用启动时就加载以上两项
-   需要在Web应用的任何位置都能获取



### 域

-   page
    -   一个页面一个域

-   request
    -   一次请求一个域

-   Sesisson
    -   一个用户一个域
-   ServletContest/Application
    -   一个服务器一个域



### 解决:

-   Lisener监听服务器启动
-   放到Application域
