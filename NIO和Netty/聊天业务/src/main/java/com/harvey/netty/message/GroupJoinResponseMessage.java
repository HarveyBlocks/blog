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
public class GroupJoinResponseMessage extends AbstractResponseMessage {

    public GroupJoinResponseMessage(boolean success, String reason,long sequenceId) {
        super(success, reason,sequenceId);
    }

    @Override
    public byte getMessageType() {
        return GROUP_JOIN_RESPONSE_MESSAGE;
    }
}
