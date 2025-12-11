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
public class GroupChatResponseMessage extends AbstractResponseMessage {
    private String from;
    private String content;
    private String groupName;

    public GroupChatResponseMessage(boolean success, String reason,long sequenceId) {
        super(success, reason,sequenceId);
    }

    public GroupChatResponseMessage(String from, String groupName, String content) {
        this.from = from;
        this.groupName = groupName;
        this.content = content;
    }
    public GroupChatResponseMessage(GroupChatRequestMessage request) {
        this.from = request.getFrom();
        this.groupName = request.getGroupName();
        this.content = request.getContent();
    }
    @Override
    public byte getMessageType() {
        return GROUP_CHAT_RESPONSE_MESSAGE;
    }
}
