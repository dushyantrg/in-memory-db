package in.memory.db.service;

import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class LogDeserializerTest {

    @Value("${aol.dir}")
    private  String logDirectory;

    @Value("${aol.filename}")
    private  String logFileName;

    @Inject
    private LogDeserializer logDeserializer;

    private Path targetPath;

    @BeforeEach
    @AfterEach
    public void cleanup() throws IOException {
        targetPath = Path.of(logDirectory, logFileName);
        Files.deleteIfExists(targetPath);
    }

    @Test
    public void shouldDeserializeLogFileIntoMap() throws IOException {
        Files.copy(Path.of(logDirectory, "testLog"), targetPath);
        Map<String, String> map = logDeserializer.cleanUpLogsAndcreateMap();
        assertNotNull(map, "Deserialized Map should not be null");
        assertEquals(39, map.size());
        assertEquals("newValueLast", map.get("newKey"));
        assertEquals("value-latest", map.get("key-37"));
        assertTrue(Files.exists(targetPath));
    }

    @Test
    public void shouldReturnEmptyMapIfFileIsEmpty() throws IOException {
        Files.createFile(targetPath);
        Map<String, String> map = logDeserializer.cleanUpLogsAndcreateMap();
        assertNotNull(map, "Deserialized Map should not be null");
        assertEquals(0,map.size());
    }
}
