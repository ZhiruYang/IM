package com.lechat.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataContent implements Serializable {
    private Integer action;
    private ChatMsg chatMsg;
    private String extend;

}
