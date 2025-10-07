package com.harvey.netty.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


/**
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 18:58
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {
    public ProtocolFrameDecoder() {
        super(1024, 16,Integer.SIZE/8);
    }
}
