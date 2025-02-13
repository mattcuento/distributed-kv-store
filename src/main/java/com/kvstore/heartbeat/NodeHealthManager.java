package com.kvstore.heartbeat;

import java.util.HashMap;
  import java.util.Map;

  public class NodeHealthManager {
      private final Map<String, Long> lastHeartbeatTimestamps = new HashMap<>();

      public void registerHeartbeat(String nodeName) {
          lastHeartbeatTimestamps.put(nodeName, System.currentTimeMillis());
          System.out.println("Received heartbeat from: " + nodeName);
      }

      public void checkHealth() {
          long currentTime = System.currentTimeMillis();
          lastHeartbeatTimestamps.forEach((nodeName, timestamp) -> {
              if (currentTime - timestamp > 10000) { // 10 seconds threshold
                  System.err.println("Node " + nodeName + " is not responding.");
              }
          });
      }
  }