# 网关

## 产生需求

前端原先只要修改8080一个端口就行了, 现在微服务了之后, 确有多个入口---------找谁好呢?

IP和端口改变了, 前端要怎么知道呢?  

每个微服务都要有**用户认证**, 都需要**用户信息**,难道都要写JWT和用户校验的逻辑吗, JWT的密钥需要给所有的微服务吗(密钥泄露风险)

## 概念与用处

>   网络关口

网关负责

-   请求的路由
-   转发
-   身份校验
-   仅仅暴露网关, 保护微服务

## 原理

>   网关也是一个微服务

从注册中心拉取所有微服务的地址

1.  身份校验
2.  解析JWT, 获取登录用户信息
3.  将用户信息向后传递

## 网关组件

>   SpringCloud提供

-   [Spring Cloud Gateway ](https://docs.spring.io/spring-cloud-gateway/docs/3.1.8/reference/html/)
    -   官方出品
    -   基于Web Flux的响应式编程
    -   午休调优即可获得优异性能
-   Netfilx Zuul
    -   Netflix出品
    -   基于Servlet的阻塞式编程
    -   需要调优才能获得与SpringCloudGateway类似的性能

