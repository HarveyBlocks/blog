# 粘包和半包



## 问题的产生

-   `channelActive()`, 在Channel创建之后调用执行

```java
new ChannelInboundHandlerAdapter() {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}
```

客户端:

```java
@Override
public void channelActive(ChannelHandlerContext ctx) throws Exception {
    for (int i = 0; i < 10; i++) {
        ByteBuf input = ctx.alloc().buffer();
        input.writeBytes("123456789abcdef.".getBytes());
        ctx.writeAndFlush(input);
    }
}
```

之后, 

服务端: 

```text
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
|00000010| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
|00000020| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
|00000030| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
|00000040| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
|00000050| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
|00000060| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
|00000070| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
|00000080| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
|00000090| 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 2e |123456789abcdef.|
+--------+-------------------------------------------------+----------------+
```

出现了粘包现象

限定Channel的传输大小(其实是滑动窗口的大小)

```java
server.option(ChannelOption.SO_RCVBUF/*SO_RCVBUF: 作为接受方的BUf大小; SO_SNDBUF: 发送方*/,10);
```

是客户端之间操作系统依据硬件, 实际网络等协商的, 可调整的, 自适应的, 一般不需要配置

配置之后, 规定大小(例如 10字节), 就会出现半包现象

## 产生粘包现象的原因

### 滑动窗口

#### 串行机制



TCP中, 如果客户端发送消息后很长时间没有服务端应答, 客户端将尝试再次发送消息

保证消息一定被收到

消息发送是串行的, 这种应答的机制影响了吞吐量

![](../../assets/Day07-%E7%B2%98%E5%8C%85%E5%92%8C%E5%8D%8A%E5%8C%85/0049.png)



#### 滑动窗口原理

为了解决此问题，引入了**窗口**

窗口就是一个有大小的缓存区

窗口内的请求, **不需要应答返回就可以发送**

窗口大小决定了**无需等待应答**而可以继续发送的**数据最大值**

第一个请求回来之后, 窗口可以往下一个, 就可以发送窗口之外的一个请求

![](../../assets/Day07-%E7%B2%98%E5%8C%85%E5%92%8C%E5%8D%8A%E5%8C%85/0051.png)

**接收方也会维护一个窗口**,  窗口里数据才会被允许接收



#### 作用

窗口实际就起到一个缓冲区的作用, 同时也能做到流量控制的作用

-   不至于让数据发送太快导致数据准确性无法保证
-   不至于让数据发送太慢影响效率
-   这个缓冲, 就造成了粘包和半包

### Negal算法

在TCP层会采用Nagel算法, 这是一种增长传输数据效率优化手段

传输层和IP层, 都要对数据添加报头, IP层的报头占 20 字节, TCP的包头也占 20 给字节

, 此时就算只传输 1 字节, 带上报头也有 41 个字节

为了防止这种情况, Nagel算法就选择**攒够了一批数据再发送数据**, 提高了数据

同时也有可能产生粘包现象

### ByteBuf

Netty会对ByteBuf默认设置成1024

对于接收方来说, 无法即使处理的数据就会和新来的数据产生

### MMS限制

网卡(链路层)对数据包的大小有限制(几千个字节), localhost的大小限制是(65536)





## 关键

TCP协议是流式协议, 是没有消息边界的

我们需要自己找出消息的边界

## 解决方案

### 短连接

客户端和服务端完成一次数据交换之后就断开连接

客户端断开连接之后, 就认为缓冲中的数据是一条完整的数据

服务端在客户端断开连接后会读到-1, 由此可以判断, 从连接建立到断开的数据为**一条数据**

```java
Bootstrap client = new Bootstrap();
NioEventLoopGroup group = new NioEventLoopGroup(20);
// 为了对称, 从选择器了拿出读事件
try {
    client.group(group);
    // 选择客户端的Channel
    client.channel(NioSocketChannel.class);
    // 处理器
    client.handler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        protected void initChannel(NioSocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            this.addHandlers(pipeline);
        }

        private void addHandlers(ChannelPipeline pipeline) {
            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    ByteBuf input = ctx.alloc().buffer();
                    input.writeBytes("0123456789abcdef".getBytes());
                    ctx.writeAndFlush(input);
                    ChannelFuture close = ctx.channel().close();
                    close.sync();
                }
            });
        }
    });
    // 连接到服务器, 异步非阻塞
    client.connect(new InetSocketAddress("localhost", 8080)).sync();
} catch (Exception e) {
    log.error(e.getMessage(), e);
} finally {
    group.shutdownGracefully();
}
```

以上代码多次循环 , 也就是循环连接-发送-断开, 数据不会发生粘包

系统的接收缓冲区(滑动窗口, Netty的接收缓冲区, ByteBuf)若过小, 就会导致半包问题

```java
// 对服务器的配置, 减小滑动窗口
server.option(ChannelOption.SO_SNDBUF,10);
// 对每次连接的配置, 减小ByteBuf
server.childOption(
        ChannelOption.RCVBUF_ALLOCATOR,
        new AdaptiveRecvByteBufAllocator(16,16,16/*总是16的倍数不能再小了*/));
```

