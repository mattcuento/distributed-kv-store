package com.kvstore.core;

import com.kvstore.OperationType;

import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SnapshotController implements ICrashRecoveryStrategy {
    private final IWriteAheadLogStrategy writeAheadLog;
    private final String snapshotFilePrefix;
    private final ScheduledExecutorService snapshotScheduler;

    public SnapshotController(String nodeName) throws IOException {
        this.snapshotFilePrefix = "snapshot-" + nodeName;
        this.writeAheadLog = new RawTextWriteAheadLog(nodeName, snapshotFilePrefix);
        this.snapshotScheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduleSnapshotExecution();
   }

    public void scheduleSnapshotExecution() {
        this.snapshotScheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        this.writeAheadLog.takeSnapshot();
                    } catch (IOException e) {
                        System.out.println("Failed to take snapshot!");
                        System.out.println(e.getMessage());
                    }
                },
                30,
                30,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void recordOperationToWal(OperationType operationType, String key, String value) {
        this.writeAheadLog.recordOperation(operationType, key, value);
    }

    @Override
    public Iterator<Operation> getRecoveryOperations(String nodeName) {
        File snapshotFile = Paths.get(this.snapshotFilePrefix + ".db").toFile();
        try {
            if (!snapshotFile.exists()) {
                snapshotFile.createNewFile();
            }
            BufferedReader snapshotBufferedReader = new BufferedReader(new FileReader(snapshotFile));
            BufferedReader walBufferedReader = this.writeAheadLog.getWalBufferedReader();

            return new Iterator<>() {
                String nextLine = readNextValidLine();
                boolean snapshotReaderOpen = true;
                boolean walReaderOpen = true;

                private String readNextValidLine() {
                    try {
                        String line;
                        while (snapshotReaderOpen && (line = snapshotBufferedReader.readLine()) != null) {
                            if (line.contains(",")) return line;
                        }
                        snapshotBufferedReader.close(); // Close reader once finished
                        snapshotReaderOpen = false;

                        while (walReaderOpen && (line = walBufferedReader.readLine()) != null) {
                            if (line.contains(",")) return line;
                        }
                        walBufferedReader.close();
                        walReaderOpen = false;
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read snapshot file" + e.getMessage(), e);
                    }
                    return null;
                }

                @Override
                public boolean hasNext() {
                    return nextLine != null;
                }

                @Override
                public Operation next() {
                    if (nextLine == null) {
                        throw new IllegalStateException("No more elements.");
                    }
                    String[] parts = nextLine.split(",");
                    Operation operation;
                    if (parts.length == 2) {
                        operation = new Operation(OperationType.PUT, parts[0], parts[1]);
                    } else if (parts.length == 3) {
                        operation = new Operation(parts[0], parts[1], parts[2]);
                    } else {
                        throw new IllegalArgumentException("Read invalid line from snapshot or WAL on recovery.");
                    }
                    nextLine = readNextValidLine();
                    return operation;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to rehydrate from snapshot.", e);
        }
    }
}
