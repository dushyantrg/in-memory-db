package in.memory.db.model;

import java.util.UUID;

public class Record {

    private final String key;
    private final String value;
    private final String id;

    public Record(String key, String value, String id) {
        this.key = key;
        this.value = value;
        this.id = id;
    }

    public Record(String key, String value) {
        this.key = key;
        this.value = value;
        this.id = UUID.randomUUID().toString();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%s::%s::%s", id, key, value);
    }
}
