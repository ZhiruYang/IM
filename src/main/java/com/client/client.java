package com.client;

public class client {

    public static void main(String[] args) throws Exception {
        Scan scan = new Scan();
        Thread thread = new Thread(scan);
        thread.setName("scan-thread");
        thread.start();
    }
}
