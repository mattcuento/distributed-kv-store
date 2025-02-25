package com.kvstore.server;

import com.kvstore.core.DurableKeyValueStore;

public class KVNode {
    private final DurableKeyValueStore backingStore;

    public KVNode(String nodeName) {
        this.backingStore = new DurableKeyValueStore(nodeName, 100);
    }

    public void startServers(String nodeName, String httpPort, String tcpPort) {
        // Start HTTP server thread with DurableKeyValueStore
        new Thread(() -> {
            try {
                ExternalHttpServer server = new ExternalHttpServer(nodeName, Integer.parseInt(httpPort), backingStore);
                server.start();
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

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java KVNode <nodeName> <httpPort> <tcpPort>");
            return;
        }

        String nodeName = args[0];
        String httpPort = args[1];
        String tcpPort = args[2];

        KVNode node = new KVNode(nodeName);
        node.startServers(nodeName, httpPort, tcpPort);
    }
}