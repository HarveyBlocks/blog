# RPC

## 请求消息

```java
package com.harvey.netty.message;

import lombok.Getter;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 20:33
 */
@Getter
public class RpcRequestMessage extends Message{
    @Override
    public byte getMessageType() {
        return RPC_REQUEST_MESSAGE;
    }
    /**
     * 调用的接口全限定名，服务端根据它找到实现
     */
    private final String interfaceName;
    /**
     * 调用接口中的方法名
     */
    private final String methodName;
    /**
     * 方法返回类型
     */
    private final Class<?> returnType;
    /**
     * 方法参数类型数组
     */
    private final Class<?>[] parameterTypes;
    /**
     * 方法参数值数组
     */
    private final Object[] parameterValue;

    public RpcRequestMessage(String interfaceName, String methodName,
                             Class<?> returnType,
                             Class<?>[] parameterTypes, Object[] parameterValue) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

}
```

### Gson的类型转换器

Gson无法将Java中的Class类转换成合适的类型,所以搞个注册器

```Java
package com.harvey.netty.protocol;

import com.google.gson.*;
import jdk.internal.net.http.common.Log;
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

```

注册一下

```Java
JSON(JSON_SERIALIZE) {
    /**
     * Google的Json工具
     */
    private final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Class.class,new GsonClassSerializer.ClassSerializer()).create();
	// ...
}
```

## 响应消息

```java
package com.harvey.netty.message;

import lombok.Data;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 20:33
 */
@Data
public class RpcResponceMessage extends Message{
    @Override
    public byte getMessageType() {
        return RPC_RESPONSE_MESSAGE;
    }
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

}
```

## 服务器

```java
package com.harvey.netty.server;

import ...

/**
 * 服务器启动类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
@Slf4j
public class RpcServer {
    private static final NioEventLoopGroup BOSS = new NioEventLoopGroup();

    private static final NioEventLoopGroup WORKER = new NioEventLoopGroup();

    public static void main(String[] args) {
        ProtocolFrameDecoder protocolFrameDecoder = new ProtocolFrameDecoder();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        // rpcRequestMessageHandler
        RpcRequestMessageHandler rpcRequestMessageHandler = new RpcRequestMessageHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(BOSS, WORKER);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(protocolFrameDecoder);
                    pipeline.addLast(loggingHandler);
                    pipeline.addLast(messageCodec);
                    pipeline.addLast(rpcRequestMessageHandler);
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            BOSS.shutdownGracefully();
            WORKER.shutdownGracefully();
        }
    }
}

```

### 服务端处理请求

```java
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());
        try {
            Class<?> interfaceClass = Class.forName(message.getInterfaceName());
            HelloService service = (HelloService)
                    ServiceFactory.getService(interfaceClass);
            Method method = interfaceClass.getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());
            response.setData(invoke);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            String msg = e.getCause().getMessage();
            response.setCode(500);
            response.setSuccess(false);
            response.setReason("远程调用出错:" + msg);
        }
        ctx.writeAndFlush(response);
    }
}
```

## 客户端的模板

```java
@Slf4j
public class RpcClient {
    private static final NioEventLoopGroup GROUP = new NioEventLoopGroup();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(RpcClient::close));
        try {
            doClient();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            close();
        }
    }

    private static void close() {
        GROUP.shutdownGracefully();
        SCANNER.close();
    }

    private static void doClient() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(GROUP);
        bootstrap.handler(getHandlerInitializer());
        Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
        RpcRequestMessage message = new RpcRequestMessage(
                "com.harvey.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );
        ChannelFuture channelFuture = channel.writeAndFlush(message);
        channelFuture.addListener(future->{
            if (!future.isSuccess()){
                Throwable cause = future.cause();
                // 有时候发生异常, 导致消息没发出, 这个↓检测消息发没发出很重要
                log.error(cause.getMessage(), cause);
            }
        });
        channel.closeFuture().sync();
    }

    private static ChannelInitializer<SocketChannel> getHandlerInitializer() {
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ProtocolFrameDecoder());
                pipeline.addLast(loggingHandler);
                pipeline.addLast(messageCodec);
                pipeline.addLast(rpcResponseMessageHandler);
            }
        };
    }

}
```

