package com.harvey.netty.message;

import lombok.Data;
import lombok.ToString;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-01 14:36
 */
@Data
@ToString(callSuper = true)
public class OnlineOfflineMessage extends Message{
    @Override
    public byte getMessageType() {
        return ONLINE_OFFLINE_MESSAGE;
    }

    public OnlineOfflineMessage(boolean isOnline ,String onOffUser){
        this.isOnline = isOnline;
        this.onOffUser = onOffUser;
    }

    private String onOffUser;
    private boolean isOnline;
}
