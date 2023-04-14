package com.lechat.client.handler;


import com.lechat.client.entity.message;
import com.lechat.client.util.Constants;
import com.lechat.client.util.StringUtil;
import com.lechat.common.MessageType;
import com.lechat.entity.ChatMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

public class msgHandler implements msgHandle{
    private final static Logger LOGGER = LoggerFactory.getLogger(msgHandler.class);
    @Override
    public boolean checkMsg(String msg) {
        if (StringUtil.isEmpty(msg)) {
            LOGGER.warn("不能发送空消息！");
            return true;
        }
        return false;
    }

    @Override
    public void sendMsg(String msg, Channel ch) {
        String[] cmd = StringUtil.split(msg);
        Integer len = cmd.length;
        if (cmd[0].equals("registry") || cmd[0].equals("reg")){
//            message m = new message(Constants.REGISTRY_CODE, msg, null, null);
            ChatMessage m = new ChatMessage(MessageType.REGISTRY_REQ, msg, null);
            BinaryWebSocketFrame frame = new BinaryWebSocketFrame(m.getBuf());
            LOGGER.debug("reg!!!");
            ch.writeAndFlush(frame);
        }


    }


}
