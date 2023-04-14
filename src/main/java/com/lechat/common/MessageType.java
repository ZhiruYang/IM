package com.lechat.common;


public enum MessageType {
    SUCCESS(200),

    REGISTRY_REQ(100),
    LOGIN_REQ(101),
    LOGOUT_REQ(102),

    SEND(300),
    RECORD(301),

    FAIL(400),
    NOT_FOUND(404),
    IGNORE(500);

    private final Integer code;

    MessageType(Integer code){
        this.code = code;
    }

    public Integer getCode(){
        return code;
    }

}
