package com.harvey.netty.message;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.harvey.netty.protocol.Snowflake.SNOWFLAKE;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 18:58
 */
@Getter
@Setter
public abstract class Message implements Serializable {

    public static Class<? extends Message> getMessageClass(byte messageType) {
        return MESSAGE_CLASSES.get(messageType);
    }

    private long sequenceId;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "sequenceId=" + sequenceId +
                '}';
    }

    public Message() {
        // TODO 客户端的MESSAGE有添加消息ID的功能, 服务端没有,服务端的ID统一为0
        this.sequenceId = SNOWFLAKE.nextId();
    }

    public abstract byte getMessageType();

    public static final byte LOGIN_REQUEST_MESSAGE = 0;
    public static final byte LOGIN_RESPONSE_MESSAGE = 1;
    public static final byte CHAT_REQUEST_MESSAGE = 2;
    public static final byte CHAT_RESPONSE_MESSAGE = 3;
    public static final byte GROUP_CREATE_REQUEST_MESSAGE = 4;
    public static final byte GROUP_CREATE_RESPONSE_MESSAGE = 5;
    public static final byte GROUP_JOIN_REQUEST_MESSAGE = 6;
    public static final byte GROUP_JOIN_RESPONSE_MESSAGE = 7;
    public static final byte GROUP_QUIT_REQUEST_MESSAGE = 8;
    public static final byte GROUP_QUIT_RESPONSE_MESSAGE = 9;
    public static final byte GROUP_CHAT_REQUEST_MESSAGE = 10;
    public static final byte GROUP_CHAT_RESPONSE_MESSAGE = 11;
    public static final byte GROUP_MEMBERS_REQUEST_MESSAGE = 12;
    public static final byte GROUP_MEMBERS_RESPONSE_MESSAGE = 13;
    public static final byte ONLINE_OFFLINE_MESSAGE = 14;
    public static final byte PING_MESSAGE = 15;
    public static final byte RPC_REQUEST_MESSAGE = 16;
    public static final byte RPC_RESPONSE_MESSAGE = 17;
    private static final Map<Byte, Class<? extends Message>> MESSAGE_CLASSES = new HashMap<>();

    static {
        MESSAGE_CLASSES.put(LOGIN_REQUEST_MESSAGE, LoginRequestMessage.class);
        MESSAGE_CLASSES.put(LOGIN_RESPONSE_MESSAGE, LoginResponseMessage.class);
        MESSAGE_CLASSES.put(CHAT_REQUEST_MESSAGE, ChatRequestMessage.class);
        MESSAGE_CLASSES.put(CHAT_RESPONSE_MESSAGE, ChatResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_CREATE_REQUEST_MESSAGE, GroupCreateRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_CREATE_RESPONSE_MESSAGE, GroupCreateResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_JOIN_REQUEST_MESSAGE, GroupJoinRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_JOIN_RESPONSE_MESSAGE, GroupJoinResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_QUIT_REQUEST_MESSAGE, GroupQuitRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_QUIT_RESPONSE_MESSAGE, GroupQuitResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_CHAT_REQUEST_MESSAGE, GroupChatRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_CHAT_RESPONSE_MESSAGE, GroupChatResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_MEMBERS_REQUEST_MESSAGE, GroupMembersRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_MEMBERS_RESPONSE_MESSAGE, GroupMembersResponseMessage.class);
        MESSAGE_CLASSES.put(ONLINE_OFFLINE_MESSAGE, OnlineOfflineMessage.class);
        MESSAGE_CLASSES.put(PING_MESSAGE, PingMessage.class);
        MESSAGE_CLASSES.put(RPC_REQUEST_MESSAGE, RpcRequestMessage.class);
        MESSAGE_CLASSES.put(RPC_RESPONSE_MESSAGE, RpcResponseMessage.class);
    }
}
