package com.lechat.handler;

import com.lechat.common.Constant;
import com.lechat.common.MessageType;
import com.lechat.common.X;
import com.lechat.entity.ChatMessage;
import com.lechat.entity.User;
import com.lechat.util.SpringBeanFactory;
import com.lechat.util.StringUtil;
import com.lechat.util.TokenUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.concurrent.TimeUnit;

import static com.lechat.common.Constant.LOGIN_PREFIX;
import static com.lechat.common.Constant.USER_PREFIX;

@Controller
@Slf4j
public class ServerMessageHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerMessageHandler.class);

    private StringRedisTemplate redisTemplate;

    public ChatMessage parse(ChannelHandlerContext ctx, WebSocketFrame frame){
        redisTemplate = SpringBeanFactory.getBean(StringRedisTemplate.class);

        ChatMessage ret = new ChatMessage();
        ret.setMessageType(MessageType.FAIL);

        try {
            byte[] data = ByteBufUtil.getBytes(frame.content());
            ChatMessage chatMessage = ChatMessage.restore(data);
            String msg = chatMessage.getMsg();
            String[] cmd = StringUtil.split(msg);
            LOGGER.info("Server rcv cmd: {}", msg);
            LOGGER.info("rcv token: {}", chatMessage.getToken());

            MessageType mt = chatMessage.getMessageType();
            User user = null;
            String token = null;


            switch (mt){
                case REGISTRY_REQ:
                    user = new User(cmd[1], cmd[2]);
                    token = Registry(user);
                    if (token != null){
                        ret.setMessageType(MessageType.SUCCESS);
                        ret.setMsg("SUCCESS");
                        ret.setToken(token);
                        X.online(ctx.channel(), cmd[1]);
                    } else {
                        ret.setMsg("username occupied!");
                    }

                    break;
                case LOGIN_REQ:
                    user = new User(cmd[1], cmd[2]);
                    token = Login(user);
                    if (token != null){
                        ret.setMessageType(MessageType.SUCCESS);
                        ret.setMsg("SUCCESS");
                        ret.setToken(token);

                        X.online(ctx.channel(), cmd[1]);
                    } else {
                        ret.setMsg("user not registered!");
                    }
                    break;
                case LOGOUT_REQ:
                    if (check(chatMessage)){
                        Logout(chatMessage.getToken());
                        ret.setMsg("Logout!");
                        ret.setMessageType(MessageType.SUCCESS);
                    }else {
                        ret.setMsg("You didn't login");
                    }
                    break;
                case SEND:
                    if (check(chatMessage)){
                        String sender = redisTemplate.opsForValue().get(LOGIN_PREFIX + chatMessage.getToken());
                        ret.setMessageType(MessageType.IGNORE);

                        // is this a private chat?
                        if (cmd[1].endsWith(";")){
                            String target = cmd[1];
                            if (target != null && target.length() > 0) {
                                target = target.substring(0, target.length() - 1);
                            }
                            // find target user
                            if (redisTemplate.opsForValue().get(USER_PREFIX + target) == null ||
                                    X.getUserChannel(target) == null){
                                ret.setMessageType(MessageType.FAIL);
                                ret.setMsg("Target user is not online!");
                                break;
                            } else {
                                X.send(sender, target, msg.substring(msg.indexOf(";") + 1));
                            }
                        } else {
                            X.broadcast(sender, msg.substring(msg.indexOf(";") + 1));
                        }

                    } else {
                        ret.setMsg("please login before sending any message");
                    }
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    private String Registry(User user){
        // check username availability
        if (redisTemplate.opsForValue().get(USER_PREFIX + user.getUsername()) != null)
            return null;

        // save User to user table and login
        redisTemplate.opsForValue().set(USER_PREFIX + user.getUsername(), user.getPassword());
        String token = TokenUtil.newToken();
        redisTemplate.opsForValue().set(LOGIN_PREFIX + token, user.getUsername(), 60, TimeUnit.MINUTES);

        return token;
    }

    private String Login(User user){
        // check username availability
        if (redisTemplate.opsForValue().get(USER_PREFIX + user.getUsername()) == null)
            return null;

        String token = TokenUtil.newToken();
        redisTemplate.opsForValue().set(LOGIN_PREFIX + token, user.getUsername(), 60, TimeUnit.MINUTES);
        return token;
    }

    private void Logout(String token){
        redisTemplate.opsForValue().set(LOGIN_PREFIX + token, "", 1, TimeUnit.SECONDS);
    }

    private boolean check(ChatMessage chatMessage){
        return chatMessage.getToken() != null;
    }
}
