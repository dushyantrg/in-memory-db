package in.memory.db.service;

import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;

@Singleton
public class FileFlushingJob {

    private final AppendOnlyLog appendOnlyLog;

    private long timeLastFlushedToDisk;

    @Inject
    public FileFlushingJob(AppendOnlyLog appendOnlyLog) {
        this.appendOnlyLog = appendOnlyLog;
        this.timeLastFlushedToDisk = 0;
    }

    @Scheduled(fixedDelay = "${aol.flush-interval}")
    void flushFileToDisk() throws IOException {
        long startTime = System.nanoTime();
        //System.out.println(System.nanoTime());
        if (!appendOnlyLog.isInitialized()) {
            return;
        }
        long lastUpdateTime = appendOnlyLog.getLastUpdateTime();
        if(lastUpdateTime <= this.timeLastFlushedToDisk) {
            //System.out.printf("Not flushing as no updates available to flush. this.timeLastFlushedToDisk=%s  lastUpdateTime=%s%n",this.timeLastFlushedToDisk,lastUpdateTime);
            long endTime = System.nanoTime() - startTime;
            System.out.printf("Execution Time taken to return without flushing by flushFileToDisk %s nanoseconds%n", endTime);
            return;
        }

        this.timeLastFlushedToDisk = System.nanoTime();
        //System.out.println("Flushing logs to disk at "+this.timeLastFlushedToDisk);
        appendOnlyLog.flushToDisk();
        long endTime = System.nanoTime() - startTime;
        System.out.printf("Execution Time taken to flush by flushFileToDisk %s nanoseconds%n", endTime);
    }
}
