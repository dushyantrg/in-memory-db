package in.memory.db.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
public class DataControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    public void shouldReturnValueForAnExists() {
        HttpRequest<?> request = HttpRequest.GET("/keys/key1").accept(MediaType.TEXT_PLAIN);
        String body = client.toBlocking().retrieve(request);

        assertNotNull(body);
        assertEquals("key1", body);
    }

    @Test
    public void shouldAddAValueForANewKey() {
        HttpRequest<?> request = HttpRequest.PUT("/keys/key1/value/value1", null).accept(MediaType.TEXT_PLAIN);
        String body = client.toBlocking().retrieve(request);

        assertNotNull(body);
        assertEquals("value1", body);
    }
}
