package com.harvey.netty.message;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 20:33
 */
@Getter
@Setter
public class RpcResponseMessage extends Message {
    @Override
    public byte getMessageType() {
        return RPC_RESPONSE_MESSAGE;
    }

    /**
     * 返回值
     */
    private Object data = null;

    @Override
    public String toString() {
        return "RpcResponseMessage{" +
                "data=" + data +
                ", code=" + code +
                ", cause=" + msg +
                '}';
    }

    /**
     * 响应状态码
     */
    private int code = 200;
    /**
     * 异常
     */
    private String msg = null;

    public void setMsg(int code, Exception e) {
        this.code = code;
        this.msg = "[ ERROR " + code + " ]" + " " + e.getClass().getName() + " : " + e.getMessage();
    }

    public boolean isSuccess() {
        return code < 300;
    }
    public RpcResponseMessage(long sequenceId) {
        super.setSequenceId(sequenceId);
    }
}
