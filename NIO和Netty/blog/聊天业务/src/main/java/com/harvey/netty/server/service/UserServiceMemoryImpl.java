package com.harvey.netty.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
public class UserServiceMemoryImpl implements UserService {
    private final Map<String, String> allUserMap = new ConcurrentHashMap<>();

    {
        allUserMap.put("zhangsan", "123");
        allUserMap.put("lisi", "123");
        allUserMap.put("wangwu", "123");
        allUserMap.put("zhaoliu", "123");
        allUserMap.put("qianqi", "123");
    }

    @Override
    public Map<String, String> allUserMap(){
        return allUserMap;
    }
    @Override
    public Boolean login(String username, String password) {
        String pass = allUserMap.get(username);
        if (pass == null) {
            return null;
        }
        return pass.equals(password);
    }
}
