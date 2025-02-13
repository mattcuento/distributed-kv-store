package com.kvstore.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class InternalTcpServer {
    public static void main(String[] args) {

        String nodeName = args[0];
        int tcpPort = Integer.parseInt(args[1]);

        try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
            System.out.println("TCP server listening on port " + tcpPort);

            while (true) {
                // Accept incoming connections from other nodes
                Socket socket = serverSocket.accept();
                System.out.println("Connected to a new server node.");

                // Handle communication in a separate thread
                new Thread(new NodeHandler(socket)).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

// Handler for each node connection
class NodeHandler implements Runnable {
    private Socket socket;

    public NodeHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message from node: " + message);
                
                // Respond to the node
                out.println("Echo from TCP server: " + message);

                // Break connection on specific command (optional)
                if ("exit".equalsIgnoreCase(message)) {
                    System.out.println("Node disconnected.");
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}