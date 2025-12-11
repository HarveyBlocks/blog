package com.harvey.netty.server;

import com.harvey.netty.protocol.MessageCodecSharable;
import com.harvey.netty.protocol.ProtocolFrameDecoder;
import com.harvey.netty.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天服务器启动类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
@Slf4j
public class ChatServer {


    protected static final NioEventLoopGroup BOSS = new NioEventLoopGroup();

    protected static final NioEventLoopGroup WORKER = new NioEventLoopGroup();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(ChatServer::close));
        try {
            doServer();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            close();
        }
    }

    private static void doServer() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 2);
        serverBootstrap.group(ChatServer.BOSS, ChatServer.WORKER);
        serverBootstrap.childHandler(getChildHandlerInitializer());
        Channel channel = serverBootstrap.bind(8080).sync().channel();
        channel.closeFuture().sync();
    }
    private static ChannelInitializer<SocketChannel> getChildHandlerInitializer(){
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler();
        // 判断是不是读写的空闲事件过长
        IdleStateHandler idleStateHandler = new IdleStateHandler(
                /*readerIdleTimeSeconds*/
                5,
                /*writerIdleTimeSeconds*/
                0,
                /*all(Read 且 WriteIdleTimeSecond)*/
                0);
        PingHandler pingHandler = new PingHandler();

        ServiceLoginHandler serviceLoginHandler = new ServiceLoginHandler();


        ChatHandler chatHandler = new ChatHandler();
        GroupChatHandler groupChatHandler = new GroupChatHandler();
        GroupCreateHandler groupCreateHandler = new GroupCreateHandler();
        GroupJoinHandler groupJoinHandler = new GroupJoinHandler();
        GroupMembersHandler groupMembersHandler = new GroupMembersHandler();
        GroupQuitHandler groupQuitHandler = new GroupQuitHandler();
        QuitHandler quitHandler = new QuitHandler();

        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(extracted());
            }

            private ChannelHandler[] extracted() {
                return new ChannelHandler[]{new ProtocolFrameDecoder(), loggingHandler, messageCodec,
                        /*心跳*/
                        idleStateHandler, pingHandler,
                        /*登录聊天,群聊处理器*/
                        serviceLoginHandler, chatHandler, groupChatHandler,
                        /*群, 写操作*/
                        groupCreateHandler, groupJoinHandler, groupMembersHandler, groupQuitHandler,
                        /*断开连接*/
                        quitHandler};
            }
        };
    }
    private static void close() {
        ChatServer.BOSS.shutdownGracefully();
        ChatServer.WORKER.shutdownGracefully();
    }
}
