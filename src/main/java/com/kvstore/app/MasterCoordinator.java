package com.kvstore.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kvstore.config.DeploymentConfig;
import com.kvstore.config.NodeConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
  import java.util.List;

  public class MasterCoordinator {

      static class Node {
          String name;
          Process process;

          Node(String name, Process process) {
              this.name = name;
              this.process = process;
          }
      }

      private final List<Node> nodes = new ArrayList<>();

      public void launchNode(String name, String command) throws IOException {
          ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
          processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
          processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
          Process process = processBuilder.start();  // Launch node as a separate process
          nodes.add(new Node(name, process));
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
  }