package com.harvey.netty.message;

import lombok.Data;
import lombok.ToString;

/**
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 18:58
 */
@Data
@ToString(callSuper = true)
public class ChatResponseMessage extends AbstractResponseMessage {

    private String from;
    private String content;

    public ChatResponseMessage(boolean success, String reason,long sequenceId) {
        super(success, reason,sequenceId);
    }

    public ChatResponseMessage(String from, String content) {
        this.from = from;
        this.content = content;
    }
    public ChatResponseMessage(ChatRequestMessage request) {
        this.from = request.getFrom();
        this.content = request.getContent();
    }

    @Override
    public byte getMessageType() {
        return CHAT_RESPONSE_MESSAGE;
    }
}
