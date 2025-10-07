package com.harvey.netty.server.session;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
public abstract class GroupSessionFactory {

    private static final GroupSession SESSION = new GroupSessionMemoryImpl();

    public static GroupSession getGroupSession() {
        return SESSION;
    }
}
