package com.kvstore.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ExternalHttpServer {
    public static void main(String[] args) throws Exception {

        String nodeName = args[0];
        int httpPort = Integer.parseInt(args[1]);

        // Start the HTTP server
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(httpPort), 0);

        // Define endpoints
        httpServer.createContext("/", new RootHandler());
        httpServer.createContext("/status", new StatusHandler());

        // Start the server
        httpServer.setExecutor(null); // Default executor
        httpServer.start();
        System.out.println("HTTP server started on port " + httpPort);
    }

    // Root handler ("/")
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Welcome to the External HTTP Server!";
            exchange.sendResponseHeaders(200, response.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Status handler ("/status")
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Server is running and listening for HTTP requests.";
            exchange.sendResponseHeaders(200, response.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}