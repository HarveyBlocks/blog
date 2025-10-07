package com.harvey.netty.server.handler;

import com.harvey.netty.message.RpcRequestMessage;
import com.harvey.netty.message.RpcResponseMessage;
import com.harvey.netty.server.service.HelloService;
import com.harvey.netty.server.service.ServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-31 20:43
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        RpcResponseMessage response = new RpcResponseMessage(message.getSequenceId());
        response.setSequenceId(message.getSequenceId());
        try {
            Class<?> interfaceClass = Class.forName(message.getInterfaceName());
            HelloService service = (HelloService)
                    ServiceFactory.getService(interfaceClass);
            Method method = interfaceClass.getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());
            response.setData(invoke);
            response.setCode(200);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            response.setMsg(400,e);
        } catch (InvocationTargetException e) {
            String msg = "服务端发生异常:" + e.getMessage();
            response.setMsg(500, new Exception(msg, e));
        }
        ctx.writeAndFlush(response);
    }
}
