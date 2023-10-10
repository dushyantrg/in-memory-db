package in.memory.db.service;

import in.memory.db.model.Record;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Singleton
public class AppendOnlyLog {

    private final String logDirectory;

    private final String logFileName;

    private final File logFile;

    private final BufferedWriter bufferedWriter;

    private long lastUpdateTime;

    public AppendOnlyLog(@Value("${aol.dir}") String logDirectory, @Value("${aol.filename}") String logFileName) throws IOException {
        this.logDirectory = logDirectory;
        this.logFileName = logFileName;

        logFile = getLogFile(logDirectory, logFileName);
        bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
        lastUpdateTime = System.nanoTime();
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

    public void add(String key, String value) throws IOException {
        bufferedWriter.write(new Record(key, value).toString());
        lastUpdateTime = System.nanoTime();
        //System.out.printf("Key %s added at %s%n", key, lastUpdateTime);
    }

    public void flushToDisk() throws IOException {
        bufferedWriter.flush();
    }

    public boolean isInitialized() {
        return lastUpdateTime > 0;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}
