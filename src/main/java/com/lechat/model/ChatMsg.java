package com.lechat.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMsg implements Serializable {
    private String senderId;
    private String receiverId;
    private String msg;
    private String msgId;
}
