package com.harvey.netty.server.handler;

import com.harvey.netty.message.GroupChatRequestMessage;
import com.harvey.netty.message.GroupChatResponseMessage;
import com.harvey.netty.server.session.GroupSession;
import com.harvey.netty.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理群聊请求
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
@ChannelHandler.Sharable
public class GroupChatHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String target = msg.getGroupName();
        String from = msg.getFrom();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();

        GroupChatResponseMessage resp;
        if(groupSession.exist(target)){
            if (!groupSession.getMembers(target).contains(from)){
                // 不是成员
                resp = new GroupChatResponseMessage(false,"你不是: `"+target+"` 的成员",msg.getSequenceId());
                ctx.writeAndFlush(resp);
            }else{
                groupSession.broadcastFilterFrom(new GroupChatResponseMessage(msg));
                resp = new GroupChatResponseMessage(true, "发送成功",msg.getSequenceId());
                ctx.writeAndFlush(resp);
            }
        }else {
            // 不存在
            resp = new GroupChatResponseMessage(false,"该群聊: `"+target+"` 不存在",msg.getSequenceId());
            ctx.writeAndFlush(resp);
        }
    }
}