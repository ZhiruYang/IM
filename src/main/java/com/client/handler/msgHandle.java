package com.client.handler;

import io.netty.channel.Channel;

public interface msgHandle {

    void sendMsg(String msg, Channel ch);
}
