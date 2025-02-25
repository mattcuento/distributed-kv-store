package com.kvstore.core;

import com.kvstore.OperationType;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class WriteAheadLog {
    private final ReentrantLock fileLock;
    private final File walFile;
    private final FileWriter walFileWriter;
    private final ScheduledExecutorService snapshotScheduler;
    private BufferedWriter bufferedWalWriter;

    public WriteAheadLog(String filename) throws IOException {
        this.fileLock = new ReentrantLock();
        this.walFile = Paths.get(filename).toFile();
        this.snapshotScheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            walFile.createNewFile();
            this.walFileWriter = new FileWriter(walFile, true);
            this.bufferedWalWriter = new BufferedWriter(this.walFileWriter);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create WAL file: " + filename, e);
        }

        this.snapshotScheduler.scheduleAtFixedRate(
            () -> {
                try {
                    this.takeSnapshot();
                } catch (IOException e) {
                   System.out.println("Failed to take snapshot!");
                }
            },
            600,
            600,
            TimeUnit.SECONDS
        );
    }

    public void recordOperation(OperationType operationType, String key, String value) {
        try {
            this.fileLock.lock();
            String logEntry = operationType + "," + key + "," + (value != null ? value : "") + "\n";
            bufferedWalWriter.write(logEntry);
            bufferedWalWriter.flush();
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
                this.truncateWal();
                return snapshotFile;
            }
        } finally {
            this.fileLock.unlock();
        }
    }

    private void truncateWal() throws IOException {
        this.closeWalWriter();
        try (RandomAccessFile raf = new RandomAccessFile(this.walFile.getPath(), "rw");
             FileChannel fileChannel = raf.getChannel()) {
            fileChannel.truncate(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setNewWalWriter();
    }

    private void closeWalWriter() throws IOException {
        bufferedWalWriter.close();
    }

    private void setNewWalWriter() throws IOException {
        this.bufferedWalWriter = new BufferedWriter(this.walFileWriter);
    }
}