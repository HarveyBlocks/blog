package com.harvey.netty.server.service;


import java.util.Map;

/**
 * 用户管理接口
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
public interface UserService {

    Map<String, String> allUserMap();

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回 true, 否则返回 false
     */
    Boolean login(String username, String password);
}
