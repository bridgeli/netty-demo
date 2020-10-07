package cn.bridgeli.netty.demo.server.netty;

import io.netty.channel.Channel;

import java.util.HashMap;

/**
 * @author bridgeli
 */
public class ChannelRepository {
    private HashMap<String, Channel> channelCache = new HashMap<>();

    public ChannelRepository put(String key, Channel value) {
        channelCache.put(key, value);
        return this;
    }

    public Channel get(String key) {
        return channelCache.get(key);
    }

    public void remove(String key) {
        this.channelCache.remove(key);
    }

    public int size() {
        return this.channelCache.size();
    }
}
