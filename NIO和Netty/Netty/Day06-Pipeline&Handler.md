# Pipeline&Handler

## 多个Handler执行流程

Pipeline会准备head的Handler和tail的handler

pipeline构造的是双向链表

入站用`ChannelInboundHandlerAdapter`, 出站用`ChannelOutboundHandlerAdapter`

两个类提供的方法不同, Inbound没有写, OutBound没有读

```java
private void addHandlers(ChannelPipeline pipeline) {
    pipeline.addLast("handler-1",new ChannelInboundHandlerAdapter(){
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.debug("handler-1");
            super.channelRead(ctx, msg); // ctx.fireChannelRead(msg);没有就不执行下文
        }
    });
    pipeline.addLast("handler-2",new ChannelInboundHandlerAdapter(){
        @Override
        public void channelRead(ChannelHandlerContext ctx, 
                                Object msg) throws Exception {
            log.debug("handler-2");
            super.channelRead(ctx, msg);
            ByteBuf buf = ctx.alloc().buffer().writeBytes("server".getBytes());
			ctx.channel().writeAndFlush(buf);
        }
    });
    pipeline.addLast("handler-3",new ChannelOutboundHandlerAdapter()
            /*出站处理器只有向Channel写入了数据才能触发*/{
        @Override
        public void write(ChannelHandlerContext ctx, 
                          Object msg, 
                          ChannelPromise promise) throws Exception {
            log.debug("handler-3");
            super.write(ctx, msg, promise);
        }
    });
    pipeline.addLast("handler-4",new ChannelOutboundHandlerAdapter(){
        @Override
        public void write(ChannelHandlerContext ctx, 
                          Object msg, 
                          ChannelPromise promise) throws Exception {
            log.debug("handler-4");
            super.write(ctx, msg, promise);
            // ctx.write(msg,promise); 没有就不执行下文
        }
    });
}
```

```text
handler-1 (入)
handler-2 (入)
handler-4 (出)
handler-3 (出)
```

诸如

```java
ctx.channel().writeAndFlush(buf);
ctx.writeAndFlush(buf);
```

都有方法

`channel`里的写api会**从tail Handler**触发下文

`ctx`里的写api会从**从当前Handler**触发下文

