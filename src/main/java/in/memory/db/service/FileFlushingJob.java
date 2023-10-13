package in.memory.db.service;

import in.memory.db.model.Record;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Singleton
public class FileFlushingJob {
    private final KeyValueStore keyValueStore;

    private final int bufferSize;

    private final Path logFilePath;

    private final Path newLogFilePath;

    private final Path oldLogFilePath;

    private long lastUpdateTime;

    public FileFlushingJob(KeyValueStore keyValueStore, @Value("${aol.dir}") String logDirectory, @Value("${aol.filename}") String logFileName,
                           @Value("${aol.bufferSize}") int bufferSize) {
        this.keyValueStore = keyValueStore;
        this.bufferSize = bufferSize;
        this.newLogFilePath = Paths.get(logDirectory, logFileName+".new");
        this.logFilePath = Paths.get(logDirectory, logFileName);
        this.oldLogFilePath = Paths.get(logDirectory, logFileName+".old");
    }

    @Scheduled(fixedDelay = "${aol.flush-interval}")
    void createSnapshotFromInMemoryData() throws IOException {
        long startTime = System.nanoTime(), endTime;
        long lastUpdateTime = keyValueStore.getLastUpdateTime();
        if(this.lastUpdateTime >= lastUpdateTime) {
            endTime = System.nanoTime();
            System.out.printf("Time taken to run createSnapshotFromInMemoryData is %s nanoseconds\n", endTime - startTime);
            return;
        }
        this.lastUpdateTime = lastUpdateTime;

        if(createNewSnapshotFromInMemoryData()) {
            replaceOldLogsWithNewLogFile();
        }
        endTime = System.nanoTime();
        System.out.printf("Time taken to run createSnapshotFromInMemoryData with updates is %s nanoseconds\n", endTime - startTime);
    }

    private boolean createNewSnapshotFromInMemoryData() {
        Map<String, String> map = keyValueStore.getData();
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(newLogFilePath.toUri())), bufferSize)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                bufferedWriter.write(new Record(key, value).toString());
            }
            bufferedWriter.flush();
            return true;
        }
        catch (IOException ex) {
            System.out.println("Error occurred while creating snapshot file");
            ex.printStackTrace();
        }
        return false;
    }

    private void replaceOldLogsWithNewLogFile() throws IOException {
        try{
            if(Files.exists(logFilePath)) {
                Files.move(logFilePath, oldLogFilePath);
            }
            Files.move(newLogFilePath, logFilePath);
            Files.deleteIfExists(oldLogFilePath);
        }
        catch(IOException ex) {
            System.out.println("Error occurred while replacing old log file with new log file");
            ex.printStackTrace();
        }
    }

    private File getLogFile(String logDirectory, String logFileName) throws IOException {
        final File logFile;
        Path logFilePath = Paths.get(logDirectory, logFileName);
        if(Files.exists(logFilePath)) {
            logFile = new File(logFilePath.toUri());
        }
        else {
            Files.createDirectories(Paths.get(logDirectory));
            Files.createFile(logFilePath);
            logFile = logFilePath.toFile();
        }
        return logFile;
    }
}
