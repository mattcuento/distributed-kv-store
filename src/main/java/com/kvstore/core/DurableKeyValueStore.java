package com.kvstore.core;

import java.time.Instant;
import java.util.HashMap;

public class DurableKeyValueStore {

    private final HashMap<String, String> valueMap;

    private final WriteAheadLog writeAheadLog;

    public DurableKeyValueStore(String nodeName, int capacity) {
        this.valueMap = new HashMap<>(); // add capacity
        this.writeAheadLog = new WriteAheadLog(nodeName + Instant.now().toEpochMilli());
    }

    public String get(String key) {
        String maybeValue = this.valueMap.get(key);
        if (maybeValue != null) {
            return maybeValue;
        }
        // check disk
        return null;
    }

    public boolean put(String key, String value) {
        String maybeValue = this.valueMap.get(key);
        if (maybeValue != null) {
            // clear from mem and disk?
        }
        // check disk
        this.valueMap.put(key, value);
        return true;
    }
}
