package in.memory.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapSpeedEstimate {

//    public void printHashMapInsertTimes(String baseString, long count) {
//        List<String> keys = getIncreasingStrings(count);
//        List<String> values = keys;
//        Map<String, String> map = new ConcurrentHashMap<>();
//        long startTime = System.nanoTime();
//        for (int i = 0; i < count; i++) {
//            map.put(keys.get(i), values.get(i));
//        }
//        System.out.printf("Time taken to insert %s pairs is %s nanoseconds%n", count, System.nanoTime() - startTime);
//
//        startTime = System.nanoTime();
//        for (int i = 0; i < count; i++) {
//            map.get(keys.get(i));
//        }
//        System.out.printf("Time taken to fetch  %s pairs is  %s nanoseconds%n", count, System.nanoTime() - startTime);
//    }

    public void printFileAppendTimes(String baseString, int count) throws IOException {
        List<String> randomStrings2K = getRandomStrings2K(baseString, count);
        Path path = Files.createTempFile("temp", "123");
        System.out.println("File created at "+path);
        File f = new File(path.toUri());
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
        long startTime = System.nanoTime();
        for (int i = 0; i < count; i++) {
            writer.write(randomStrings2K.get(i));
        }
        writer.flush();
        System.out.printf("Time taken to append %s lines is %s nanoseconds%n with size %s\n", count, System.nanoTime() - startTime, f.length());
        writer.close();
        Files.delete(path);
    }

//    public void printHashMapInsertTimes(String baseString, long count) {
//        Map<String, String> map = new ConcurrentHashMap<>();
//        long startTime = System.nanoTime();
//        for (int i = 0; i < count; i++) {
//            map.put(i+"", i+"");
//        }
//        System.out.printf("Time taken to insert %s pairs is %s nanoseconds%n", count, System.nanoTime() - startTime);
//
//        startTime = System.nanoTime();
//        for (int i = 0; i < count; i++) {
//            map.get(i+"");
//        }
//        System.out.printf("Time taken to fetch  %s pairs is  %s nanoseconds%n", count, System.nanoTime() - startTime);
//    }

    public void printHashMapInsertTimes(String baseString, long count) {
        List<String> keys = getRandomStrings(baseString, count);
        List<String> values = getRandomStrings(baseString, count);
        Map<String, String> map = new ConcurrentHashMap<>();
        long startTime = System.nanoTime();
        for (int i = 0; i < count; i++) {
            map.put(keys.get(i), values.get(i));
        }
        System.out.printf("Time taken to insert %s pairs is %s nanoseconds%n", count, System.nanoTime() - startTime);

        startTime = System.nanoTime();
        for (int i = 0; i < count; i++) {
            map.get(keys.get(i));
        }
        System.out.printf("Time taken to fetch %s pairs is %s nanoseconds%n", count, System.nanoTime() - startTime);
    }

    private List<String> getRandomStrings(String baseString, long count) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(String.format("%s%s", baseString, UUID.randomUUID()));
        }
        return list;
    }

    private List<String> getRandomStrings2K(String baseString, long count) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(String.format("%s%s%s%s", baseString, UUID.randomUUID(), baseString, UUID.randomUUID()));
        }
        return list;
    }

    private List<String> getIncreasingStrings(long count) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(String.format("%s", i));
        }
        return list;
    }
}
