# gRPC

>   由google开源的高性能开源框架, 由Google内部的Stubby演化而来, 2015年开源

云原生

-   容器(Docker)技术
-   服务编排(K8s)技术



## 核心设计思路



### 网络通信

gRPC自己封装了网络通信的部分(客户端和服务端)

支持多种语言网络通信的封装

C/Java(Netty)/Go, C(有适配拓展C++,C#, Node.js, Python,Ruby,PHP)



### 协议

使用HTTP2

-   传输数据直接使用二进制数据内容
-   使用双向流(双工) ,支持服务端的推送
-   支持连接的多路复用





### 序列化

基于二进制的 `Protobuf` Google开源的一种序列化方式

Dubbo也可以使用Protobuf

时间效率和空间效率是 Json 的3-5倍

定制 IDL 语言





### 代理

Stub 存根

至此异构系统的RPC



## 异构编程语言的RPC

Thrift也是异构语言的RPC

Thrift使用TCP专属协议,  效率比gRPC高

gRPC高效进行进程间通信, **原生**支持Go, Java, C, 支持多平台Linux, Android,IOS,MacOS, Windows

Dubbo可以和gRPC做整合
