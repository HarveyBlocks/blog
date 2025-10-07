package com.harvey.netty.server.session;

import com.harvey.netty.message.OnlineOfflineMessage;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * 会话管理接口实现
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-02-29 22:19
 */
public class SessionMemoryImpl implements Session {

    private final Map<String, Channel> usernameChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, String> channelUsernameMap = new ConcurrentHashMap<>();
    private final Map<Channel,Map<String,Object>> channelAttributesMap = new ConcurrentHashMap<>();

    @Override
    public void bind(Channel channel, String username) {
        usernameChannelMap.put(username, channel);
        channelUsernameMap.put(channel, username);
        channelAttributesMap.put(channel, new ConcurrentHashMap<>());
    }

    @Override
    public void unbind(Channel channel) {
        String username = channelUsernameMap.remove(channel);
        usernameChannelMap.remove(username);
        channelAttributesMap.remove(channel);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return channelAttributesMap.get(channel).get(name);
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {
        channelAttributesMap.get(channel).put(name, value);
    }

    @Override
    public Channel getChannel(String username) {
        return usernameChannelMap.get(username);
    }

    @Override
    public String toString() {
        return usernameChannelMap.toString();
    }

    @Override
    public List<Channel> getOnlineUserChannels() {
        return usernameChannelMap.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


    @Override
    public void broadcast(OnlineOfflineMessage onlineMessage) {
        for (Channel channel : this.getOnlineUserChannels()) {
            channel.writeAndFlush(onlineMessage);
        }
    }

}
