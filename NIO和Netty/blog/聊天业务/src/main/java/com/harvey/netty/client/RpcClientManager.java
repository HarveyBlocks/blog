package com.harvey.netty.client;

import com.harvey.netty.client.handler.RpcResponseMessageHandler;
import com.harvey.netty.client.proxy.ServiceProxy;
import com.harvey.netty.protocol.MessageCodecSharable;
import com.harvey.netty.protocol.ProtocolFrameDecoder;
import com.harvey.netty.server.service.HelloService;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.harvey.netty.client.handler.ClientLoginHandler.SCANNER;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-04-01 13:21
 */
@Slf4j
public class RpcClientManager {
    private Bootstrap bootstrap;

    private static final NioEventLoopGroup GROUP = new NioEventLoopGroup();

    private RpcClientManager() {
    }

    private static final RpcClientManager SINGLE = new RpcClientManager();
    private static Channel CHANNEL;
    private static final ExecutorService SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();
    public static void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(SINGLE::close));
        SINGLE.initBootstrap();
        try {
            Channel channel = getClientChannel();
            // 代理类
            SINGLE_EXECUTOR.execute(SINGLE::exec);
            channel.closeFuture().addListener(
                    future -> {
                        SINGLE.close();
                    }
            );
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
    private final HelloService helloService = ServiceProxy.getProxyService(HelloService.class);
    private void exec() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        helloService.sayHello("nihao");
        helloService.sayHello("zhangsan");
        helloService.sayHello("lisi");
        helloService.sayHello("wangwu");
        helloService.sayHello("zhaoliu");
    }

    private static final Object LOCK = new Object();

    /**
     * @return 单例的Channel对象
     */
    public static Channel getClientChannel() throws InterruptedException {
        // t1, t2 都来了
        // CHANNEL都没有初始化
        if (CHANNEL != null) {
            return CHANNEL;
        }
        // t1, t2 都走到了这
        synchronized (LOCK) {
            // t1 先进去, t1走了t2再进去
            if (CHANNEL != null) {
                return CHANNEL;
            }
            SINGLE.doClient();
            return CHANNEL;
        }
    }

    private ChannelInitializer<SocketChannel> getHandlerInitializer() {
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ProtocolFrameDecoder());
                pipeline.addLast(loggingHandler);
                pipeline.addLast(messageCodec);
                pipeline.addLast(rpcResponseMessageHandler);
            }
        };
    }

    private void initBootstrap() {
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(GROUP);
        bootstrap.handler(getHandlerInitializer());
    }



    private void doClient() throws InterruptedException {
        ChannelFuture channelFuture = this.bootstrap.connect("localhost", 8080);
        CHANNEL = channelFuture.sync().channel();
    }

    private void close() {
        GROUP.shutdownGracefully();
        SCANNER.close();
    }
}
