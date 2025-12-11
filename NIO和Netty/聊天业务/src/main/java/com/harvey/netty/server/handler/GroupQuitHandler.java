package com.harvey.netty.server.handler;

import com.harvey.netty.message.GroupQuitRequestMessage;
import com.harvey.netty.message.GroupQuitResponseMessage;
import com.harvey.netty.server.session.Group;
import com.harvey.netty.server.session.GroupSession;
import com.harvey.netty.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理退群请求
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
@ChannelHandler.Sharable
public class GroupQuitHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        GroupQuitResponseMessage respMsg = null;
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.removeMember(msg.getGroupName(), msg.getUsername());
        if(group == null){
            respMsg = new GroupQuitResponseMessage(false,"该群聊不存在",msg.getSequenceId());
        }else {
            respMsg = new GroupQuitResponseMessage(true,"成功退出群聊+`"+group.getName()+"`",msg.getSequenceId());
            groupSession.broadcastFilterFrom(msg,msg.getGroupName()+"退出群聊 `"+msg.getGroupName()+"`");
            if(group.getMembers().size()<=2){
                groupSession.removeGroup(group.getName());
                groupSession.broadcastFilterFrom(msg,msg.getGroupName()+"人数少于三人, 已解散");
            }
        }

        ctx.writeAndFlush(respMsg);
    }
}