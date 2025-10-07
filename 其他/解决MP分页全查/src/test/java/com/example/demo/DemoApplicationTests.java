package com.example.demo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private UserService userService;
    @Test
    void contextLoads() {
        Page<User> page = new Page<>(1, 10,15,true);
        Page<User> userPage = userService.page(page);
        System.out.println(userPage.getRecords().size());
    }

}
