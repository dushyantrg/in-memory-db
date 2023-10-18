package in.memory.db.service;

import io.micronaut.context.annotation.Context;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.Map;

@Context
public class KeyValueStore {

    private final AppendOnlyLog appendOnlyLog;
    private final LogDeserializer logDeserializer;
    private Map<String, String> map;

    @Inject
    public KeyValueStore(AppendOnlyLog appendOnlyLog, LogDeserializer logDeserializer) throws IOException {
        this.appendOnlyLog = appendOnlyLog;
        this.logDeserializer = logDeserializer;
        long startTime = System.nanoTime();
        this.map = this.logDeserializer.cleanUpLogsAndcreateMap();
        long endTime = System.nanoTime();
        System.out.printf("Time taken to deserialize log in map is %s%n",endTime-startTime);
    }

    public String getValue(String key) {
        return map.getOrDefault(key, "Key Not Found");
    }

    public synchronized void putValue(String key, String value) throws IOException {
        this.map = appendOnlyLog.add(key, value, map);
        map.put(key, value);
    }
}