#### 缺点

每次都要关闭重启连接, 浪费了双方的资源



### 定长解码器

>   FixedLengthFrameDecoder
>
>   固定 长度 帧 解码器

-   服务端和客户端指定每个消息长度
-   前面的消息分到后来不够一个长度(一个帧), 就将其分到下一次的消息前面



#### 使用

客户端

```java
pipeline.addLast(new ChannelInboundHandlerAdapter() {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf input = ctx.alloc().buffer();
        for (int i = 0; i < 10; i++) {
            input.writeBytes(fillBytes((char) ('0' + i), 10 - i)
                             /*自定义方法, 生成byte[10]*/);
        }
        log.debug("发送了一次数据");
        ctx.writeAndFlush(input);
    }
});
```

服务端

```java
pipeline.addLast(new FixedLengthFrameDecoder(10/*应不小于所有消息的最大值*/));
```



#### 测试

客户端

```log
14:00:54.181 [nioEventLoopGroup-2-1] DEBUG com.harvey.netty.demo.client.NettyClient - 发送了一次数据
14:00:54.182 [nioEventLoopGroup-2-1] INFO io.netty.handler.logging.LoggingHandler - [id: 0xda0c39f5, L:/127.0.0.1:60707 - R:localhost/127.0.0.1:8080] WRITE: 100B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 30 30 30 30 30 30 30 30 30 30 31 31 31 31 31 31 |0000000000111111|
|00000010| 31 31 31 5f 32 32 32 32 32 32 32 32 5f 5f 33 33 |111_22222222__33|
|00000020| 33 33 33 33 33 5f 5f 5f 34 34 34 34 34 34 5f 5f |33333___444444__|
|00000030| 5f 5f 35 35 35 35 35 5f 5f 5f 5f 5f 36 36 36 36 |__55555_____6666|
|00000040| 5f 5f 5f 5f 5f 5f 37 37 37 5f 5f 5f 5f 5f 5f 5f |______777_______|
|00000050| 38 38 5f 5f 5f 5f 5f 5f 5f 5f 39 5f 5f 5f 5f 5f |88________9_____|
|00000060| 5f 5f 5f 5f                                     |____            |
+--------+-------------------------------------------------+----------------+
```





服务端

```log
14:00:54.229 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 30 30 30 30 30 30 30 30 30 30                   |0000000000      |
+--------+-------------------------------------------------+----------------+
14:00:54.231 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 31 31 31 31 31 31 31 31 31 5f                   |111111111_      |
+--------+-------------------------------------------------+----------------+
14:00:54.231 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 32 32 32 32 32 32 32 32 5f 5f                   |22222222__      |
+--------+-------------------------------------------------+----------------+
14:00:54.232 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 33 33 33 33 33 33 33 5f 5f 5f                   |3333333___      |
+--------+-------------------------------------------------+----------------+
14:00:54.232 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 34 34 34 34 34 34 5f 5f 5f 5f                   |444444____      |
+--------+-------------------------------------------------+----------------+
14:00:54.232 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 35 35 35 35 35 5f 5f 5f 5f 5f                   |55555_____      |
+--------+-------------------------------------------------+----------------+
14:00:54.233 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 36 36 36 36 5f 5f 5f 5f 5f 5f                   |6666______      |
+--------+-------------------------------------------------+----------------+
14:00:54.233 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 37 37 37 5f 5f 5f 5f 5f 5f 5f                   |777_______      |
+--------+-------------------------------------------------+----------------+
14:00:54.233 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 38 38 5f 5f 5f 5f 5f 5f 5f 5f                   |88________      |
+--------+-------------------------------------------------+----------------+
ngHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ COMPLETE
14:00:54.234 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x062bf8c0, L:/127.0.0.1:8080 - R:/127.0.0.1:60707] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 39 5f 5f 5f 5f 5f 5f 5f 5f 5f                   |9_________      |
+--------+-------------------------------------------------+----------------+
```



#### 缺点

要补足数据, 造成了资源利用不充分

### 分隔符



#### 使用

行分隔符

```java
pipeline.addLast(
        new LineBasedFrameDecoder(
                1024//超出maxLen限制会抛异常`TooLongFrameException`,毕竟不能一直找下去
        )// "\n", "\r\n" 都支持
);
```

自定义分隔符

```java
pipeline.addLast(
        new DelimiterBasedFrameDecoder(
            	1024,
                channel.alloc().buffer().writeBytes(new byte[]{';'})
        )
);
```



#### 缺点

需要遍历, 效率低下



### 长度字段

>   LengthFieldBasedFrameDecoder
>
>   基于长度字段的解码器

#### 使用

```java
public LengthFieldBasedFrameDecoder(
        ByteOrder byteOrder, 
    	int maxFrameLength, 	// 最长长度上限
    	int lengthFieldOffset, 	// 长度字段的偏移量
    	int lengthFieldLength, 	// 长度字段长度
        int lengthAdjustment, 	// 以长度字段为基准, 还有几个字节之后是内容
    	int initialBytesToStrip,// 从头剥离几个字节(ByteBuf的结果将去除其前面的指定部分, 可取长度字段)
    	boolean failFast
);
```

