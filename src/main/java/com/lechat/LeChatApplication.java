package com.lechat;

import com.lechat.server.IMServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LeChatApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LeChatApplication.class, args);
        IMServer.start();
    }

}
