package in.memory.db.model;

import java.util.UUID;

public class Record {

    private final String key;
    private final String value;

    public Record(String key, String value, String id) {
        this.key = key;
        this.value = value;
    }

    public Record(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s::%s\n", key, value);
    }
}
