package com.harvey.netty.client.handler;

import com.harvey.netty.message.GroupMembersResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-01 15:02
 */
@ChannelHandler.Sharable
public class GroupMembersResultHandler extends SimpleChannelInboundHandler<GroupMembersResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersResponseMessage msg) throws Exception {
        if(Boolean.TRUE.equals(msg.isSuccess())){
            System.out.println(msg.getReason());
            System.out.println(msg.getMembers());
        }else {
            System.err.println(msg.getReason());
        }
    }
}