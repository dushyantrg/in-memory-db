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
import java.util.concurrent.atomic.AtomicInteger;
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

    public Map<String, String> cleanUpLogsAndcreateMap() throws IOException {
        final Optional<File> logFileOptional = getLogFile();
        Map<String, String> map = null;
        AtomicInteger linesCount = new AtomicInteger();
        if(logFileOptional.isPresent()) {
            try(BufferedReader reader = new BufferedReader(new FileReader(logFileOptional.get()))) {
                map = reader.lines()
                        .peek(line -> linesCount.getAndIncrement())
                        .map(this::getRecordFromText)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toConcurrentMap(Record::getKey, Record::getValue, (val1, val2) -> val2));

            }
            catch (IOException ex) {
                System.out.println("Error occurred while converting log file contents into records");
            }

            if(map != null && linesCount.get() > map.size()) {
                compactLogWithDistinctEntries(map);
            }

            return map;
        }

        return new ConcurrentHashMap<>(initialMapCapacity);
    }

    private void compactLogWithDistinctEntries(Map<String, String> map) throws IOException {

        Path newLogPath = Path.of(logDirectory, logFileName+".new");
        String cleanedLogFilePath = newLogPath.toString();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(cleanedLogFilePath))){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                writer.write(new Record(key, value).toString());
            }
        }
        catch (IOException ex){
            System.out.println("Error occurred while writing cleaned log file");
            ex.printStackTrace();
        }

        try{
            Path originalLogPath = Path.of(logDirectory, logFileName);
            Path originalsCopyLogPath = Path.of(logDirectory, logFileName+ ".old");
            Files.move(originalLogPath, originalsCopyLogPath);
            Files.move(newLogPath, originalLogPath);
            Files.deleteIfExists(originalsCopyLogPath);
        }
        catch(IOException ex) {
            System.out.println("Error occurred while replacing old log file with cleaned log file");
            throw ex;
        }
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
