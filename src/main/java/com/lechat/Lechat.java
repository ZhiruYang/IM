package com.lechat;

import com.lechat.server.IMServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lechat {
    public static void main(String[] args) throws Exception {

        SpringApplication.run(Lechat.class, args);
        IMServer.start();
    }
}
