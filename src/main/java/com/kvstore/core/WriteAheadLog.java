package com.kvstore.core;

import com.kvstore.OperationType;
import java.io.*;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;

public class WriteAheadLog {
    private final File walFile;
    private final BufferedWriter bufferedWriter;
    private final ReentrantLock fileLock;

    public WriteAheadLog(String filename) {
        this.walFile = Paths.get(filename).toFile();
        this.fileLock = new ReentrantLock();
        try {
            walFile.createNewFile();
            this.bufferedWriter = new BufferedWriter(new FileWriter(walFile, true));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create WAL file: " + filename, e);
        }
    }

    public void recordOperation(OperationType operationType, String key, String value) {
        try {
            this.fileLock.lock();
            String logEntry = operationType + "," + key + "," + (value != null ? value : "") + "\n";
            bufferedWriter.write(logEntry);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to WAL file: " + walFile.getAbsolutePath(), e);
        } finally {
            this.fileLock.unlock();
        }
    }

    public File takeSnapshot() throws IOException {
        try {
            this.fileLock.lock();
            FileReader fileReader = new FileReader(walFile);
            fileReader.read()
        } finally {
            this.fileLock.unlock();
        }
    }

    public void closeWriter() throws IOException {
        bufferedWriter.close();
    }
}