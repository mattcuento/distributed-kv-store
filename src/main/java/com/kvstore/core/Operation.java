package com.kvstore.core;

import com.kvstore.OperationType;

public class Operation {
    public final OperationType operationType;
    public final String key;
    public final String value;

    public Operation(OperationType operationType, String key, String value) {
        this.operationType = operationType;
        this.key = key;
        this.value = value;
    }

    public Operation(String operationString, String key, String value) {
        this.operationType = OperationType.valueOf(operationString);
        this.key = key;
        this.value = value;
    }
}