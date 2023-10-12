package in.memory.db.service;

import in.memory.db.model.Record;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Singleton
public class FileFlushingJob {
    private final KeyValueStore keyValueStore;

    private final int bufferSize;

    private final File logFile;

    public FileFlushingJob(KeyValueStore keyValueStore, @Value("${aol.dir}") String logDirectory, @Value("${aol.filename}") String logFileName,
                           @Value("${aol.bufferSize}") int bufferSize) throws IOException {
        this.keyValueStore = keyValueStore;
        this.bufferSize = bufferSize;
        this.logFile = getLogFile(logDirectory, logFileName);
    }

    @Scheduled(fixedDelay = "${aol.flush-interval}")
    void flushFileToDisk() throws IOException {
        Map<String, String> map = keyValueStore.getData();

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile), bufferSize)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                bufferedWriter.write(new Record(key, value).toString());
            }
        }
        catch (IOException ex) {
            System.out.println("Error occurred while creating snapshot file");
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
