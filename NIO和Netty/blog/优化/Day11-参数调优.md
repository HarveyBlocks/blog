# 参数调优

## 参数配置

1.  客户端通过 `Bootstrap#option()`

    ```java
    new Bootstrap().option(ChannelOption.CONNECT_TIMEOUT_MILLIS,300);
    ```

2.  服务段有两种配置参数的类型

    -   给SocketChannel 配置参数

        ```java
        new ServerBootstrap().childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS,300);
        ```

    -   给 ServerSocketChannel配置参数

        ```java
        new ServerBootstrap().option(ChannelOption.SO_TIMEOUT,300);
        ```

## 超时

>   CONNECT_TINEOUT_MILLIS

-   属于SocketChannal参数
-   用于客户端建立连接时, 如果在指定毫秒内无法连接, 会抛出`Timeout`异常
-   区分: `SO_TIMEOUT`主要用在阻塞IO中, 阻塞IO中`Accept`和`Read`都是无限等待的, 如果不希望永远阻塞, 使用它调整超时时间

```java
@Test
public void testTimeout(){
    // 1. 客户端通 Bootstrap#option()

    NioEventLoopGroup group = new NioEventLoopGroup();
    try {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300); // 时间
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new LoggingHandler());
        ChannelFuture future = bootstrap.connect("centos", 8080); // 别用127.0.0.1, 用这个IP使用的是不同Timeout逻辑, 不采用配置的CONNECT_TIMEOUT_MILLIS
        future.sync().channel().closeFuture().sync();
    } catch (Exception e) {
        log.error(e.getMessage(),e);
    }finally {
        group.shutdownGracefully();
    }
}
```

```verilog
io.netty.channel.ConnectTimeoutException: connection timed out: centos/192.168.88.130:8080
	at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe$1.run(AbstractNioChannel.java:261)
	at io.netty.util.concurrent.PromiseTask.runTask(PromiseTask.java:98)
	at io.netty.util.concurrent.ScheduledFutureTask.run(ScheduledFutureTask.java:153)
	at io.netty.util.concurrent.AbstractEventExecutor.runTask(AbstractEventExecutor.java:174)
	at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:167)
	at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:470)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:569)
	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997)
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.base/java.lang.Thread.run(Thread.java:829)
```

-   如果超时时间超过两秒, 讲超出Java内定的超时时长, 就会报Java的错

```verilog
12:46:23 [ERROR] [main] c.h.n.p.TimeoutTest - Connection refused: no further information: /127.0.0.1:8080
io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: no further information: /127.0.0.1:8080
Caused by: java.net.ConnectException: Connection refused: no further information
	at java.base/sun.nio.ch.SocketChannelImpl.checkConnect(Native Method)
	at java.base/sun.nio.ch.SocketChannelImpl.finishConnect(SocketChannelImpl.java:777)
	at io.netty.channel.socket.nio.NioSocketChannel.doFinishConnect(NioSocketChannel.java:337)
	at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.finishConnect(AbstractNioChannel.java:334)
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:776)
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:724)
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:650)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:562)
	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997)
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.base/java.lang.Thread.run(Thread.java:829)

```

## SO_BACKLOG

-   属于ServerSocketChannel的参数

### TCP三次握手和Backlog

-   在Linux早期版本中, backlog包括了两个队列的大小, 在之后, 分别用一下参数来控制

-   Sync queue 半连接队列

    -   存放未完成完整三次握手的链接
    -   大小通过`/proc/sys/net/ipv4/tcp_max_syn_backlog`来指定
    -   在`syncookies`启用的情况下, 逻辑上没有最大值限制, 这个设置被忽略

-   Accept Queue 全连接队列

    -   存放完成了三次握手的连接

    -   大小通过`/proc/sys/net/core/somaxconn`来信指定

    -   在使用`listen`函数时, 内核会根据**传入的backlog参数(程序员指定)**与系统参数, **取二者的较小值**

    -   如果队列溢出, 则会**拒绝连接**(客户端会关闭)

        ```java
        19:10:13 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x638983e4] REGISTERED
        19:10:13 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x638983e4] CONNECT: localhost/127.0.0.1:8080
        19:10:13 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x638983e4] CLOSE
        19:10:13 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x638983e4] UNREGISTERED
        ```

    -   如果`accept queue`队列满了, server将发送一个拒绝连接的错误信息到client

-   一次请求-响应对应一次TCP连接, 也就是说, 只有在好几个请求处理不过来的时候才会有队列堆积

    -   要使任务堆积, 可以在此处打断点:

        ![image-20240331191130803](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/NIO和Netty/优化/Day11-参数调优/image-20240331191130803.png)

    -   或者使用BIO

-   Syn 握手时携带的数据包

## `ulimit -n 文件描述符上限`

>   操作系统参数

在Linux中, 无论是SocketCannel还是文件, 都有一个文件描述符

为了防止打开文件太多, 导致操作系统崩掉, 操作系统限制了文件打开的上限

```powershell
ulimit -n 上限
```

## [建议]TCP_NODELAY

no delay

TCP-Nagle算法: 将小数据包攒一批了再发送

可能导致客户端接收消息的延迟

然而, Netty默认开启了Nagle算法

## SO_SNDBUF & SO_RCVBUF

-   发送缓冲区和接收缓冲区

-   不建议改动, 操作系统会做调整

## ALLOCATOR

>   分配器

-   属于SocketChannel参数
-   用来分配`ByteBuf`, `ctx.alloc()`

![image-20240331193534666](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/NIO和Netty/优化/Day11-参数调优/image-20240331193534666.png)

配置方法:

虚拟机参数

是否使用池化

```powershell
-Dio.netty.allocator.type=unpooled
```

`不首选直接内存`

![image-20240331193911168](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/NIO和Netty/优化/Day11-参数调优/image-20240331193911168.png)

```powershell
-Dio.netty.noPreferDirec=True
```

## RCVBUF_ALLOCATOR

-   属于SocketChannal 参数
-   控制netty接收缓冲区的大小
-   负责入站数据的分配, 决定入站缓冲区的大小(可动态调整), 统一采用direct直接内存, 具体池化还是非池化由allocator决定

![image-20240331202159281](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/NIO和Netty/优化/Day11-参数调优/image-20240331202159281.png)

-   ByteBufAllocator决定池化还是非池化

-   RecvByteBufAllocator决定ByteBUf 的大小

    ![image-20240331202725841](https://raw.githubusercontent.com/HarveyBlocks/blog_assets/refs/heads/main/NIO和Netty/优化/Day11-参数调优/image-20240331202725841.png)

    根据数据, 猜测ByteBuf的大小, 然后分配内存

    动态根据数据量调整大小, 自适应ByteBuf大小

