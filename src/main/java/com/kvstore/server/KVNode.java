package com.kvstore.server;

public class KVNode {
    public static void main(String[] args) {
        // Start HTTP server thread
        new Thread(() -> {
            try {
                ExternalHttpServer.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Start TCP server thread
        new Thread(() -> {
            try {
                InternalTcpServer.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("Both HTTP and TCP servers are running...");
    }
}