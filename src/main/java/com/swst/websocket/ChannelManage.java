package com.swst.websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChannelManage {


    // web 推送 channel 管理
    private static ChannelManage ourInstance = null;
    public static ChannelGroup channelGroup= new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static ChannelManage getInstance() {
        if (ourInstance==null){
            ourInstance =  new ChannelManage();
        }
        return ourInstance;
    }

    public static void send2all(BinaryWebSocketFrame str) {
        channelGroup.writeAndFlush(str);
    }

    public static void add(Channel channel) {
        channelGroup.add(channel);
    }

    public static void remove(Channel channel) {
        channelGroup.remove(channel);
    }


    private ChannelManage() {
    }
}
