package com.harvey.netty.message;

import lombok.Data;
import lombok.ToString;


/**
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 18:58
 */
@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    private String id;
    private String password;
    private String username;

    public LoginRequestMessage() {
    }

    public LoginRequestMessage( String username,String password) {
        if(username==null||username.isEmpty()){
            System.err.println("用户名输入有误");
            throw new NullPointerException();
        }
        if(password==null||password.isEmpty()){
            System.err.println("密码输入有误");
            throw new NullPointerException();
        }
        this.username = username;
        this.password = password;
    }

    @Override
    public byte getMessageType() {
        return LOGIN_REQUEST_MESSAGE;
    }
}
