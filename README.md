# Overview

# How to Run

`make startApp` will run the gradle application with entry on the `MasterCoordinator` class.
The `MasterCoordinator` will read the `deployment-config.json` file, find free ports, and spin up instances of `KVNode`
accordingly.

Once up and running, the external HTTP ports will be printed on the console. Use these ports for commands to target 
specific KV instances. 

To put a new key value pair: `make putKeyValue PORT=<port_number> KEY=<key_string> VALUE=<value_string>`
To get a value for a given key: `make getKey PORT=<port_number> KEY=<key_string>`
To delete a key if it exists: `make deleteKey PORT=<port_number> KEY=<key_string>`

# Architecture

## Deployment Strategy

## Sharding

## Replication

## Durability

## Crash Recovery

WAL & Snapshotting

## Hard Failure Recovery

Re-partitioning

# Failure Scenarios

Simulating Network Partitions

# Simulation Suite

# Roadmap

## Complete
- [x] Implement `KVNode` with basic key-value operations
- [x] Implement `MasterCoordinator` to manage `KVNode` instances
- [x] Implement `KVNode` heartbeats from `MasterCoordinator`
- [x] Implement WAL 

## In Progress
- [ ] Implement snapshotting
- 

## Future
- [ ] Node process cleanup
- [ ] API Server to decouple Nodes from storage requests
- [ ] Sharding
- [ ] Replication
- [ ] Durability - Writing to disk
- [ ] Simulation of network partitions


## Optimizations
- [ ] Compressed WAL/Snapshots
- [ ] Direct I/O
- [ ] Non-http heartbeat?
- [ ] Streaming WAL to Snapshot while it's being taken in case of concurrent updates
- [ ] Configure data and snapshot directories