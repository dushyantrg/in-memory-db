package in.memory.db.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class KeyValueStore {

    @Inject
    private AppendOnlyLog appendOnlyLog;
    private final Map<String, String> map = new ConcurrentHashMap<>();

    public String getValue(String key) {
        return map.getOrDefault(key, "");
    }

    public void putValue(String key, String value) throws IOException {
        appendOnlyLog.add(key, value);
        map.put(key, value);
    }
}
