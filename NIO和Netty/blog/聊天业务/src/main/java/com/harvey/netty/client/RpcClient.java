package com.harvey.netty.client;

import com.harvey.netty.client.handler.RpcResponseMessageHandler;
import com.harvey.netty.message.RpcRequestMessage;
import com.harvey.netty.protocol.MessageCodecSharable;
import com.harvey.netty.protocol.ProtocolFrameDecoder;
import com.harvey.netty.server.service.HelloService;
import com.harvey.netty.server.service.HelloServiceImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.harvey.netty.client.handler.ClientLoginHandler.SCANNER;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 18:58
 */
public class RpcClient {

    public static void main(String[] args) {
        RpcClientManager.run();
    }
}

