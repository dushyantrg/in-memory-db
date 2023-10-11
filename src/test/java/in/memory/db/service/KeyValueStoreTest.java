package in.memory.db.service;

import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class KeyValueStoreTest {


    @Value("${aol.dir}")
    private  String logDirectory;

    @Value("${aol.filename}")
    private  String logFileName;

    @Inject
    private  KeyValueStore keyValueStore;

    @AfterEach
    public void cleanup() throws IOException {
        Files.deleteIfExists(Path.of(logDirectory, logFileName));
    }


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

        assertEquals("Key Not Found", keyValueStore.getValue("foo"));
    }
}
