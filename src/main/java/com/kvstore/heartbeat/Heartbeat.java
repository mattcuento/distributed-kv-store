package com.kvstore.heartbeat;// In ExternalHttpServer or InternalTcpServer
import java.net.HttpURLConnection;
import java.net.URL;

public class Heartbeat {
    public static void sendHeartbeat() {
        try {
            URL url = new URL("http://127.0.0.1:8080/heartbeat?node=http-server");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("com.kvstore.heartbeat.Heartbeat sent successfully.");
            }
        } catch (Exception e) {
            System.err.println("Failed to send heartbeat: " + e.getMessage());
        }
    }
}