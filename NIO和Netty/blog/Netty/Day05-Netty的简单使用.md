# Netty的简单使用

## 引入依赖

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
</dependency>
```

## 组件概念

-   eventLoop
    -   有多个Channel的任务队列
        -   普通任务
        -   定时任务
    -   管理Channel的io操作
    -   也可以做IO以外的其他任务
    -   EventLoop会对曾经管理过的Channel负责到底(防止数据不一致)
    -   调用执行对数据加工的几个方法
-   Channel
    -   数据的传输通道
-   msg
    -   Channel中流动的数据
    -   最开始是ByteBuf是字节形式的
-   pipeline
    -   由多道handler工序组成的流水线
    -   将事件传给所有handler
    -   handler对关注的事件加工
-   handler
    -    用来加工msg的工序
    -   对事件的关注与否取决于是否重写了其父类/接口的方法
    -   Inbound 入站 , 从服务端来看,msg进入服务端时进行的操作
    -   outbound 出站, 从服务端来看, msg离开服务端时进行 的操作
    -   可以指定EventLoop

## 编写服务端

### 创建服务器启动器

ServerBootstrap服务器端启动器, 负责组装netty组件, 启动服务器

```java
ServerBootstrap server = new ServerBootstrap();
```

### 创建EventLoopGroup

NioEvent(事件)Loop(循环)Group 

Event(事件)Loop(循环)

1.  用selector组件检测事件; 
2.  附带新线程处理事件
3.  有Accept的EventLoop, 有Read的EventLoop...故称组

```java
server.group(new NioEventLoopGroup());
```

### 选择ServerSocketChannel

选择服务器的ServerSocketChannel实现,

NIO的对操作系统比较通用

支持NIO, OIO(BIO).. 的Channel

```java
server.channel(NioServerSocketChannel.class);
```

### 添加事件处理组件



-   child = worker

-   childHandler用来分配工作,如
    -   编解码
    -   业务处理
-   处理器的方法在对应事件发生之后调用

```java
server.childHandler(childHandler);
```





#### 创建ChildHandler

ChildHandler是特殊的Handler,职责是添加别的handler

```java
ChannelInitializer<NioSocketChannel> childHandler = new ChannelInitializer<>() {
    /**
     * 添加具体的Handler
     */
    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG)); 
        // 好用的打印日志Handler
        channel.pipeline().addLast(new StringDecoder()); // 将ByteBuf转为字符串
        channel.pipeline().addLast(customHandler); // 自定义处理器
    }
};
```



#### 创建自定义处理器

自定义Handler

```java
ChannelInboundHandlerAdapter customHandler = new ChannelInboundHandlerAdapter() {
    /**
     * 要出理读事件的Handler, 打印上一步转换好的字符串
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug(String.valueOf(msg));
        log.debug(String.valueOf(msg.getClass()));
    }
};
```





### 绑定监听端口

```java
server.bind(8080);
```

## 编写客户端

### 创建启动器

```java
Bootstrap client = new Bootstrap();
```



### 添加循环事件组



```java
client.group(new NioEventLoopGroup());
```



### 选择客户端的Channel

```java
client.channel(NioSocketChannel.class);
```



### 添加客户端的处理器

```java
client.handler(new ChannelInitializer<NioSocketChannel>() {
    /**
     * 在连接建立后调用
     */
    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new StringEncoder()); // 将字符串编码
    }
});
```



### 连接到服务器

异步非阻塞

```java
ChannelFuture future = client.connect(new InetSocketAddress("localhost", 8080));
```



### 发送数据

```java
try {
    Channel channel = future
            // 阻塞方法, 直到连接建立
            .sync()
            // 获取channel
            .channel();
    // 向服务器发送数据
    channel.writeAndFlush("Hello World");
    // 无论收发数据, 都会走Handler
} catch (InterruptedException e) {
    log.error(e.getMessage(), e);
}
```





