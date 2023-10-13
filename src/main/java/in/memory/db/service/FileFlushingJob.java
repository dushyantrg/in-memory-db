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

    private final File logFile;

    private final File newLogFile;

    private final File oldLogFile;

    public FileFlushingJob(KeyValueStore keyValueStore, @Value("${aol.dir}") String logDirectory, @Value("${aol.filename}") String logFileName,
                           @Value("${aol.bufferSize}") int bufferSize) throws IOException {
        this.keyValueStore = keyValueStore;
        this.bufferSize = bufferSize;
        this.newLogFile = getLogFile(logDirectory, logFileName+".new");
        this.logFile = getLogFile(logDirectory, logFileName);
        this.oldLogFile = getLogFile(logDirectory, logFileName+".old");
    }

    @Scheduled(fixedDelay = "${aol.flush-interval}")
    void createSnapshotFromInMemoryData() throws IOException {
        if(createNewSnapshotFromInMemoryData()) {
            replaceOldLogsWithNewLogFile();
        }
    }

    private boolean createNewSnapshotFromInMemoryData() {
        Map<String, String> map = keyValueStore.getData();
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newLogFile), bufferSize)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                bufferedWriter.write(new Record(key, value).toString());
            }
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
            if(logFile.exists()) {
                Files.move(logFile.toPath(), oldLogFile.toPath());
            }
            Files.move(newLogFile.toPath(), logFile.toPath());
            Files.deleteIfExists(oldLogFile.toPath());
        }
        catch(IOException ex) {
            System.out.println("Error occurred while replacing old log file with new log file");
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
