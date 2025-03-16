package com.kvstore.core;

import com.kvstore.OperationType;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;

public class RawTextWriteAheadLog implements IWriteAheadLogStrategy {
    private final ReentrantLock fileLock;
    private final File walFile;
    private final FileWriter walFileWriter;
    private final String snapshotFilePrefix;
    private BufferedWriter bufferedWalWriter;

    public RawTextWriteAheadLog(String nodeName, String snapshotFilePrefix) throws IOException {
        this.fileLock = new ReentrantLock();
        this.snapshotFilePrefix = snapshotFilePrefix;
        this.walFile = Paths.get("wal-" + nodeName).toFile();
        try {
            walFile.createNewFile();
            this.walFileWriter = new FileWriter(walFile, true);
            this.bufferedWalWriter = new BufferedWriter(this.walFileWriter);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create WAL file: " + nodeName, e);
        }

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

    public BufferedReader getWalBufferedReader() throws FileNotFoundException {
        FileReader fileReader = new FileReader(this.walFile);
        return new BufferedReader(fileReader);
    }

    public void takeSnapshot() throws IOException {
        try {
            this.fileLock.lock();
            File snapshotTmpFile = Paths.get(this.snapshotFilePrefix + "tmp").toFile();
            File snapshotFile = Paths.get(this.snapshotFilePrefix + ".db").toFile();
            FileReader walFileReader = new FileReader(walFile);
            try (
                    BufferedReader walBufferedReader = new BufferedReader(walFileReader);
                    BufferedWriter snapshotBufferedWriter = new BufferedWriter(new FileWriter(snapshotTmpFile))
            ) {
                String line;
                while ((line = walBufferedReader.readLine()) != null) {
                    snapshotBufferedWriter.write(line);
                    snapshotBufferedWriter.newLine();
                }
                snapshotBufferedWriter.flush();
                if (!snapshotTmpFile.renameTo(snapshotFile)) {
                    throw new IOException("Failed to rename snapshot file from " + snapshotTmpFile + " to " + snapshotFile);
                }
                this.truncateWal();
            }
        } finally {
            this.fileLock.unlock();
        }
    }

    public void truncateWal() throws IOException {
        this.closeWalWriter();
        try (RandomAccessFile raf = new RandomAccessFile(this.walFile.getPath(), "rw");
             FileChannel fileChannel = raf.getChannel()) {
            fileChannel.truncate(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setNewWalWriter();
    }

    public void closeWalWriter() throws IOException {
        bufferedWalWriter.close();
    }

    public void setNewWalWriter() {
        this.bufferedWalWriter = new BufferedWriter(this.walFileWriter);
    }
}