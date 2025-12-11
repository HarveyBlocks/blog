package com.harvey.netty.protocol;

import com.harvey.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 18:58
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    public static final int MAGIC_NUMBER = 0xACCEBEFF;

    public static final byte VERSION_1 = 1;

    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 4 字节的魔数, 服务端和客户端一致;
        out.writeInt(MAGIC_NUMBER);
        // 1 字节的版本
        out.writeByte(VERSION_1);
        // 1 字节的序列化方式 jdk 0 , json 1
        Serializer.Algorithm serializer = Serializer.Algorithm.JSON;
        out.writeByte(serializer.value());
        // 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        // 8 字节的消息序列号
        out.writeLong(msg.getSequenceId());
        // 1 字节, 无意义，对齐填充
        out.writeByte(0xff);
        // 获取内容的字节数组 , message实现了Serializable接口
        byte[] bytes = serializer.serialize(msg);
        // 长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)  {
        int magicNum = in.readInt();
        if (magicNum != MAGIC_NUMBER) {
            log.error("数据包无效");
            return;
        }
        byte version = in.readByte();
        byte serializerType = in.readByte();
        Serializer.Algorithm deserializer = Serializer.Algorithm.algorithm(serializerType);
        Class<? extends Message> messageType = Message.getMessageClass(in.readByte());
        long sequenceId = in.readLong();
        // 无意义,对其填充
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        Object message = null;
        if (deserializer != null) {
            message = deserializer.deserialize(messageType, bytes);
        }
        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        // 解析出消息加入List, 以传给下一个Handler
        out.add(message);
    }

}