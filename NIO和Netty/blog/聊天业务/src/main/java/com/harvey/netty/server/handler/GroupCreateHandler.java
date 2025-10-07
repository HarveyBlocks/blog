package com.harvey.netty.server.handler;

import com.harvey.netty.message.*;
import com.harvey.netty.server.session.Group;
import com.harvey.netty.server.session.GroupSession;
import com.harvey.netty.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

/**
 * 处理建群请求
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
@ChannelHandler.Sharable
public class GroupCreateHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        if(members.size()<=2){
            ctx.writeAndFlush(new GroupCreateResponseMessage(
                    false,"群: `"+groupName+"` 已经存在少于三人, 无法创建",msg.getSequenceId()));
            return;
        }
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null){
            ctx.writeAndFlush(new GroupCreateResponseMessage(
                    false,"群: `"+groupName+"` 已经存在",msg.getSequenceId()));
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