package com.kvstore.server;

import com.kvstore.core.IDatabaseAPI;
import com.kvstore.core.InMemoryRecoverableDatabase;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class KVNode {
    private final IDatabaseAPI databaseInstance;

    public KVNode(String nodeName, IDatabaseAPI databaseInstance) throws IOException {
        System.out.println("Initializing " + nodeName);
        this.databaseInstance = databaseInstance;
    }

    public void startServers(String nodeName, String httpPort, String tcpPort) {
        // Start HTTP server thread with DurableKeyValueStore
        new Thread(() -> {
            try {
                ExternalHttpServer server = new ExternalHttpServer(nodeName, Integer.parseInt(httpPort), databaseInstance);
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

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (args.length < 3) {
            System.err.println("Usage: java KVNode <nodeName> <httpPort> <tcpPort>");
            return;
        }

        String nodeName = args[0];
        String httpPort = args[1];
        String tcpPort = args[2];
        String databaseClassName = args[3];

        Class<? extends IDatabaseAPI> databaseClass= (Class<? extends IDatabaseAPI>) Class.forName(databaseClassName);

        Class<?>[] parameterTypes = {String.class};

        KVNode node = new KVNode(nodeName, databaseClass.getDeclaredConstructor(parameterTypes).newInstance(nodeName));
        node.startServers(nodeName, httpPort, tcpPort);
    }
}