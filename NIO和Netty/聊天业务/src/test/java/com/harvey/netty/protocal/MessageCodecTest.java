package com.harvey.netty.protocal;

import com.harvey.netty.message.LoginRequestMessage;
import com.harvey.netty.protocol.MessageCodec;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 10:38
 */
public class MessageCodecTest {
    private static final MessageCodec CODEC = new MessageCodec();
    private static final LoggingHandler LOGGING = new LoggingHandler();
    private static final EmbeddedChannel CHANNEL = new EmbeddedChannel(LOGGING, CODEC, LOGGING);

    @Test
    public void testEncode(){
        CHANNEL.writeOutbound(new LoginRequestMessage("张三", "123"));
    }
    @Test
    public void testDecode(){
        CHANNEL.writeInbound();
    }
}