```html
lengthFieldOffset   = 1 (= the length of HDR1)
lengthFieldLength   = 2 (= the Length)
lengthAdjustment    = 1 (= the length of HDR2)
initialBytesToStrip = 3 (= the length of HDR1 + LEN)
BEFORE DECODE (16 bytes)                       AFTER DECODE (13 bytes)
+------+--------+------+----------------+      +------+----------------+
| HDR1 | Length | HDR2 | Actual Content |----->| HDR2 | Actual Content |
| 0xCA | 0x000C | 0xFE | "HELLO, WORLD" |      | 0xFE | "HELLO, WORLD" |
+------+--------+------+----------------+      +------+----------------+
```

#### 测试

```java
public static final byte[] PRE_HEADER = "HEADER-0".getBytes();

public static final byte[] POST_HEADER = "HD1".getBytes();


public static void main(String[] args) {
    EmbeddedChannel channel = new EmbeddedChannel(handlers());

    CompositeByteBuf buf = ByteBufAllocator.DEFAULT.compositeBuffer();
    int i = 10;
    while (i-->0) {
        send(buf);
    }
    channel.writeInbound(buf);
}

private static void send(CompositeByteBuf buf) {
    byte[] src = createRandomBytes(4,30);
    buf.writeBytes(PRE_HEADER)
            .writeInt(src.length) // 使用大端表示法, 解码也用大端表示法
            .writeBytes(POST_HEADER)
            .writeBytes(src);
}

private static Random RANDOM = new Random(System.currentTimeMillis());
private static byte[] createRandomBytes(int minLen, int maxLen) {
    int len = RANDOM.nextInt(maxLen) + minLen;
    byte[] result = new byte[len];
    for (int i = 0; i < len; i++) {
        result[i] = (byte) (RANDOM.nextInt(26)+'a');
    }
    return result;
}

private static ChannelHandler[] handlers() {
    int initialBytesToSharp = PRE_HEADER.length + Integer.SIZE / 8;
    return new ChannelHandler[]{
            new LengthFieldBasedFrameDecoder(
                    1024,
                    PRE_HEADER.length,
                    Integer.SIZE / 8,
                    POST_HEADER.length,
                    initialBytesToSharp
            ), new LoggingHandler()};
}
```



```log
15:12:01.932 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 18B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 6e 76 77 74 6f 77 6c 67 77 6c 78 75 6a |HD1nvwtowlgwlxuj|
|00000010| 78 6c                                           |xl              |
+--------+-------------------------------------------------+----------------+
15:12:01.932 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 34B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 72 63 67 72 67 6d 70 63 64 6c 6a 6c 6d |HD1rcgrgmpcdljlm|
|00000010| 70 68 70 67 6d 7a 68 74 6d 75 6b 77 76 77 76 77 |phpgmzhtmukwvwvw|
|00000020| 62 69                                           |bi              |
+--------+-------------------------------------------------+----------------+
15:12:01.932 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 26B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 68 6d 73 6b 68 72 67 62 70 72 63 67 63 |HD1hmskhrgbprcgc|
|00000010| 63 7a 78 76 75 75 73 62 6e 74                   |czxvuusbnt      |
+--------+-------------------------------------------------+----------------+
15:12:01.934 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 11B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 77 72 64 76 66 73 68 72                |HD1wrdvfshr     |
+--------+-------------------------------------------------+----------------+
15:12:01.934 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 30B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 6b 75 62 71 74 61 64 65 70 65 6c 69 73 |HD1kubqtadepelis|
|00000010| 6a 64 62 7a 71 61 67 74 62 72 6b 77 6c 6b       |jdbzqagtbrkwlk  |
+--------+-------------------------------------------------+----------------+
15:12:01.934 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 22B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 66 6c 72 70 73 66 70 75 6c 6b 65 69 76 |HD1flrpsfpulkeiv|
|00000010| 71 66 62 71 6a 78                               |qfbqjx          |
+--------+-------------------------------------------------+----------------+
15:12:01.934 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 32B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 64 7a 7a 79 62 69 70 6f 6c 72 6a 65 70 |HD1dzzybipolrjep|
|00000010| 69 66 65 65 79 6a 75 69 75 77 76 77 64 6c 61 67 |ifeeyjuiuwvwdlag|
+--------+-------------------------------------------------+----------------+
15:12:01.934 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 15B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 6c 74 72 77 6c 68 6e 67 61 65 71 68    |HD1ltrwlhngaeqh |
+--------+-------------------------------------------------+----------------+
15:12:01.934 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 12B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 6c 70 70 6d 6d 63 7a 76 61             |HD1lppmmczva    |
+--------+-------------------------------------------------+----------------+
15:12:01.934 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 32B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 44 31 69 74 65 73 61 66 6e 64 72 6f 72 73 74 |HD1itesafndrorst|
|00000010| 7a 72 69 72 77 72 6d 61 74 72 77 63 77 6c 63 73 |zrirwrmatrwcwlcs|
+--------+-------------------------------------------------+----------------+
15:12:01.934 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ COMPLETE
```

