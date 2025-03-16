package com.kvstore.core;

import java.util.Iterator;

public abstract class IRecoverableDatabaseStrategy implements IDatabaseAPI {
    protected final ICrashRecoveryStrategy snapshotController;

    public IRecoverableDatabaseStrategy(String nodeName, ICrashRecoveryStrategy snapshotController) {
        this.snapshotController = snapshotController;
        this.applyRecoveryOperations(this.snapshotController.getRecoveryOperations(nodeName));
    }

    void applyRecoveryOperations(Iterator<Operation> operationIterator) {
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            switch (operation.operationType) {
                case DELETE -> this.delete(operation.key);
                case PUT -> this.put(operation.key, operation.value);
            }
        }
    }
}
