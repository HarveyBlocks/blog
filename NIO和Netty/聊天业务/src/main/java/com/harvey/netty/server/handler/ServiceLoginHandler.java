package com.harvey.netty.server.handler;

import com.harvey.netty.message.LoginRequestMessage;
import com.harvey.netty.message.LoginResponseMessage;
import com.harvey.netty.message.OnlineOfflineMessage;
import com.harvey.netty.server.service.UserService;
import com.harvey.netty.server.service.UserServiceFactory;
import com.harvey.netty.server.session.Session;
import com.harvey.netty.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理登录请求
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
@ChannelHandler.Sharable
public class ServiceLoginHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        long sequenceId = msg.getSequenceId();
        UserService userService = UserServiceFactory.getUserService();
        Boolean login = userService.login(username,password);
        LoginResponseMessage respMsg;
        if(login == null){
            respMsg = new LoginResponseMessage(false,"该用户不存在",sequenceId);
        }else if(login) {
            respMsg = new LoginResponseMessage(true,"登录成功",sequenceId);
            Session session = SessionFactory.getSession();

            // 广播上线消息
            session.broadcast(new OnlineOfflineMessage(true,username));

            // 将该用户存入服务器缓存
            session.bind(ctx.channel(),username);
        }else {
            respMsg = new LoginResponseMessage(false,"用户名或密码错误",sequenceId);
        }
        ctx.writeAndFlush(respMsg);
    }



}