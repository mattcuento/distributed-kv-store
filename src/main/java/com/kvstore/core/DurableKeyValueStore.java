package com.kvstore.core;

import java.time.Instant;
import java.util.HashMap;

public class DurableKeyValueStore {

    private final HashMap<Byte[], Byte[]> valueMap;

    private final WriteAheadLog writeAheadLog;

    public DurableKeyValueStore(String nodeName, int capacity) {
        this.valueMap = new HashMap<>(); // add capacity
        this.writeAheadLog = new WriteAheadLog(nodeName + Instant.now().toEpochMilli());
    }

    public Byte[] get(Byte[] key) {
        Byte[] maybeValue = this.valueMap.get(key);
        if (maybeValue != null) {
            return maybeValue;
        }
        // check disk
    }

    public boolean put(Byte[] key, Byte[] value) {
        Byte[] maybeValue = this.valueMap.get(key);
        if (maybeValue != null) {
            // clear from mem and disk?
        }
        // check disk
        this.valueMap.put(key, value);
        return true;
    }
}
