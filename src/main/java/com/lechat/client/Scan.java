package com.lechat.client;

import com.lechat.client.handler.ClientHandler;
import com.lechat.client.handler.msgHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Scan implements Runnable{
    private final static Logger LOGGER = LoggerFactory.getLogger(Scan.class);
    private static final String URL = System.getProperty("url", "ws://127.0.0.1:9999");
    private msgHandler msgHandler = new msgHandler();

    @Override
    public void run() {
        URI uri = null;
        Channel ch = null;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            uri = new URI(URL);
            Integer port = uri.getPort();

            final ClientHandler clientHandler =
                    new ClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
                                    WebSocketClientCompressionHandler.INSTANCE,
                                    clientHandler
                            );
                        }
                    });

            ch = b.connect(uri.getHost(), port).sync().channel();
            clientHandler.handshakeFuture().sync();

            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (true) {

                String msg = null;
                try {
                    msg = console.readLine();
                    //ping
                    if (msg.toLowerCase().equals("ping")){
                        WebSocketFrame frame = new PingWebSocketFrame(
                                Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
                        ch.writeAndFlush(frame);
                        LOGGER.debug("ppp");
                        continue;
                    }
                    //quit
                    else if (msg.toLowerCase().equals("quit")){
                        ch.writeAndFlush(new CloseWebSocketFrame());
                        ch.closeFuture().sync();
                        continue;
                    }
                    //发送消息
                    msgHandler.sendMsg(msg, ch) ;
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

//    @Override
//    public void run() {
//        URI uri = null;
//        try {
//            uri = new URI(URL);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//        Integer port = uri.getPort();
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            final ClientHandler handler =
//                    new ClientHandler(
//                            WebSocketClientHandshakerFactory.newHandshaker(
//                                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));
//
//            Bootstrap b = new Bootstrap();
//            b.group(group)
//                    .channel(NioSocketChannel.class)
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) {
//                            ChannelPipeline p = ch.pipeline();
//                            p.addLast(
//                                    new HttpClientCodec(),
//                                    new HttpObjectAggregator(8192),
//                                    WebSocketClientCompressionHandler.INSTANCE,
//                                    handler
//                            );
//                        }
//                    });
//
//            Channel ch = b.connect(uri.getHost(), port).sync().channel();
//            handler.handshakeFuture().sync();
//
//            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
//            while (true) {
//                String msg = null;
//                try {
//                    msg = console.readLine();
//                    if (msg == null) {
//                        break;
//                    } else if ("bye".equals(msg.toLowerCase())) {
//                        ch.writeAndFlush(new CloseWebSocketFrame());
//                        ch.closeFuture().sync();
//                        break;
//                    } else if ("ping".equals(msg.toLowerCase())) {
//                        WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
//                        ch.writeAndFlush(frame);
//                    } else {
//                        ChatMessage chatMessage = new ChatMessage(2, "Dololo");
//                        WebSocketFrame bf = new BinaryWebSocketFrame(chatMessage.getBuf());
//                        ch.writeAndFlush(bf);
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        } finally {
//            group.shutdownGracefully();
//        }
//    }
}
