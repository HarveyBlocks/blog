# Netty

>Netty is *an asynchronous event-driven network application framework*
>for rapid development of maintainable high performance protocol servers & clients.
>
>Netty是一个异步的, 基于事件驱动的网络应用框架, 用于快速开发可维护, 高性能的网络服务器和客户端

-   Netty实现异步使用的不是异步IO的API, 而是多线程
-   基于NIO
-   网络通讯的第一选择
-   作者
    -   Trustin Lee
    -   棒子人
    -   Apache成员
-   使用Netty技术的应用
    -   Cassandra - nosql 数据库
    -   Spark - 大数据分布式计算框架
    -   Hadoop - 大数据分布式存储框架
    -   RocketMQ - ali 开源的消息队列
    -   ElasticSearch - 搜索引擎
    -   gRPC - rpc 框架
    -   Dubbo - rpc 框架
    -   Spring 5.x - flux api 完全抛弃了 tomcat ，使用 netty 作为服务器端
    -   Zookeeper - 分布式协调框架

## 优势

-   更大, 更好, 更强的NIO
    -   构建了协议
        -   请求行
        -   请求头
        -   请求体
    -   解决了TCP传输问题
        -   粘包
        -   半包
    -   解决nio对Linux的epoll空轮询导致的CPU占用`100%`
        -   NIO至今没有解决
    -   对API进行增强, 使之更易得
        -   `ThreadLocal`=>`FastThreadLocal`
        -   `ByteBuffer`=>`ByteBuf`
-   Netty自2004年的2.x版本经受考验至今
-   比Mina迭代更快, API更简洁, 文档更优秀(棒子的自买自夸)

