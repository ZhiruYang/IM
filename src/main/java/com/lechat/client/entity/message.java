package com.lechat.client.entity;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class message implements Serializable {
    private Integer code;
    private String msg;
    private Object content;
    private String token;

    public BinaryWebSocketFrame build(){
        return new BinaryWebSocketFrame();
    }
}
