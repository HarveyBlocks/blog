package com.harvey.netty.client.handler;

import com.google.gson.Gson;
import com.harvey.netty.message.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 20:43
 */
@Slf4j
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    /**
     * 选择<code>ConcurrentHashMap<code/>保证线程安全
     */
    private static final Map<Long, Promise<Object>> SEQUENCE_ID_PROMISE_MAP = new ConcurrentHashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage message) {
        long sequenceId = message.getSequenceId();
        // 即时删除Map中无用的promise
        Promise<Object> promise = SEQUENCE_ID_PROMISE_MAP.remove(sequenceId);
        if (promise == null) {
            return;
        }
        if (message.isSuccess()) {
            Object data = message.getData();
            promise.setSuccess(data);
        } else {
            promise.setFailure(new Exception(message.getMsg()));
        }
        // promise set 之后, 就会唤醒await()
        ctx.writeAndFlush(message);
    }

    public static void putPromise(long sequenceId,Promise<Object> promise){
        RpcResponseMessageHandler.SEQUENCE_ID_PROMISE_MAP.put(sequenceId,promise);
    }
}
