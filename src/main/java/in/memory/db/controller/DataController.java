package in.memory.db.controller;

import in.memory.db.service.KeyValueStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

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
    public String getValueForKey(@PathVariable(name= "key") String key)  {
        return this.keyValueStore.getValue(key);
    }

    @Put("/{key}/value/{value}")
    public void updateValueForKey(@PathVariable(name= "key") String key, @PathVariable(name= "value") String value) throws IOException {
        this.keyValueStore.putValue(key, value);
    }
}
