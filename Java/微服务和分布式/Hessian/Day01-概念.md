# Hessian

基于Java编程语言开发的RPC编程方案

零零年以前的qwq

没有注册中心, 熔断, 限流

但是有网络传输, 协议, 序列化, 代理

**Dubbo采用了Hessian序列化**的改版Hessian-light

## 概念

-   Hessian是Resin服务器(与Tomcat同类)的伴生产品

    Resin被誉为最快的服务器

    新浪大量使用Resin

-   只支持Java编程语言使用(服务的调用者和服务的提供者都得是Java开发的) 好 垃 圾 啊

    gRPC(google), Thrift(Apache)都是支持多语言的

-   序列化协议是二进制

-   [Hessian2.0](http://hessian.caucho.com/doc/hessian-ws.html)

