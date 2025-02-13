package com.kvstore.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kvstore.config.DeploymentConfig;
import com.kvstore.config.NodeConfig;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.ArrayList;
  import java.util.List;

  public class MasterCoordinator {

      private final String classPath = System.getProperty("java.class.path");
      private final String javaHome = System.getProperty("java.home");  // Get the Java executable path
      String javaExec = javaHome + File.separator + "bin" + File.separator + "java";

      static class Node {
          String name;
          Process process;
          int httpPort;
          int tcpPort;

          Node(String name, Process process, int httpPort, int tcpPort) {
              this.name = name;
              this.process = process;
              this.httpPort = httpPort;
              this.tcpPort = tcpPort;
          }
      }

      private final List<Node> nodes = new ArrayList<>();

      public void launchNode(String name, String classString) throws IOException {
          int httpPort = getFreePort();
          int tcpPort = getFreePort();
          ProcessBuilder processBuilder = new ProcessBuilder(
                  javaExec, "-cp", classPath, classString, name, String.valueOf(httpPort), String.valueOf(tcpPort)
          );
          processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
          processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
          Process process = processBuilder.start();  // Launch node as a separate process
          nodes.add(new Node(name, process, httpPort, tcpPort));
          System.out.println("Launched node: " + name);
      }

      public void monitorNodes() {
          nodes.forEach(node -> {
              if (!node.process.isAlive()) {
                  System.err.println("Node " + node.name + " is down!");
              }
          });
      }

      public static void main(String[] args) throws Exception {
          MasterCoordinator master = new MasterCoordinator();

          ObjectMapper mapper = new ObjectMapper();
          try {
              File file = Paths.get("src/main/java/resources/deployment-config.json").toFile();
              // Read the JSON file into an array of NodeConfig objects
              DeploymentConfig deploymentConfig = mapper.readValue(file, DeploymentConfig.class);
              for (NodeConfig node : deploymentConfig.nodes) {
                  master.launchNode(node.getName(), node.getCommand());
              }
          } catch (IOException e) {
              e.printStackTrace();
          }

          while (true) {
              master.monitorNodes();
              Thread.sleep(5000); // Adjust monitoring frequency
          }
      }

      // Method to find a free port
      private int getFreePort() {
          try (ServerSocket socket = new ServerSocket(0)) {
              return socket.getLocalPort(); // Returns a free port
          } catch (IOException e) {
              throw new RuntimeException("No free ports available!", e);
          }
      }
  }