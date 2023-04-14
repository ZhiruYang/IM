package com.lechat.handler;

import com.lechat.common.MessageType;
import com.lechat.entity.ChatMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {

        ServerMessageHandler serverMessageHandler = new ServerMessageHandler();
        ChatMessage msg = serverMessageHandler.parse(ctx, frame);
        if (msg.getMessageType() != MessageType.IGNORE){
            ByteBuf buf = msg.getBuf();

            ctx.channel().writeAndFlush(
                    new BinaryWebSocketFrame(buf)

            );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
        clients.remove(ctx.channel());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
        LOGGER.info("channel added");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        clients.remove(ctx.channel());
    }

}
