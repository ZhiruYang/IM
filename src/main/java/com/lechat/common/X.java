package com.lechat.common;

import com.lechat.entity.ChatMessage;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.concurrent.ConcurrentHashMap;

public class X {
    private static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    public static void online(Channel channel, String username){
        channelMap.put(username, channel);
    }

    public static Channel getUserChannel(String username){
        return channelMap.get(username);
    }

    public static void send(String sender, String target, String msg){
        ChatMessage ret = new ChatMessage();
        ret.setMessageType(MessageType.SEND);
        ret.setMsg("This is a private chat from " + sender + ": " + msg);
        Channel c = channelMap.get(target);
        c.writeAndFlush(
                new BinaryWebSocketFrame(ret.getBuf())
        );
    }

    public static void broadcast(String sender, String msg){
        ChatMessage ret = new ChatMessage();
        ret.setMessageType(MessageType.SEND);
        ret.setMsg("This is a broadcast from " + sender + ": " + msg);

        for (String user : channelMap.keySet()){
            Channel c = channelMap.get(user);
            c.writeAndFlush(
                    new BinaryWebSocketFrame(ret.getBuf())
            );
        }
    }

}
