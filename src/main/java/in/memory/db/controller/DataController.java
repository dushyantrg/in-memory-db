package in.memory.db.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.io.UnsupportedEncodingException;

@Controller("/keys")
public class DataController {

    @Get("/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getValueForKey(@PathVariable(name= "key") String key)  {
        return key;
    }

    @Put("/{key}/value/{value}")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateValueForKey(@PathVariable(name= "key") String key, @PathVariable(name= "value") String value) {
        return value;
    }
}
