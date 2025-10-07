# 协议的解析和设计

##以Redis协议为例

例如Redis传输协议

```shell
set name Jack
```

-   命令长度3
-   `set`长度3
-   `name`长度4
-   `Jack`长度4
-   使用回车换行分割

传输格式应该是

```text
*3
$3
set
$4
name
$4
Jack
```



Netty提供了HTTP, Redis, Https协议, Web操作协议.....



## HTTP协议

服务器端

```java
pipeline.addLast(new HttpServerCodec()); // Codec 一般是组合式的编解码器
```

![image-20240229153827182](../../assets/Day08-%E5%8D%8F%E8%AE%AE%E7%9A%84%E8%A7%A3%E6%9E%90%E5%92%8C%E8%AE%BE%E8%AE%A1/image-20240229153827182.png)



客户端发送



![image-20240229154638331](../../assets/Day08-%E5%8D%8F%E8%AE%AE%E7%9A%84%E8%A7%A3%E6%9E%90%E5%92%8C%E8%AE%BE%E8%AE%A1/image-20240229154638331.png)

服务端接收

```log
15:48:13.402 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x8e48bd35, L:/0:0:0:0:0:0:0:1:8080 - R:/0:0:0:0:0:0:0:1:61233] READ: 763B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 47 45 54 20 2f 73 68 6f 70 2f 69 64 2f 31 20 48 |GET /shop/id/1 H|
|00000010| 54 54 50 2f 31 2e 31 0d 0a 48 6f 73 74 3a 20 6c |TTP/1.1..Host: l|
|00000020| 6f 63 61 6c 68 6f 73 74 3a 38 30 38 30 0d 0a 43 |ocalhost:8080..C|
|00000030| 6f 6e 6e 65 63 74 69 6f 6e 3a 20 6b 65 65 70 2d |onnection: keep-|
|00000040| 61 6c 69 76 65 0d 0a 73 65 63 2d 63 68 2d 75 61 |alive..sec-ch-ua|
|00000050| 3a 20 22 4e 6f 74 5f 41 20 42 72 61 6e 64 22 3b |: "Not_A Brand";|
|00000060| 76 3d 22 38 22 2c 20 22 43 68 72 6f 6d 69 75 6d |v="8", "Chromium|
|00000070| 22 3b 76 3d 22 31 32 30 22 2c 20 22 4d 69 63 72 |";v="120", "Micr|
|00000080| 6f 73 6f 66 74 20 45 64 67 65 22 3b 76 3d 22 31 |osoft Edge";v="1|
|00000090| 32 30 22 0d 0a 73 65 63 2d 63 68 2d 75 61 2d 6d |20"..sec-ch-ua-m|
|000000a0| 6f 62 69 6c 65 3a 20 3f 30 0d 0a 73 65 63 2d 63 |obile: ?0..sec-c|
|000000b0| 68 2d 75 61 2d 70 6c 61 74 66 6f 72 6d 3a 20 22 |h-ua-platform: "|
|000000c0| 57 69 6e 64 6f 77 73 22 0d 0a 55 70 67 72 61 64 |Windows"..Upgrad|
|000000d0| 65 2d 49 6e 73 65 63 75 72 65 2d 52 65 71 75 65 |e-Insecure-Reque|
|000000e0| 73 74 73 3a 20 31 0d 0a 55 73 65 72 2d 41 67 65 |sts: 1..User-Age|
|000000f0| 6e 74 3a 20 4d 6f 7a 69 6c 6c 61 2f 35 2e 30 20 |nt: Mozilla/5.0 |
|00000100| 28 57 69 6e 64 6f 77 73 20 4e 54 20 31 30 2e 30 |(Windows NT 10.0|
|00000110| 3b 20 57 69 6e 36 34 3b 20 78 36 34 29 20 41 70 |; Win64; x64) Ap|
|00000120| 70 6c 65 57 65 62 4b 69 74 2f 35 33 37 2e 33 36 |pleWebKit/537.36|
|00000130| 20 28 4b 48 54 4d 4c 2c 20 6c 69 6b 65 20 47 65 | (KHTML, like Ge|
|00000140| 63 6b 6f 29 20 43 68 72 6f 6d 65 2f 31 32 30 2e |cko) Chrome/120.|
|00000150| 30 2e 30 2e 30 20 53 61 66 61 72 69 2f 35 33 37 |0.0.0 Safari/537|
|00000160| 2e 33 36 20 45 64 67 2f 31 32 30 2e 30 2e 30 2e |.36 Edg/120.0.0.|
|00000170| 30 0d 0a 41 63 63 65 70 74 3a 20 74 65 78 74 2f |0..Accept: text/|
|00000180| 68 74 6d 6c 2c 61 70 70 6c 69 63 61 74 69 6f 6e |html,application|
|00000190| 2f 78 68 74 6d 6c 2b 78 6d 6c 2c 61 70 70 6c 69 |/xhtml+xml,appli|
|000001a0| 63 61 74 69 6f 6e 2f 78 6d 6c 3b 71 3d 30 2e 39 |cation/xml;q=0.9|
|000001b0| 2c 69 6d 61 67 65 2f 77 65 62 70 2c 69 6d 61 67 |,image/webp,imag|
|000001c0| 65 2f 61 70 6e 67 2c 2a 2f 2a 3b 71 3d 30 2e 38 |e/apng,*/*;q=0.8|
|000001d0| 2c 61 70 70 6c 69 63 61 74 69 6f 6e 2f 73 69 67 |,application/sig|
|000001e0| 6e 65 64 2d 65 78 63 68 61 6e 67 65 3b 76 3d 62 |ned-exchange;v=b|
|000001f0| 33 3b 71 3d 30 2e 37 0d 0a 53 65 63 2d 46 65 74 |3;q=0.7..Sec-Fet|
|00000200| 63 68 2d 53 69 74 65 3a 20 6e 6f 6e 65 0d 0a 53 |ch-Site: none..S|
|00000210| 65 63 2d 46 65 74 63 68 2d 4d 6f 64 65 3a 20 6e |ec-Fetch-Mode: n|
|00000220| 61 76 69 67 61 74 65 0d 0a 53 65 63 2d 46 65 74 |avigate..Sec-Fet|
|00000230| 63 68 2d 55 73 65 72 3a 20 3f 31 0d 0a 53 65 63 |ch-User: ?1..Sec|
|00000240| 2d 46 65 74 63 68 2d 44 65 73 74 3a 20 64 6f 63 |-Fetch-Dest: doc|
|00000250| 75 6d 65 6e 74 0d 0a 41 63 63 65 70 74 2d 45 6e |ument..Accept-En|
|00000260| 63 6f 64 69 6e 67 3a 20 67 7a 69 70 2c 20 64 65 |coding: gzip, de|
|00000270| 66 6c 61 74 65 2c 20 62 72 0d 0a 41 63 63 65 70 |flate, br..Accep|
|00000280| 74 2d 4c 61 6e 67 75 61 67 65 3a 20 7a 68 2d 43 |t-Language: zh-C|
|00000290| 4e 2c 7a 68 3b 71 3d 30 2e 39 2c 65 6e 3b 71 3d |N,zh;q=0.9,en;q=|
|000002a0| 30 2e 38 2c 65 6e 2d 47 42 3b 71 3d 30 2e 37 2c |0.8,en-GB;q=0.7,|
|000002b0| 65 6e 2d 55 53 3b 71 3d 30 2e 36 0d 0a 43 6f 6f |en-US;q=0.6..Coo|
|000002c0| 6b 69 65 3a 20 49 64 65 61 2d 63 39 32 32 62 31 |kie: Idea-c922b1|
|000002d0| 66 33 3d 38 35 61 65 36 32 37 38 2d 61 32 62 34 |f3=85ae6278-a2b4|
|000002e0| 2d 34 62 63 61 2d 62 34 37 66 2d 63 38 35 65 39 |-4bca-b47f-c85e9|
|000002f0| 31 37 36 64 34 61 32 0d 0a 0d 0a                |176d4a2....     |
+--------+-------------------------------------------------+----------------+
```





