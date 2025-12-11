package com.harvey.netty.client.handler;

import com.harvey.netty.message.OnlineOfflineMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 上线下线消息处理器
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-01 18:48
 */
public class OnlineOfflineHandler extends SimpleChannelInboundHandler<OnlineOfflineMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, OnlineOfflineMessage msg) throws Exception {
        if (msg.isOnline()) {
            System.out.println(msg.getOnOffUser()+"已上线!");
        }else {
            System.err.println(msg.getOnOffUser()+"已下线!");
        }
    }
}
