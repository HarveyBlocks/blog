package com.harvey.netty.protocal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 11:00
 */
@Slf4j
public class TimeoutTest {

    @Test
    public void testTimeout() {

        // 1. 客户端通 Bootstrap#option()
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.option(ChannelOption.SO_BACKLOG, 5000);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new LoggingHandler());
            ChannelFuture future = bootstrap.connect("centos", 8080);
            future.sync().channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getClass().getName());
        } finally {
            group.shutdownGracefully();
        }

    }
}
