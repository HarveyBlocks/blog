# 序列化



## 序列化接口

```java
package com.harvey.netty.protocol;


import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 序列化器和反序列化器
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-30 17:13
 */
public interface Serializer {
    <T> byte[] serialize(T object);

    <T> T deserialize(Class<T> type, byte[] bytes);

    public static final byte JDK_SERIALIZE = 0;
    public static final byte JSON_SERIALIZE = 1;
    /**
     * 序列化算法
     */
    @Slf4j
    enum Algorithm implements Serializer {
        JDK(JDK_SERIALIZE) {
            @Override
            public <T> byte[] serialize(T object) {
                return new byte[0];
            }

            @Override
            public <T> T deserialize(Class<T> type, byte[] bytes) {
				return null;
            }


        }, JSON(JSON_SERIALIZE) {
            @Override
            public <T> byte[] serialize(T object)  {
                return new byte[0];
            }

            @Override
            public <T> T deserialize(Class<T> type, byte[] bytes)  {
                return null;
            }
        };
        private final byte value;

        Algorithm(byte value) {
            this.value = value;
        }

        public byte value() {
            return value;
        }
        public static Algorithm algorithm(byte value){
            switch (value) {
                case JDK_SERIALIZE:
                    return JDK;
                case JSON_SERIALIZE:
                    return JSON;
                default:
                    log.error("未找到指定序列化器");
                    return null;
            }
        }
    }
}
```



### 编解码器

```java
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    // ...

    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // ...
        // 1 字节的序列化方式 jdk 0 , json 1
        Serializer.Algorithm serializer = Serializer.Algorithm.JDK;
        out.writeByte(serializer.value());
        // ...
        // 获取内容的字节数组 , message实现了Serializable接口
        byte[] bytes = serializer.serialize(msg);
        // ...
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // ...
        byte serializerType = in.readByte();
        Class<? extends Message> messageType = Message.getMessageClass(in.readByte());
        Serializer.Algorithm deserializer = Serializer.Algorithm.algorithm(serializerType);
        Object message = serializer.deserialize(messageType,bytes);
        // ...
    }

}
```



## JDK序列化

太长太冗余, 效率不咋地

```JDK
JDK((byte) 0) {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(Class<T> type, byte[] bytes)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (T) ois.readObject();
    }
}
```






## Json



用Google的序列化工具

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>19.0</version>
</dependency>
```

```java
 JSON(JSON_SERIALIZE) {
    /**
     * Google的Json工具
     */
    private final Gson GSON = new Gson();
    private final Charset charset = StandardCharsets.UTF_8;
    @Override
    public <T> byte[] serialize(T object) {
        return GSON.toJson(object).getBytes(charset);
    }
    @Override
    public <T> T deserialize(Class<T> type, byte[] bytes) {
        return GSON.fromJson(new String(bytes,charset),type);
    }
};
```