`class io.netty.handler.codec.http.DefaultHttpRequest` 请求头

```http
DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /shop/id/1 HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Cache-Control: max-age=0
sec-ch-ua: "Not_A Brand";v="8", "Chromium";v="120", "Microsoft Edge";v="120"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Sec-Fetch-Site: none
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6
Cookie: Idea-c922b1f3=85ae6278-a2b4-4bca-b47f-c85e9176d4a2
```

`class io.netty.handler.codec.http.LastHttpContent$1` 请求体

```
EmptyLastHttpContent
```



对某类型的msg加以区分, 选择处理

```java
// 只关注某类型的msg,不是此类型会被跳过去
pipeline.addLast(new SimpleChannelInboundHandler<HttpRequest>() {...});
pipeline.addLast(new SimpleChannelInboundHandler<HttpContent>() {...});
```





一次请求响应

```java
pipeline.addLast(new SimpleChannelInboundHandler<HttpRequest>() {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        String uri = request.uri();
        log.debug("uri: {}", uri);
        uri = uri.substring(1);
        // 构造Response
        DefaultFullHttpResponse response =
                new DefaultFullHttpResponse(
                        request.protocolVersion()// 请求到的协议的版本
                        ,HttpResponseStatus.OK);

        // 获取响应的ByteBuf
        ByteBuf responseBuf = response.content();

        // 响应数据写入responseBuf
        responseBuf.writeBytes(("<h1>Hello, "+uri+"<h1>").getBytes());

        // 写回响应
        ctx.writeAndFlush(response);
    }
});
```

