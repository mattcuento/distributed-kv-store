package com.kvstore.core;

import com.kvstore.OperationType;

import java.util.Iterator;

public interface ICrashRecoveryStrategy {
    void recordOperationToWal(OperationType operationType, String key, String value);

    Iterator<Operation> getRecoveryOperations(String nodeName);
}
