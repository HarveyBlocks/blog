package com.harvey.netty.client.proxy;

import com.harvey.netty.client.RpcClientManager;
import com.harvey.netty.client.handler.RpcResponseMessageHandler;
import com.harvey.netty.message.RpcRequestMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
        if (!serviceClass.isInterface()) {
            throw new IllegalArgumentException(serviceClass.getName() + " is not an interface");
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
                    return sendAndReceive(serviceClass, method, args);
                }
        );
        return (T) proxyInstance;
    }

    private static Object sendAndReceive(Class<?> serviceClass, Method method, Object[] args) throws InterruptedException {
        // 1. 将方法调用转换为RpcRequestMessage
        RpcRequestMessage message = prepareMessage(serviceClass, method, args);
        // 2. 将消息发送
        Channel channel = RpcClientManager.getClientChannel();
        send(channel, message);
        return syncReceive(message.getSequenceId(), channel);
    }

    private static RpcRequestMessage prepareMessage(Class<?> serviceClass, Method method, Object[] args) {
        return new RpcRequestMessage(
                serviceClass.getName(),
                method.getName(),
                method.getReturnType(),
                method.getParameterTypes(),
                args
        );
    }

    private static void send(Channel channel, RpcRequestMessage message) {
        ChannelFuture future = channel.writeAndFlush(message);
        future.addListener(f -> {
            if (f.isSuccess()) {
                log.info("请求发送成功");
            } else {
                Throwable cause = f.cause();
                log.error(cause.getMessage(), cause);
                log.error("请求发送失败");
            }
        });
    }

    private static Object syncReceive(Long sequenceId, Channel channel)  {
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
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }
}
