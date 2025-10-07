package com.harvey.netty.server;


import com.harvey.netty.protocol.MessageCodecSharable;
import com.harvey.netty.protocol.ProtocolFrameDecoder;
import com.harvey.netty.server.handler.RpcRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * 服务器启动类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
@Slf4j
public class RpcServer {
    private static final NioEventLoopGroup BOSS = new NioEventLoopGroup();

    private static final NioEventLoopGroup WORKER = new NioEventLoopGroup();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(RpcServer::close));
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(BOSS, WORKER);
            serverBootstrap.childHandler(getChildHandlerInitializer());
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            close();
        }
    }

    private static ChannelInitializer<SocketChannel> getChildHandlerInitializer() {
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        RpcRequestMessageHandler rpcRequestMessageHandler = new RpcRequestMessageHandler();
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ProtocolFrameDecoder());
                pipeline.addLast(loggingHandler);
                pipeline.addLast(messageCodec);
                pipeline.addLast(rpcRequestMessageHandler);
            }
        };
    }
    private static void close() {
        BOSS.shutdownGracefully();
        WORKER.shutdownGracefully();
    }
}
