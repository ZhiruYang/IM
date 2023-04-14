package com.lechat.entity;

import com.lechat.common.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage implements Serializable {
    private MessageType messageType;
    private String msg;
    private Object content;

    public ByteBuf getBuf() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        ByteBuf buf = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            byte[] data = bos.toByteArray();
            buf = Unpooled.copiedBuffer(data);
        } catch (Exception e){
            e.printStackTrace();
        }
        return buf;
    }

    public static ChatMessage restore(byte[] data) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ChatMessage ret;
        try{
            ObjectInput in = new ObjectInputStream(bis);
            ret = (ChatMessage) in.readObject();
        } finally {
            bis.close();
        }
        return ret;
    }
}
