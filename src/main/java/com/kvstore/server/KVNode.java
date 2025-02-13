package com.kvstore.server;

import java.util.ArrayList;
import java.util.Arrays;

public class KVNode {
    public static void main(String[] args) {

        String nodeName = args[0];
        String httpPort = args[1];
        String tcpPort = args[2];

        // create string array of args 0 and 1

        // Start HTTP server thread
        new Thread(() -> {
            try {
                ExternalHttpServer.main(new String[]{nodeName, httpPort});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Start TCP server thread
        new Thread(() -> {
            try {
                InternalTcpServer.main(new String[]{nodeName, tcpPort});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("Both HTTP and TCP servers are running...");
    }
}