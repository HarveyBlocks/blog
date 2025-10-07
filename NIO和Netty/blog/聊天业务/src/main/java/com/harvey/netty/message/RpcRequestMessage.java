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
