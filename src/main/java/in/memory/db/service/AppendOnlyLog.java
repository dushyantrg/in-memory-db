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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class AppendOnlyLog {

    private final String logDirectory;

    private final String logFileName;

    private File logFile;

    private BufferedWriter bufferedWriter;

    private final int bufferSize;

    private int logFileCount = 0;

    private long lastUpdateTime;

    private final AtomicInteger updateCount = new AtomicInteger();
    private final int appendCountThreshold = 30_000;

    public AppendOnlyLog(@Value("${aol.dir}") String logDirectory, @Value("${aol.filename}") String logFileName,
        @Value("${aol.bufferSize}") int bufferSize) throws IOException {
        this.bufferSize = bufferSize;
        this.logDirectory = logDirectory;
        this.logFileName = logFileName;

        logFile = getLogFile(logDirectory, logFileName);
        bufferedWriter = new BufferedWriter(new FileWriter(logFile, true), bufferSize);
        lastUpdateTime = System.nanoTime();
        addFileCleanupHook();
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

    public Map<String, String> add(String key, String value, Map<String, String> map) throws IOException {

        int updatesCount = updateCount.addAndGet(1);
        if(updatesCount > appendCountThreshold) {
            createSSTable(0, map);
            createNewLogFile();
            logFileCount++;
            updateCount.set(1);
            bufferedWriter.write(new Record(key, value).toString());
            lastUpdateTime = System.nanoTime();
            return new ConcurrentHashMap<>();
        }
        bufferedWriter.write(new Record(key, value).toString());
        lastUpdateTime = System.nanoTime();
        return map;
    }

    private void createSSTable(int level, Map<String, String> map) throws IOException {

        File ssTable = getLogFile(logDirectory, logFileCount+".ldb");

        TreeMap<String, String> treeMap = new TreeMap<>(map);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(ssTable))) {
            for (Map.Entry<String, String> entry: treeMap.entrySet()) {
                writer.write(new Record(entry.getKey(), entry.getValue()).toString());
            }
            writer.flush();
        }
        catch (IOException ex) {
            System.out.println("Error occurred creating ss table");
        }
    }

    private void createNewLogFile() throws IOException {
        this.bufferedWriter.close();

        Files.move(logFile.toPath(), Path.of(logDirectory, logFileName+logFileCount+".log"));
        logFile = getLogFile(logDirectory, logFileName);
        bufferedWriter = new BufferedWriter(new FileWriter(logFile), bufferSize);
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

    private void addFileCleanupHook() {
        Runtime.getRuntime().addShutdownHook(getProfilingReportPrintingThread());
    }

    private Thread getProfilingReportPrintingThread() {
        return new Thread(() -> {
            try {
                this.bufferedWriter.close();
            } catch (IOException e) {
                System.out.println("Error occurred while closing bufferedWriter");
                e.printStackTrace();
            }
        });
    }

}
