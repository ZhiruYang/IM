package com.lechat;

import com.lechat.entity.User;
import com.lechat.util.TokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisTemplateApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private RedisTemplate<String, Object> Template;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("name", "lili");
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }

//    @Test
//    void saveUserTest() {
//        User user = new User("ggb", "123456");
//        Template.opsForValue().set("user:100", user);
//        user = (User) Template.opsForValue().get("user:100");
//        System.out.println(user.getUsername());
//    }

    @Test
    void registryTest() {
        User user = new User("ggb", "123456");
        String token = TokenUtil.newToken();
        stringRedisTemplate.opsForValue().set("user:" + user.getUsername(), user.getPassword());

        stringRedisTemplate.opsForValue().set("token:" + token, user.getUsername());
        String s = stringRedisTemplate.opsForValue().get("token:" + token);
        System.out.println(s);

    }
    @Test
    void stringTest(){
        String s = ":send yy; wrnm";
        System.out.println(s.substring(s.indexOf(";") + 1));
    }
}
