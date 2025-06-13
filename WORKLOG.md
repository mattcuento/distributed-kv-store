# Worklog

## OPEN BUGS
- Snapshots get incorrectly truncated when the WAL is empty. If I record an op from WAL -> snapshot file, then take a subsequent snapshot, the empty WAL somehow overwrites the snapshot.
  We need to ensure we're appending to the snapshot file from WAL while keeping it compacted OR take a new snapshot version that reads from both WAL and the last snapshot version.
- After the first snapshot for a node is taken, we get an empty reply from the server when trying to persist new KV pairs.

## 06-12-25
- Fixed a bug with snapshotting. When reading from the snapshot file buffer, once we reach EOF we close it. However, the iterator doesn't know that it's closed, so on a next() call, we try to read from it again. Added a flag to mark it as closed and avoid subsequent reads for snapshot reader and wal reader.
- Fixed a bug in Make command for persisting KV pair. The & in the query string needed to be escaped to be propagated through the curl call underneath.
- Fixed scheduled snapshot bug

## Next Priorities:
- Fix open bugs
- Create API server/start sharding
- Finish true heartbeat impl
- Disk DB implementation