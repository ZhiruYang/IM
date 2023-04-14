package com.lechat.client.handler;

import io.netty.channel.Channel;

public interface msgHandle {
    boolean checkMsg(String msg);

    void sendMsg(String msg, Channel ch);
}
