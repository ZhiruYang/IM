package com.lechat.handler;

import com.lechat.common.MessageType;
import com.lechat.entity.ChatMessage;
import com.lechat.entity.User;
import com.lechat.util.JedisConnectionFactory;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

import java.rmi.registry.Registry;

public class ServerMessageHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerMessageHandler.class);

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;
    @Autowired
    private RedisTemplate redisTemplate;

    public void parse(WebSocketFrame frame){
        try {
            byte[] data = ByteBufUtil.getBytes(frame.content());
            ChatMessage chatMessage = ChatMessage.restore(data);
            LOGGER.info("rcv: {}", chatMessage.getMsg());
            MessageType mt = chatMessage.getMessageType();
            switch (mt){
                case REGISTRY_REQ:
                    User user = (User) chatMessage.getContent();
                    Registry(user);
                    break;

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void Registry(User user){
        Jedis jedis = jedisConnectionFactory.getJedis();
        // set User
        redisTemplate.opsForValue().set("name", "lili");
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println(name);

    }
}
