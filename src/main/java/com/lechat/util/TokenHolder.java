package com.lechat.util;

public class TokenHolder {
    private static String token;

    public static void saveToken(String s){
        token = s;
    }
    public static String getToken(){
        return token;
    }
    public static void deleteToken(){
        token = null;
    }
}
