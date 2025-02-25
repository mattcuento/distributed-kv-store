package com.kvstore.server;

import com.kvstore.core.DurableKeyValueStore;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ExternalHttpServer {
    private final DurableKeyValueStore store;
    private final HttpServer server;

    public ExternalHttpServer(String nodeName, int httpPort, DurableKeyValueStore store) throws IOException {
        this.store = store;
        this.server = HttpServer.create(new InetSocketAddress(httpPort), 0);

        // Define endpoints
        server.createContext("/get", new GetHandler());
        server.createContext("/put", new PutHandler());

        server.setExecutor(null); // Default executor
    }

    public void start() {
        server.start();
        System.out.println("HTTP server started on port " + server.getAddress().getPort());
    }

    // GET handler ("/get?key=someKey")
    class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, "Invalid request method", 405);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.startsWith("key=")) {
                sendResponse(exchange, "Missing 'key' parameter", 400);
                return;
            }

            String key = query.substring(4); // Extract key value
            String value = store.get(key);

            if (value == null) {
                sendResponse(exchange, "Key not found", 404);
            } else {
                sendResponse(exchange, value, 200);
            }
        }
    }

    // PUT handler ("/put?key=someKey&value=someValue")
    class PutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, "Invalid request method", 405);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.contains("key=") || !query.contains("&value=")) {
                sendResponse(exchange, "Missing 'key' or 'value' parameter", 400);
                return;
            }

            String[] params = query.split("&");
            String key = params[0].split("=")[1];
            String value = params[1].split("=")[1];

            store.put(key, value);
            sendResponse(exchange, "Stored successfully", 200);
        }
    }

    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}