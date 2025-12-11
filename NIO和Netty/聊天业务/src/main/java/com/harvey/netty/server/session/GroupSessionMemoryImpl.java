package com.harvey.netty.server.session;

import com.harvey.netty.message.*;
import com.harvey.netty.server.service.UserServiceFactory;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
public class GroupSessionMemoryImpl implements GroupSession {
    private final Map<String, Group> groupMap = new ConcurrentHashMap<>();

    @Override
    public Group createGroup(String name, Set<String> members) {
        Map<String, String> map = UserServiceFactory.getUserService().allUserMap();
        // 用户不存在就删除
        members.removeIf(member -> map.get(member) == null);
        if (exist(name)){
            return null;
        }
        Group group = new Group(name, members);
        groupMap.put(name, group);
        return group;
    }

    @Override
    public Group joinMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, group) -> {
            group.getMembers().add(member);
            return group;
        });
    }

    /**
     * TODO 在某个客户端下线时调用
     * @param name   组名
     * @param member 成员名
     */
    @Override
    public Group removeMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().remove(member);
            return value;
        });
    }

    @Override
    public Group removeGroup(String name) {
        return groupMap.remove(name);
    }

    @Override
    public Set<String> getMembers(String name) {
        return groupMap.getOrDefault(name, Group.EMPTY_GROUP).getMembers();
    }

    @Override
    public List<Channel> getMembersChannel(String name) {
        return getMembers(name).stream()
                .map(member -> SessionFactory.getSession().getChannel(member))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private List<Channel> getMembersChannelFilterFrom(String groupName,String from) {
        return getMembers(groupName).stream()
                .filter((member)-> !member.equals(from))// 只有不是发送信息的用户的不会被广播到
                .map(member -> SessionFactory.getSession().getChannel(member))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public void broadcast(GroupChatResponseMessage groupMessage) {
        String groupName = groupMessage.getGroupName();
        for (Channel channel : getMembersChannel(groupName)) {
            channel.writeAndFlush(groupMessage);
        }
    }

    @Override
    public void broadcastFilterFrom(GroupChatResponseMessage groupMessage) {
        String groupName = groupMessage.getGroupName();
        String from = groupMessage.getFrom();
        for (Channel channel : getMembersChannelFilterFrom(groupName,from)) {
            channel.writeAndFlush(groupMessage);
        }
    }
    @Override
    public void broadcastFilterFrom(GroupQuitRequestMessage groupMessage,String reason) {
        String groupName = groupMessage.getGroupName();
        String from = groupMessage.getUsername();
        for (Channel channel : getMembersChannelFilterFrom(groupName,from)) {
            channel.writeAndFlush(new GroupQuitResponseMessage(false,reason,0));
        }
    }
    @Override
    public void broadcastFilterFrom(GroupJoinRequestMessage groupMessage,String reason) {
        String groupName = groupMessage.getGroupName();
        String from = groupMessage.getUsername();
        for (Channel channel : getMembersChannelFilterFrom(groupName,from)) {
            channel.writeAndFlush(new GroupJoinResponseMessage(true,reason,0));
        }
    }
    @Override
    public boolean exist(String groupName) {
        return groupMap.containsKey(groupName);
    }
}
