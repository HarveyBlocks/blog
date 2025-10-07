package com.harvey.netty.client;

import com.harvey.netty.client.handler.*;
import com.harvey.netty.protocol.MessageCodecSharable;
import com.harvey.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import static com.harvey.netty.client.handler.ClientLoginHandler.SCANNER;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 18:58
 */
@Slf4j
public class ChatClient {
    private static final NioEventLoopGroup GROUP = new NioEventLoopGroup();

    public static void main(String[] args) {
        /*
         * 在使用右上角的关闭按钮
         * 或调用 System.exit() 方法关闭程序时，
         * Runtime.getRuntime().addShutdownHook() 注册的钩子线程仍然会被调用。
         * 但是，
         * 如果程序正常执行完成并退出时，
         * 注册的钩子线程不会被调用。
         * 注册的钩子线程主要是在 JVM 即将关闭时执行清理操作或执行特定的逻辑。
         * */
        Runtime.getRuntime().addShutdownHook(new Thread(ChatClient::close));
        try {
            doClient();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            close();
        }
    }

    private static void close() {
        GROUP.shutdownGracefully();
        SCANNER.close();
    }

    private static void doClient() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(GROUP);
        bootstrap.handler(getHandlerInitializer());
        Channel channel = bootstrap.connect("localhost", 8080).channel();
        channel.closeFuture().sync();
    }

    private static ChannelInitializer<SocketChannel> getHandlerInitializer() {
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();

        IdleStateHandler idleStateHandler = new IdleStateHandler(0, 3, 0);
        PongHandler pongHandler = new PongHandler();
        ReceivePersonChatHandler receivePersonChatHandler = new ReceivePersonChatHandler();
        ReceiveGroupChatHandler receiveGroupChatHandler = new ReceiveGroupChatHandler();
        GroupCreateResultHandler groupCreateResultHandler = new GroupCreateResultHandler();
        GroupJoinResultHandler groupJoinResultHandler = new GroupJoinResultHandler();
        GroupMembersResultHandler groupMembersResultHandler = new GroupMembersResultHandler();
        GroupQuitResultHandler groupQuitResultHandler = new GroupQuitResultHandler();

        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ProtocolFrameDecoder()).addLast(loggingHandler);
                pipeline.addLast(messageCodec);
                pipeline.addLast(idleStateHandler).addLast(pongHandler);
                pipeline.addLast("login", new ClientLoginHandler());
                pipeline.addLast("check login", new ClientLoginHandler.ResponseHandler());
                pipeline.addLast(receivePersonChatHandler).addLast(receiveGroupChatHandler);
                pipeline.addLast(groupCreateResultHandler).addLast(groupJoinResultHandler);
                pipeline.addLast(groupMembersResultHandler).addLast(groupQuitResultHandler);

            }
        };
    }

}


