package com.lechat;

import com.lechat.util.JedisConnectionFactory;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.UUID;

@SpringBootTest
public class Test {

    private String signature = "admin";
    private Jedis jedis;
    @Autowired
    JedisConnectionFactory jedisConnectionFactory;
    @Autowired
    private RedisTemplate redisTemplate;

    @org.junit.Test
    public void jwt(){
        JwtBuilder jb = Jwts.builder();
        String jwtToken = jb
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .claim("username", "tom")
                .claim("role", "admin")
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*30))
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, signature)
                .compact();
        System.out.println(jwtToken);
    }

    @org.junit.Test
    public void parse(){
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InRvbSIsInJvbGUiOiJhZG1pbiIsImV4cCI6MTY4MDM2MTU5MywianRpIjoiMDhiY2EzYjAtODJiOC00MTAwLWEyZmEtOTg0ZmVmMjc5MzUzIn0.zUtNilMsePVzoYnMnwVqI26CmLvApumBsrIWJ7Z7kkM";
        JwtParser jp = Jwts.parser();
        Jws<Claims> claimsJws = jp.setSigningKey(signature).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        System.out.println((claims.get("username")));

    }

    @org.junit.Test
    public void jedis(){
        jedis = jedisConnectionFactory.getJedis();

        String result = jedis.set("name", "li");
        System.out.println(result);

        String name = jedis.get("name");
        System.out.println(name);

        jedis.close();

    }

    @org.junit.Test
    public void template(){
        redisTemplate.opsForValue().set("name", "lili");
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }
}
