package com.harvey.netty.server.handler;

import com.harvey.netty.message.GroupJoinRequestMessage;
import com.harvey.netty.message.GroupJoinResponseMessage;
import com.harvey.netty.server.session.Group;
import com.harvey.netty.server.session.GroupSession;
import com.harvey.netty.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理入群请求
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
@ChannelHandler.Sharable
public class GroupJoinHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        GroupJoinResponseMessage respMsg;
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.joinMember(msg.getGroupName(), msg.getUsername());
        if(group == null){
            respMsg = new GroupJoinResponseMessage(false,"该群聊不存在",msg.getSequenceId());
        }else {
            respMsg = new GroupJoinResponseMessage(true,"加入 `"+group.getName()+"` 成功! 群成员还有: "+group.getMembers(),msg.getSequenceId());
            groupSession.broadcastFilterFrom(msg,"有新成员 "+msg.getUsername()+" 加入");
        }
        ctx.writeAndFlush(respMsg);
    }
}