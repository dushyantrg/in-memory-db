package in.memory.db.controller;

import in.memory.db.service.KeyValueStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Controller("/keys")
public class DataController {

    private final KeyValueStore keyValueStore;

    public DataController(KeyValueStore keyValueStore){
        this.keyValueStore = keyValueStore;
    }

    @Get("/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Mono<String> getValueForKey(@PathVariable(name= "key") String key)  {
        return Mono.just(this.keyValueStore.getValue(key));
    }

    @Put("/{key}/value/{value}")
    public Mono<Void> updateValueForKey(@PathVariable(name= "key") String key, @PathVariable(name= "value") String value) throws IOException {
        this.keyValueStore.putValue(key, value);
        return Mono.empty();
    }
}
