package com.kvstore.core;

import java.util.HashMap;

public class InMemoryStore implements IDatabaseAPI {

    private final HashMap<String, String> valueMap;

    public InMemoryStore() {
        this.valueMap = new HashMap<>();
    }

    @Override
    public void put(String key, String value) {
        this.valueMap.put(key, value);
    }

    @Override
    public String get(String key) {
        return this.valueMap.get(key);
    }

    @Override
    public void delete(String key) {
        this.valueMap.remove(key);
    }
}
