package com.harvey.netty.protocol;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harvey.netty.message.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 序列化器和反序列化器
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-30 17:13
 */
public interface Serializer {
    <T> byte[] serialize(T object);

    <T extends Message> T deserialize(Class<T> type, byte[] bytes);

    byte JDK_SERIALIZE = 0;
    byte JSON_SERIALIZE = 1;

    /**
     * 序列化算法
     */
    @Slf4j
    enum Algorithm implements Serializer {
        JDK(JDK_SERIALIZE) {
            @Override
            public <T> byte[] serialize(T object) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = null;
                try {
                    oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                } catch (IOException e) {
                    log.error("序列化失败");
                }
                return bos.toByteArray();
            }

            @Override
            public <T extends Message> T deserialize(Class<T> type, byte[] bytes) {
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = null;
                Object object = null;
                T result = null;
                try {
                    ois = new ObjectInputStream(bis);
                    object = ois.readObject();
                    result = (T) object;
                } catch (ClassNotFoundException e) {
                    log.error("未找到该类");
                } catch (ClassCastException e) {
                    log.error("类型转换异常");
                } catch (IOException e) {
                    log.error("序列化失败");
                }
                return result;
            }

        }, JSON(JSON_SERIALIZE) {
            /**
             * Google的Json工具
             */
            private final Gson GSON = new GsonBuilder()
                    .registerTypeAdapter(Class.class,new GsonClassSerializer.ClassSerializer()).create();
            private final Charset charset = StandardCharsets.UTF_8;

            @Override
            public <T> byte[] serialize(T object) {
                return GSON.toJson(object).getBytes(charset);
            }

            @Override
            public <T extends Message> T deserialize(Class<T> type, byte[] bytes) {
                return GSON.fromJson(new String(bytes, charset), type);
            }
        };
        private final byte value;

        Algorithm(byte value) {
            this.value = value;
        }

        public byte value() {
            return value;
        }

        public static Algorithm algorithm(byte value) {
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
