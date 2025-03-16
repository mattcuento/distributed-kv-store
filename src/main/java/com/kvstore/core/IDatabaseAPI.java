package com.kvstore.core;

public interface IDatabaseAPI {
    void put(String key, String value);

    String  get(String key);

    void delete(String key);

}
