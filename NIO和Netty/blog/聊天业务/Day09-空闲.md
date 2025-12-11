# 连接假死

由于无用的连接导致的资源长期占用

## 原因

-   网络设备出现故障, 例如网卡, 机房等, **底层的TCP已经断开了**

    但应用程序没有感知到, 仍然占用着资源

-   公网网络不稳定, 出现丢包

    连续出现丢包, 这现象就是客户端数据发不出去, 服务端也一直收不到数据, 就这么一直等

-   应用程序线程阻塞, 无法进行数据读写

## 问题

假死的连接占用的的资源不能自动释放

向假死的连接发送数据, 得到的反馈时发送超时

## 空闲检测器(Handler)

`addLast()`

放到编解码Handler下方

```java
new IdleStateHandler(
        5/*readerIdleTimeSeconds*/,
        5/*writerIdleTimeSeconds*/,
        2/*all(Read 且 WriteIdleTimeSecond)*/
), // 判断是不是读写的空闲事件过长
// 如果读超时, 触发事件 IdleState.READER_IDLE
new ChannelDuplexHandler(){
    // 既可以作为入站处理器, 也可以作为出站处理器

    /**
     * 自定义事件, 用于应对特殊事件类型
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        if(event.state() == IdleState.READER_IDLE){
            // 触发了读空闲事件
            log.warn("已经5s没有读到数据了");
            Channel channel = ctx.channel();
			log.warn("关闭 {}",channel);
			channel.close();
        }
    }
},
```

## 对空闲的处理

-   是网络故障了? 还是网络正常, 只是一段时间没输入?

使用心跳检测, 定时向服务器发送一些数据, 告诉服务器我还或者, 只是人没有操作而已

**服务器一般判断读空闲, 客户端一般判断写空闲**

客户端写的频率要比服务器读的频率高一点, 否则服务器又要认为你已经完蛋了

### 心跳包

普通消息, 与业务无关

Ping-Pong

客户端

```java
.addLast(new IdleStateHandler(
        0, 3, 0))
// 判断是不是写的空闲事件过长
.addLast(
        new ChannelDuplexHandler() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.WRITER_IDLE) {
                    // 触发了写空闲事件
                    ctx.writeAndFlush(new PingMessage());
                }
            }
        }
)
```

