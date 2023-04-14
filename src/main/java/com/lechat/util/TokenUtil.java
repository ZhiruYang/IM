package com.lechat.util;

import java.util.UUID;

public class TokenUtil {
    public static String newToken(){
        return UUID.randomUUID().toString();
    }
}
