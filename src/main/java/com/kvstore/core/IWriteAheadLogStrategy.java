package com.kvstore.core;

import com.kvstore.OperationType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface IWriteAheadLogStrategy {
    void recordOperation(OperationType operationType, String key, String value);
    BufferedReader getWalBufferedReader() throws FileNotFoundException;
    void takeSnapshot() throws IOException;
    void truncateWal() throws IOException;
    void closeWalWriter() throws IOException;
    void setNewWalWriter();
}
