# 群聊

## 创建群聊



服务端

```java
@ChannelHandler.Sharable
public class GroupCreateHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members); // 存在就返回null(createGroup已改)
        if (group == null){
            ctx.writeAndFlush(new GroupCreateResponseMessage(false,"群: `"+groupName+"` 已经存在",msg.getSequenceId()));
        }else {
            Set<String> trueMember = group.getMembers();
            ctx.writeAndFlush(new GroupCreateResponseMessage(
                    true,
                    "群: `"+ groupName+"` 创建成功!成员如下: "+ trueMember,
                    msg.getSequenceId())
            );
            groupSession.broadcast(new GroupChatResponseMessage(
                    "系统消息",groupName,"您被加入群聊:`"+groupName+"` 其他成员包括还有: "+trueMember)
            );
        }
    }
}
```

## 发送群聊消息



服务端

```java
@ChannelHandler.Sharable
public class GroupChatHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String target = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();

        GroupChatResponseMessage resp;
        if(groupSession.exist(target)){
            groupSession.broadcastFilterFrom(new GroupChatResponseMessage(msg));
            resp = new GroupChatResponseMessage(true, "发送成功",msg.getSequenceId());
            ctx.writeAndFlush(resp);
        }else {
            // 不存在
            resp = new GroupChatResponseMessage(false,"该群聊: `"+target+"` 不存在",msg.getSequenceId());
            ctx.writeAndFlush(resp);
        }
    }
}
```

客户端

```java
@ChannelHandler.Sharable
public class ReceiveGroupChatHandler extends SimpleChannelInboundHandler<GroupChatResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatResponseMessage msg) throws Exception {
        Boolean success = msg.getSuccess();
        if (success == null){
            // 是别人发来的消息
            String from = msg.getFrom();
            String content = msg.getContent();
            System.out.println(from+"-"+msg.getGroupName()+":"+content);
        }else if(Boolean.TRUE.equals(success)){
            // 发送成功
            System.out.println(msg.getReason());
        }else {
            System.err.println(msg.getReason());
        }
    }
}
```