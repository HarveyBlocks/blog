package com.harvey.netty.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 18:58
 */
public abstract class AbstractResponseMessage extends Message {
    private Boolean success = null;

    @Getter
    @Setter
    private String reason;

    public Boolean isSuccess() {
        return success;
    }
    public void failure(){
        this.success = Boolean.FALSE;
    }
    public void success(){
        this.success = Boolean.TRUE;
    }
    public AbstractResponseMessage() {
    }

    public AbstractResponseMessage(Boolean success, String reason, long sequenceId) {
        this.success = success;
        this.reason = reason;
        super.setSequenceId(sequenceId);
    }
}
