package com.harvey.netty.server.handler;

import com.harvey.netty.message.*;
import com.harvey.netty.server.session.GroupSession;
import com.harvey.netty.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

/**
 * 处理查询群成员请求
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
@ChannelHandler.Sharable
public class GroupMembersHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        GroupMembersResponseMessage respMsg;
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Set<String> members = groupSession.getMembers(msg.getGroupName());
        if(members.isEmpty()){
            respMsg = new GroupMembersResponseMessage(null,"该群聊不存在",msg.getSequenceId());
        }else {
            respMsg = new GroupMembersResponseMessage(members,"已查询到成员 "+members.size()+"个",msg.getSequenceId());

        }
        ctx.writeAndFlush(respMsg);
    }
}