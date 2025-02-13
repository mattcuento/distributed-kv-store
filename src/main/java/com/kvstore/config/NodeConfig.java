package com.kvstore.config;

public class NodeConfig {
    private String name;
    private String command;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    @Override
    public String toString() {
        return "NodeConfig{name='" + name + "', command='" + command + "'}";
    }
}