### 接收响应

```java
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage message) {
        log.info("{}",message.getData());
        ctx.writeAndFlush(message);
    }
}
```

## 代理Service对象

```java
package com.harvey.netty.client.proxy;

import com.harvey.netty.client.RpcClientManager;
import com.harvey.netty.message.RpcRequestMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import sun.jvm.hotspot.utilities.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-04-01 13:09
 */
@Slf4j
public class ServiceProxy {
    /**
     * 返回代理类
     *
     * @param serviceClass 接口类
     * @return 代理
     */
    public static <T> T getProxyService(Class<T> serviceClass) {
        Objects.requireNonNull(serviceClass);
        if (!serviceClass.isInterface()){
            throw new IllegalArgumentException(serviceClass.getName()+" is not an interface");
        }
        // 希望代理类和接口类是同一个类加载器
        ClassLoader classLoader = serviceClass.getClassLoader();
        Class<?>[] interfaces = {serviceClass};
        Object proxyInstance = Proxy.newProxyInstance(
                classLoader,// 类加载器
                interfaces,// 接口
                (proxy, method, args) -> {
                    // 代理类, 代理方法, 方法参数
                    // 代理方法执行时的逻辑

                    send(serviceClass, method, args);
                    // TODO
                    return null /* method.invoke(proxy, (Object[]) method.getParameters())*/;
                }
        );
        return (T) proxyInstance;
    }

    private static void send(Class<?> serviceClass, Method method, Object[] args) throws InterruptedException {
        // 1. 将方法调用转换为RpcRequestMessage
        RpcRequestMessage message = new RpcRequestMessage(
                serviceClass.getName(),
                method.getName(),
                method.getReturnType(),
                method.getParameterTypes(),
                args
        );
        // 2. 将消息发送
        Channel channel = RpcClientManager.getClientChannel();
        ChannelFuture future = channel.writeAndFlush(message);
        future.addListener(f->{
            if(f.isSuccess()){
                log.info("请求发送成功");
            }else {
                Throwable cause = f.cause();
                log.error(cause.getMessage(),cause);
                log.error("请求发送失败");
            }
        });
    }
}
```

### 从RpcResponseMessageHandler获取结果

我们知道, 在RpcResponseMessageHandler获取结果的, 都是和发起请求的不是同一条线程

怎么从另一个线程获取响应结果?

使用Promise, 在多个线程之际之间获取数据

-   服务端

```java
package com.harvey.netty.client.handler;

import ....

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 20:43
 */
@Slf4j
@ChannelHandler.Sharable // 记录了状态, 共享了变量, 但是有线程安全问题吗? 没有. 
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    /**
     * 选择<code>ConcurrentHashMap<code/>保证线程安全
     */
    private static final Map<Long, Promise<Object>> SEQUENCE_ID_PROMISE_MAP = new ConcurrentHashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage message) {
        long sequenceId = message.getSequenceId();

        // 即时删除Map中无用的promise, Remove还会返回删除的值
        Promise<Object> promise = SEQUENCE_ID_PROMISE_MAP.remove(sequenceId);

        if (promise == null) {
            return;
        }
        if (message.isSuccess()) {
            Object data = message.getData();
            promise.setSuccess(data);
        } else {
            promise.setFailure(message.getCause());
        }
        // promise set 之后, 就会唤醒await()
        ctx.writeAndFlush(message);
    }

    public static void putPromise(long sequenceId,Promise<Object> promise){
        RpcResponseMessageHandler.SEQUENCE_ID_PROMISE_MAP.put(sequenceId,promise);
    }
}
```

-   客户端

```java
private static Object syncReceive(Long sequenceId, Channel channel) {
    // 准备一个空的Promise对象, 来接收结果
    DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
    // 放入Map
    RpcResponseMessageHandler.putPromise(sequenceId, promise);
    Object result;

    try {
        // 等待响应
        promise.await(); // sync()会自动抛异常, await不会自动抛异常
        // 我们要自己通过Success方法来检查
        if (promise.isSuccess()) {
            result = promise.get();
        } else {
            throw promise.cause();
        }
    } catch (Throwable e) {
        throw new RuntimeException(e);
    }
    return result;
}
```

