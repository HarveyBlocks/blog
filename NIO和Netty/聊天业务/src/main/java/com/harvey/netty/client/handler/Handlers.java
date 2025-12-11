package com.harvey.netty.client.handler;

import com.harvey.netty.message.AbstractResponseMessage;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-01 18:37
 */
public class Handlers {
    public static void responseSuccessHandler(AbstractResponseMessage msg) {
        if(Boolean.TRUE.equals(msg.isSuccess())){
            System.out.println(msg.getReason());
        }else {
            System.err.println(msg.getReason());
        }
    }
    private Handlers(){}

}
