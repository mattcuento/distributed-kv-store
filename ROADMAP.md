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