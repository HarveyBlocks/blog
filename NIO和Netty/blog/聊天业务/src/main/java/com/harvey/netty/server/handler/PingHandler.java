package com.harvey.netty.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 心跳
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 19:49
 */
@Slf4j
@ChannelHandler.Sharable
public class PingHandler extends ChannelDuplexHandler {
    // 既可以作为入站处理器, 也可以作为出站处理器
    private final Map<Channel, Integer> countMap = new HashMap<>();

    /**
     * 自定义事件, 用于应对特殊事件类型
     * 如果读超时, 触发事件 IdleState.READER_IDLE
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() != IdleState.READER_IDLE) {
            return;
        }
        // 触发了读空闲事件
        log.warn("已经5s没有读到数据了");
        Channel channel = ctx.channel();
        Integer count = countMap.get(channel);
        if (count == null) {
            countMap.put(channel, 1);
            return;
        }
        countMap.put(channel, count++);
        if (count != 30 * 60 / 5) {
            return;
        }
        // 30分钟
        log.warn("关闭 {}", channel);
        countMap.remove(channel);
        channel.close();
    }
}