package in.memory.db.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class KeyValueStoreTest {

    @Inject
    KeyValueStore keyValueStore;

    @Test
    public void shouldReturnAValueIfItsKeyExists() throws IOException {
        keyValueStore.putValue("newKey", "newValue");
        assertEquals("newValue", keyValueStore.getValue("newKey"));
    }

    @Test
    public void shouldInsertAValueForANewKey() throws IOException {
        keyValueStore.putValue("newKey", "newValue");
        assertEquals("newValue", keyValueStore.getValue("newKey"));
    }

    @Test
    public void shouldUpdateTheValueForAnExistingKey() throws IOException {
        keyValueStore.putValue("newKey", "newValue");
        keyValueStore.putValue("newKey", "updatedValue");
        assertEquals("updatedValue", keyValueStore.getValue("newKey"));
    }

    @Test
    public void shouldReturnEmptyStringIfKeyDoesNotExist() {

        assertEquals("", keyValueStore.getValue("foo"));
    }
}
