package com.harvey.netty.protocol;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * 类型转换器, 帮助Gson转换类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 22:32
 */
@Slf4j
public class GsonClassSerializer {
    static class ClassSerializer implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
        @Override
        public JsonElement serialize(Class<?> clazz, Type type, JsonSerializationContext ctx) {
            return new JsonPrimitive(clazz.getName()); // Primitive 基本
        }
        @Override
        public Class<?> deserialize(JsonElement jsonElement,
                                    Type type,
                                    JsonDeserializationContext ctx) throws JsonParseException {
            String string = jsonElement.getAsString();
            Class<?> result; // 默认是Object.class
            try {
                result =  Class.forName(string);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
            return result;
        }
    }
}
