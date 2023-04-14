package com.client.handler;

import com.lechat.common.MessageType;
import com.lechat.entity.ChatMessage;
import com.lechat.entity.User;
import com.lechat.util.StringUtil;
import com.lechat.util.TokenHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownServiceException;
@Slf4j
public class msgHandler implements msgHandle{
    private final static Logger LOGGER = LoggerFactory.getLogger(msgHandler.class);

    @Override
    public void sendMsg(String msg, Channel ch) {
        String[] cmd = StringUtil.split(msg);
        Integer len = cmd.length;
        ChatMessage ret = new ChatMessage(MessageType.FAIL, msg, null, null);
        boolean send_flg = true;

        switch (cmd[0].toLowerCase()){
            case ":registry":
            case ":reg":
                if (cmd.length != 3){
                    LOGGER.debug("Registry; Wrong argument number");
                    send_flg = false;
                    break;
                }
                ret.setMessageType(MessageType.REGISTRY_REQ);
                break;
            case ":login":
                if (cmd.length != 3){
                    LOGGER.debug("Login; Wrong argument number");
                    send_flg = false;
                    break;
                }
                ret.setMessageType(MessageType.LOGIN_REQ);
                break;
            case ":logout":
                ret.setMessageType(MessageType.LOGOUT_REQ);
                ret.setToken(TokenHolder.getToken());
                break;
            case ":send":
                if (cmd.length < 2){
                    LOGGER.debug("Send; You cannot send empty string");
                    send_flg = false;
                    break;
                }
                ret.setMessageType(MessageType.SEND);
                ret.setToken(TokenHolder.getToken());
                break;
            case ":record":
                ret.setMessageType(MessageType.RECORD);
                ret.setToken(TokenHolder.getToken());
                break;
        }

        if (send_flg){
            BinaryWebSocketFrame frame = new BinaryWebSocketFrame(ret.getBuf());
            ch.writeAndFlush(frame);
        }
    }
}
