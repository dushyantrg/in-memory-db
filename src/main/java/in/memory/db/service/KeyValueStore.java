package in.memory.db.service;

import io.micronaut.context.annotation.Context;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.Map;

@Context
public class KeyValueStore {
    private final LogDeserializer logDeserializer;
    private final Map<String, String> map;

    @Inject
    public KeyValueStore(LogDeserializer logDeserializer) throws IOException {
        this.logDeserializer = logDeserializer;
        long startTime = System.nanoTime();
        this.map = this.logDeserializer.cleanUpLogsAndcreateMap();
        long endTime = System.nanoTime();
        System.out.printf("Time taken to deserialize log in map is %s%n",endTime-startTime);
    }

    public String getValue(String key) {
        return map.getOrDefault(key, "Key Not Found");
    }

    public void putValue(String key, String value) throws IOException {
        map.put(key, value);
    }

    public Map<String, String> getData(){
        return map;
    }
}
