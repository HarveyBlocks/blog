# 退出

`quit`指令退出

```java
ctx.channel().close();
```

正常退出

```java
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当链接断开时触发该事件
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = SessionFactory.getSession();
        Channel channel = ctx.channel();
        session.unbind(channel);
        log.warn("{} 连接已断开",channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Session session = SessionFactory.getSession();
        Channel channel = ctx.channel();
        session.unbind(channel);
        log.warn("发生异常!, {} 连接已断开",channel);
        log.warn(cause.getMessage(),cause);
    }
}
```



