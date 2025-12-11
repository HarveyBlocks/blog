package com.harvey.netty.server.service;

import lombok.Getter;

/**
 * 用户管理接口
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
public abstract class UserServiceFactory {

    private static final UserService USER_SERVICE = new UserServiceMemoryImpl();
    public static UserService getUserService(){
        return USER_SERVICE;
    }
}
