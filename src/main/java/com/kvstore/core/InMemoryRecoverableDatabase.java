package com.kvstore.core;

import com.kvstore.OperationType;

import java.io.IOException;

public class InMemoryRecoverableDatabase extends IRecoverableDatabaseStrategy {

    private final InMemoryStore dataStore = new InMemoryStore();

    public InMemoryRecoverableDatabase(String nodeName) throws IOException {
        super(nodeName, new SnapshotController(nodeName));
    }

    public String get(String key) {
        return this.dataStore.get(key);
    }

    public void put(String key, String value) {
        this.snapshotController.recordOperationToWal(OperationType.PUT, key, value);
        this.dataStore.put(key, value);
    }

    public void delete(String key) {
        this.snapshotController.recordOperationToWal(OperationType.DELETE, key, null);
        this.dataStore.delete(key);
    }
}
