package com.client.handler;

import com.lechat.entity.ChatMessage;
import com.lechat.util.TokenHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.data.redis.serializer.SerializationUtils;



public class ClientHandler extends SimpleChannelInboundHandler<Object> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public ClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                LOGGER.info("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                LOGGER.info("WebSocket Client failed to connect!");
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            System.out.println("WebSocket Client received message: " + textFrame.text());
        } else if (frame instanceof PongWebSocketFrame) {
            System.out.println("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("WebSocket Client received closing");
            ch.close();
        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame bf = (BinaryWebSocketFrame) frame;
            byte[] data = ByteBufUtil.getBytes(bf.content());
            ChatMessage chatMessage = ChatMessage.restore(data);
//            LOGGER.info("WebSocket Client received: {}", chatMessage.getMsg());

            switch (chatMessage.getMessageType()){
                case SUCCESS:
                    LOGGER.info("Success! {}", chatMessage.getMsg());
                    if (chatMessage.getToken() != null){
                        TokenHolder.saveToken(chatMessage.getToken());
                        LOGGER.info("Login success; my token is: {}", chatMessage.getToken());
                    }
                    break;
                case FAIL:
                    LOGGER.info("Fail! {}", chatMessage.getMsg());
                    break;
                case SEND:
                    LOGGER.info("receive msg from server: {}", chatMessage.getMsg());
                    break;

            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}