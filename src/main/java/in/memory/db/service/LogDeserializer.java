package in.memory.db.service;

import in.memory.db.model.Record;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class LogDeserializer {

    private final String logDirectory;

    private final String logFileName;

    private final int initialMapCapacity;

    public LogDeserializer(@Value("${aol.dir}") String logDirectory, @Value("${aol.filename}") String logFileName,
                           @Value("${aol.initialMapCapacity}") int initialMapCapacity) {
        this.logDirectory = logDirectory;
        this.logFileName = logFileName;
        this.initialMapCapacity = initialMapCapacity;
    }

    public Map<String, String> createMapFromLogs() {
        final Optional<File> logFileOptional = getLogFile();

        if(logFileOptional.isPresent()) {
            try(BufferedReader reader = new BufferedReader(new FileReader(logFileOptional.get()))) {
                return reader.lines()
                        .map(this::getRecordFromText)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toConcurrentMap(Record::getKey, Record::getValue, (val1, val2) -> val2));
            }
            catch (IOException ex) {
                System.out.println("Error occurred while converting log file contents into records");
                ex.printStackTrace();
            }
        }

        return new ConcurrentHashMap<>(initialMapCapacity);
    }

    private Optional<Record> getRecordFromText(String line) {
        String[] tokens = line.split("::");
        if(tokens.length >= 2)
            return Optional.of(new Record(tokens[0], tokens[1]));
        return Optional.empty();
    }

    private Optional<File> getLogFile() {

        Path logFilePath = Paths.get(logDirectory, logFileName);
        if(Files.exists(logFilePath)) {
            return Optional.of(new File(logFilePath.toUri()));
        }
        return Optional.empty();
    }
}
