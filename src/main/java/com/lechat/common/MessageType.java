package com.lechat.common;


public enum MessageType {
    SUCCESS(200),
    FAIL(400),
    REGISTRY_REQ(100),
    REGISTRY_FAIL(101),

    NOT_FOUND(404);


    private final Integer code;

    private MessageType(Integer code){
        this.code = code;
    }

    public Integer getCode(){
        return code;
    }

}