![image-20240229161119749](../../assets/Day08-%E5%8D%8F%E8%AE%AE%E7%9A%84%E8%A7%A3%E6%9E%90%E5%92%8C%E8%AE%BE%E8%AE%A1/image-20240229161119749.png)

但是, 浏览器不知道实际内容有多长, 就一直等

![image-20240229161407560](../../assets/Day08-%E5%8D%8F%E8%AE%AE%E7%9A%84%E8%A7%A3%E6%9E%90%E5%92%8C%E8%AE%BE%E8%AE%A1/image-20240229161407560.png)

```java
String result = "<h1>Hello, " + uri + "<h1>";
response.headers().setInt(
    	HttpHeaderNames.CONTENT_LENGTH,
        result.length()
);
```

## 自定义协议

有时候看某个协议不爽, 例如Redis的缺点是使用回车换行, 消息不够紧凑

###协议的组成

-   魔数
    -   用来第一时间判定是否是无效数据包
    -   一个协议对应一个魔数, 客户端和服务器一致
-   版本号
    -   可以支持协议的升级
-   序列化算法
    -   **消息正文**采用哪种序列化, 反序列化方式? 
    -   由此可以拓展
        -   json
        -   protobuf(二进制)
            -   占数据量更少
            -   效率更高
        -   hessian(二进制)
            -   占数据量更少
            -   效率更高
        -   jdk对象流(二进制)
            -   不能跨平台
            -   效率低
-   指令类型
    -   跟业务相关
-   请求序号
    -   为了双工通信, 提供了异步能力(发送1, 2, 3, 不一定要以1, 2, 3的顺序响应)
-   正文长度
-   消息正文



### 设计

#### 编码与解码

```java
public class MessageCodec extends ByteToMessageCodec<Message> {

    public static final byte[] MAGIC_NUMBER = {0xB, 0xE, 0xE, 0xF};
    public static final byte JDK_SERIALIZE = 0;
    public static final byte JSON_SERIALIZE = 1;
    public static final byte VERSION_1 = 1;
	@Override
    public void encode(ChannelHandlerContext ctx, 
                       Message msg, ByteBuf out) throws Exception {...}

    @Override
    protected void decode(ChannelHandlerContext ctx, 
                          ByteBuf in, 
                          List<Object> out) throws Exception {...}
}
```



-   编码

