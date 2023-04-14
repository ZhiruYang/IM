package com.lechat.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil implements Serializable {

    private static final String signature = "admin";

    public void getToken(String username){
        JwtBuilder jb = Jwts.builder();
        String jwtToken = jb
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .claim("username", username)
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*30))
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, signature)
                .compact();
        System.out.println(jwtToken);
    }

    public void parse(String token){
//        token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InRvbSIsInJvbGUiOiJhZG1pbiIsImV4cCI6MTY4MDM2MTU5MywianRpIjoiMDhiY2EzYjAtODJiOC00MTAwLWEyZmEtOTg0ZmVmMjc5MzUzIn0.zUtNilMsePVzoYnMnwVqI26CmLvApumBsrIWJ7Z7kkM";
        JwtParser jp = Jwts.parser();
        Jws<Claims> claimsJws = jp.setSigningKey(signature).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        System.out.println((claims.get("username")));

    }

    public static boolean checkToken(String token){
        if(token == null)
            return false;
        JwtParser jp = Jwts.parser();
        try {
            Jws<Claims> claimsJws = jp.setSigningKey(signature).parseClaimsJws(token);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
