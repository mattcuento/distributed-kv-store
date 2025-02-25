package com.kvstore.core;

import com.kvstore.OperationType;
import java.io.*;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

public class WriteAheadLog {
    private final File walFile;
    private final BufferedWriter bufferedWriter;
    private final ReentrantLock fileLock;
    private final ScheduledExecutorService snapshotScheduler;

    public WriteAheadLog(String filename) {
        this.walFile = Paths.get(filename).toFile();
        this.fileLock = new ReentrantLock();
        this.snapshotScheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            walFile.createNewFile();
            this.bufferedWriter = new BufferedWriter(new FileWriter(walFile, true));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create WAL file: " + filename, e);
        }

//        this.snapshotScheduler.scheduleAtFixedRate();
        // todo schedule snapshotting
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
            File snapshotTmpFile = Paths.get("snapshot.tmp").toFile();
            File snapshotFile = Paths.get("snapshot.db").toFile();
            FileReader fileReader = new FileReader(walFile);
            try (
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    BufferedWriter snapshotBufferedWriter = new BufferedWriter(new FileWriter(snapshotTmpFile))
            ) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length < 3) continue;

                    String key = parts[1]; // todo extract current keys in mem

                    snapshotBufferedWriter.write(line);
                    snapshotBufferedWriter.newLine();
                }
                snapshotBufferedWriter.flush();
                if (!snapshotTmpFile.renameTo(snapshotFile)) {
                    throw new IOException("Failed to rename snapshot file from " + snapshotTmpFile + " to " + snapshotFile);
                }
                return snapshotFile;
            }
        } finally {
            this.fileLock.unlock();
        }
    }

    public void closeWriter() throws IOException {
        bufferedWriter.close();
    }
}