```java
@Override
public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
    // 4 字节的魔数, 服务端和客户端一致;
    out.writeBytes(MAGIC_NUMBER);
    // 1 字节的版本
    out.writeByte(VERSION_1);
    // 1 字节的序列化方式 jdk 0 , json 1
    out.writeByte(JDK_SERIALIZE);
    // 1 字节的指令类型
    out.writeByte(msg.getMessageType());
    // 4 字节的消息序列号
    out.writeInt(msg.getSequenceId());
    // 无意义，对齐填充
    out.writeByte(0xff);
    // 获取内容的字节数组 , message实现了Serializable接口
    byte[] bytes = serialize(msg);
    // 长度
    out.writeInt(bytes.length);
    // 写入内容
    out.writeBytes(bytes);
}

private static byte[] serialize(Message msg) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(msg);
    return bos.toByteArray();
}


```


-   解码

```java
@Override
protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    int magicNum = in.readInt();
    if(magicNum!=MAGIC_NUMBER){
        log.error("数据包无效");
        return;
    }
    byte version = in.readByte();
    byte serializerType = in.readByte();
    Class<?> messageType = Message.getMessageClass(in.readByte());
    int sequenceId = in.readInt();
    // 无意义,对其填充
    in.readByte();
    int length = in.readInt();
    byte[] bytes = new byte[length];
    in.readBytes(bytes, 0, length);
    Message message = serializerType == JDK_SERIALIZE ? deserialize(bytes) : null;
    log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
    log.debug("{}", message);
    // 解析出消息加入List, 以传给下一个Handler
    out.add(message);
}
```



为了应对粘包, 半包,配置帧解码器

```java
private static ChannelHandler[] handles() {
    return new ChannelHandler[]{
            new LengthFieldBasedFrameDecoder(1024, 12, Integer.SIZE / 8),
            new LoggingHandler(), new MessageCodec()};
}
```

-   测试粘包半包的产生

```java
ByteBuf slice1 = buf.slice(0,100);
buf.retain();
ByteBuf slice2 = buf.slice(100,buf.readableBytes()-100);
buf.retain();
channel.writeInbound(slice1);// writeInbound完了以后会调用release;
channel.writeInbound(slice2);
```

## Handlerd的线程安全

```java
LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder();
serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(decoder);
    }
});
```

这样子, 将Handler交由多个线程使用, 可以吗?

`LengthFieldBasedFrameDecoder` , 读取到一条数据后, 发现数据不全(半包), 就会等待下一条数据

此时, 另一条Channel进入(**客户端不同**),传来了一条数据, 那这条数据就会再Handler里**与上一条数据拼接**, 形成错误的数据



那么, 什么时候可以用这种把Handler交给多条Channel, 什么时候不能呢?

Netty提供**`@Sharable`注解**, 指示是否可以交给多条Channel

![image-20240229205112468](../../assets/Day08-%E5%8D%8F%E8%AE%AE%E7%9A%84%E8%A7%A3%E6%9E%90%E5%92%8C%E8%AE%BE%E8%AE%A1/image-20240229205112468.png)



**我们自定义的编解码器能否被提出呢?**

```java
public class MessageCodec extends ByteToMessageCodec<Message>{}
```

父类`ByteToMessageCodec`: 

![image-20240229205834088](../../assets/Day08-%E5%8D%8F%E8%AE%AE%E7%9A%84%E8%A7%A3%E6%9E%90%E5%92%8C%E8%AE%BE%E8%AE%A1/image-20240229205834088.png)

怎么办呢?

换一个父类

```java
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {}
```

![image-20240229205912064](../../assets/Day08-%E5%8D%8F%E8%AE%AE%E7%9A%84%E8%A7%A3%E6%9E%90%E5%92%8C%E8%AE%BE%E8%AE%A1/image-20240229205912064.png)

```java
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    private static final MessageCodec CODEC =  new MessageCodec();
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        CODEC.encode(ctx, msg, out);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        CODEC.decode(ctx,in,out);
    }
}
```



当然在使用这个解码器之前一定要将`LengthFieldBasedFrameDecoder`加入pipline