package com.harvey.netty.client.handler;

import com.harvey.netty.message.LoginRequestMessage;
import com.harvey.netty.message.LoginResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发送Login的请求
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:11
 */
public class ClientLoginHandler extends ChannelInboundHandlerAdapter {

    public static final Scanner SCANNER = new Scanner(System.in);
    private static final ExecutorService LOGIN_INPUT_EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 连接建立后执行, 发送登录请求
        LOGIN_INPUT_EXECUTOR.execute(
                () -> {
                    ctx.writeAndFlush(getLoginMessage());
                }
        );
    }


    private static LoginRequestMessage getLoginMessage() {
        LoginRequestMessage message;
        while (true) {
            String username = inputString("username");
            String password = inputString("password");
            try {
                message = new LoginRequestMessage(username, password);
                if (currentUsername == null) {
                    ClientLoginHandler.currentUsername = username;
                }
                // System.out.println(message);
                break;
            } catch (NullPointerException ignore) {
            }
        }
        return message;
    }

    private static String inputString(String hint) {
        System.out.print(hint+": ");
        String username = null;
        if (SCANNER.hasNext()) {
            username = SCANNER.next();
        }
        return username;
    }

    private static String currentUsername;

    public static String currentUsername() {
        return currentUsername;
    }

    @Slf4j
    public static class ResponseHandler extends SimpleChannelInboundHandler<LoginResponseMessage> {
        private static final ExecutorService COMMAND_INPUT_EXECUTOR = Executors.newSingleThreadExecutor();

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, LoginResponseMessage msg) throws Exception {
            if (!msg.isSuccess()) {
                // 失败
                System.err.println(msg.getReason());
                ctx.writeAndFlush(getLoginMessage());
                return;
            }
            // 成功
            System.out.println(msg.getReason());
            printHelp();
            COMMAND_INPUT_EXECUTOR.execute(
                    () -> {
                        while (true) {
                            String[] split = inputCommand();
                            if (split == null || split.length == 0 || split[0].isEmpty()) {
                                continue;
                            }
                            log.debug(Arrays.toString(split));
                            int result = ParseCommand.doParse(split, ctx);
                            if (result == 0) {
                                break;
                            } else if (result == 2) {
                                printHelp();
                            }
                        }
                        ctx.channel().close();
                    }
            );
        }

        private static String[] inputCommand() {
            String[] split = null;
            if (SCANNER.hasNextLine()) {
                String command = SCANNER.nextLine();
                split = splitString(command);
            }
            return split;
        }

        /**
         * 双引号和单引号内的不被分割, 以逗号和空格分割
         */
        private static String[] splitString(String input) {
            List<String> result = new ArrayList<>();
            Pattern pattern = Pattern.compile("[^\\s\",']+|\"[^\"]*\"|'[^']*'");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                result.add(matcher.group());
            }
            return result.toArray(new String[0]);
        }

        private static void printHelp() {
            System.out.println("=========================================");
            System.out.println("注意输入指令时使用半角字符, 指令不区分大小写");
            System.out.println("=========================================");
            System.out.println("quit");
            System.out.println("help");
            System.out.println("send   username content");
            System.out.println("group  send     group_name  content");
            System.out.println("group  create   group_name  m1[,m2,m3...]");
            System.out.println("group  members  group_name");
            System.out.println("group  join     group_name");
            System.out.println("group  quit     group_name");
            System.out.println("=========================================");
        }
    }
}
