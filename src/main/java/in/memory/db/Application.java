package in.memory.db;

import io.micronaut.runtime.Micronaut;

import java.io.IOException;
import java.util.UUID;

public class Application {

    public static void main(String[] args) throws IOException {

        //printExecutionTimes();


        Micronaut.run(Application.class, args);
    }

    private static void printExecutionTimes() throws IOException {
        HashMapSpeedEstimate estimate = new HashMapSpeedEstimate();
        String baseString = "key0123456789012345678901234567890123456789012345678901234567890";
        long count = 10000000;
        estimate.printHashMapInsertTimes(baseString, count);
        estimate.printHashMapInsertTimes(baseString, count);
        estimate.printHashMapInsertTimes(baseString, count);
        estimate.printHashMapInsertTimes(baseString, count);
        estimate.printHashMapInsertTimes(baseString, count);
        estimate.printHashMapInsertTimes(baseString, count);
        estimate.printHashMapInsertTimes(baseString, count);
        estimate.printHashMapInsertTimes(baseString, count);
        estimate.printHashMapInsertTimes(baseString, count);


        estimate.printFileAppendTimes(baseString, (int)count);
        estimate.printFileAppendTimes(baseString, (int)count);
        estimate.printFileAppendTimes(baseString, (int)count);
        estimate.printFileAppendTimes(baseString, (int)count);
        estimate.printFileAppendTimes(baseString, (int)count);
        estimate.printFileAppendTimes(baseString, (int)count);
        estimate.printFileAppendTimes(baseString, (int)count);
        estimate.printFileAppendTimes(baseString, (int)count);
        estimate.printFileAppendTimes(baseString, (int)count);
        estimate.printFileAppendTimes(baseString, (int)count);
    }
}