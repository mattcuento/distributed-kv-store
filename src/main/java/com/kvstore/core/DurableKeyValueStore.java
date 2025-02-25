package com.kvstore.core;

import com.kvstore.OperationType;

import java.time.Instant;
import java.util.HashMap;

public class DurableKeyValueStore {

    private final HashMap<String, String> valueMap;

    private final WriteAheadLog writeAheadLog;

    public DurableKeyValueStore(String nodeName) {
        this.valueMap = new HashMap<>(); // add capacity
        this.writeAheadLog = new WriteAheadLog(nodeName + Instant.now().toEpochMilli());
    }

    public String get(String key) {
        return this.valueMap.get(key);
    }

    public void put(String key, String value) {
        this.writeAheadLog.recordOperation(OperationType.PUT, key, value);
        this.valueMap.put(key, value);
    }

    public void delete(String key) {
        this.writeAheadLog.recordOperation(OperationType.DELETE, key, null);
        this.valueMap.remove(key);
    }
}
