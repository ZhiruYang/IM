package com.lechat.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
public class JedisConnectionFactory {
    private static final JedisPool jp;
    static {
        JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setMaxTotal(8);
        jpc.setMaxIdle(8);
        jpc.setMinIdle(0);
        jpc.setMaxWait(Duration.ofMillis(200));
        jp = new JedisPool(jpc, "localhost", 6379, 1000, "123456");
    }
    @Bean
    public static Jedis getJedis(){
        return jp.getResource();
    }
}
