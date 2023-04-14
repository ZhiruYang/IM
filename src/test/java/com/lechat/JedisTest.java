package com.lechat;

import redis.clients.jedis.Jedis;

public class JedisTest {
    private Jedis jedis;
    void setup() {
        jedis = new Jedis("localhost", 6379);
        jedis.auth("123456");
        jedis.select(0);
    }

    @org.junit.Test
    void testString(){
        System.out.println("fuck");
        setup();

        String result = jedis.set("name", "lili");
        System.out.println(result);

        String name = jedis.get("name");
        System.out.println(name);

        tearDown();
    }

    void tearDown(){
        if (jedis != null)
            jedis.close();
    }
}
