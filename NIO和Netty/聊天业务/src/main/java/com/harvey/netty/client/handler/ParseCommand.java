package com.harvey.netty.client.handler;

import com.harvey.netty.message.*;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.harvey.netty.client.handler.ClientLoginHandler.currentUsername;

/**
 * 解析命令
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-03-01 01:10
 */
@Slf4j
public class ParseCommand {
    /**
     * @return  0: 循环停止;1:循环继续; 2,输出帮助界面
     */
    public static int doParse(String[] split, ChannelHandlerContext ctx) {
        try {
            if (split.length < 1) {
                throw new IllegalArgumentException(Arrays.toString(split));
            }
            split[0] = split[0].toLowerCase();
            Message msg = null;
            switch (split[0]) {
                case "send":
                    if (split.length != 3) {
                        throw new IllegalArgumentException(Arrays.toString(split));
                    }
                    msg = new ChatRequestMessage(currentUsername(), split[1], split[2]);
                    break;
                case "quit":
                    return 1;
                case "help":
                    return 2;
                case "group":
                    split[1] = split[1].toLowerCase();
                    msg = parseGroupCommand(split);
                    break;
                default:
                    throw new UnknownCommandException(split[0]);
            }
            ctx.writeAndFlush(msg); // 不产生异常就发消息
        } catch (UnknownCommandException e) {
            System.err.println("unknown command with: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("illegal option counts:" + e.getMessage());
        }
        return 0; // 循环继续
    }

    private static Message parseGroupCommand(String[] split) {
        Message message;
        switch (split[1]) {
            case "send":
                if (split.length != 4) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                message = new GroupChatRequestMessage(currentUsername(), split[2], split[3]);
                break;
            case "create":
                if (split.length < 4) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                String[] members = Arrays.copyOfRange(split, 3, split.length);
                log.debug(Arrays.toString(members));
                // 对重复项会进行合并
                Set<String> memberSet = Arrays.stream(members).collect(Collectors.toSet());
                memberSet.add(currentUsername()); // 当前用户也会加入群聊(自动)
                message = new GroupCreateRequestMessage(split[2], memberSet);
                break;
            case "members":
                if (split.length != 3) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                message = new GroupMembersRequestMessage(split[2]);
                break;
            case "join":
                if (split.length != 3) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                message = new GroupJoinRequestMessage(currentUsername(), split[2]);
                break;
            case "quit":
                if (split.length != 3) {
                    throw new IllegalArgumentException(Arrays.toString(split));
                }
                message = new GroupQuitRequestMessage(currentUsername(), split[2]);
                break;
            default:
                throw new UnknownCommandException(split[1]);
        }
        return message;
    }




}

class UnknownCommandException extends IllegalArgumentException {
    public UnknownCommandException(String msg) {
        super(msg);
    }
}