# 私信



## 服务端处理请求并返回响应并发送消息

```java
public class ChatHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String target = msg.getTo();
        Channel targetChannel = SessionFactory.getSession().getChannel(target);
        ChatResponseMessage resp;
        if(targetChannel == null){
            // 不在线
            resp = new ChatResponseMessage(false,"对方不在线",msg.getSequenceId());
            ctx.writeAndFlush(resp);
        }else {
            ChatResponseMessage sendMessage = new ChatResponseMessage(msg.getFrom(), msg.getContent());
            targetChannel.writeAndFlush(sendMessage);
            resp = new ChatResponseMessage(true, "发送成功",msg.getSequenceId());
            ctx.writeAndFlush(resp);
        }


    }
}
```



### 客户端接收响应

```java
@ChannelHandler.Sharable
public class ReceivePersonChatHandler extends SimpleChannelInboundHandler<ChatResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatResponseMessage msg) throws Exception {
        Boolean success = msg.getSuccess();
        if (success == null){
            // 是别人发来的消息
            String from = msg.getFrom();
            String content = msg.getContent();
            System.out.println(from+"-私信:"+content);
        }else if(Boolean.TRUE.equals(success)){
            // 发送成功
            System.out.println(msg.getReason());
        }else {
            System.err.println(msg.getReason());
        }
    }
}
```

