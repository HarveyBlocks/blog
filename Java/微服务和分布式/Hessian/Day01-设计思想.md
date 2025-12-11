# 设计思想

## 应用服务器

Resin或Tomcat

也就是说, 使用了Http协议

## 创建服务

1.  原来的Service, **一定**要定义接口(习惯了)

    Why? 要代理类, JDK创建代理类需要接口

    Hession那时候没有Cglib, 所以不能用父子类做接口

2.  需要被序列化的实体对象, 必须实现Serliazble接口

3.  服务的发布

    让调用者知道我提供了哪些服务, 以及这些服务如何访问

    没有注册中心, 但是使用HessianServlet完成

    在web,xml给HessianServlet配置注册服务

    ```xml
    <servlet>
        <!--servlet名字随便取, 一个服务一个servlet-name-->
    	<servlet-name>orderHessianServlet</servlet-name>
        <servlet-ckass>xx.xxx.xxx.HessianServlet</servlet-ckass>
        <!--初始化参数-->
        <init-param>
            <!--param-name固定-->
        	<param-name>home-api</param-name>
            <param-value>com.harvey.service.OrderService</param-value>
        </init-param>
        <init-param>
            <!--param-name固定-->
        	<param-name>home-class</param-name>
            <param-value>com.harvey.service.impl.OrderServiceImpl</param-value>
        </init-param>
    </servlet>
    ```

    客户端访问Servlet的配置

    ```xml
    <servlet-mapping>
    	<servlet-name>orderHessianServlet</servlet-name>
        <url-pattern>/orderService</url-pattern>
    </servlet-mapping>
    ```

    **请求必须是POST请求**, 请求内容是二进制的, 要去放到HTTP请求的请求体

## 代理

在客户端需要代理

`HessianProxyFactory`

提供接口的类对象

需要url

客户端和服务端需要同一套Service的接口, 这个服务的接口要做成公告的模块

即Maven的Commans模块

