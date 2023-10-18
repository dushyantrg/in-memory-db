//package in.memory.db.service;
//
//import io.micronaut.scheduling.annotation.Scheduled;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//
//import java.io.IOException;
//
//@Singleton
//public class FileFlushingJob {
//
//    private final AppendOnlyLog appendOnlyLog;
//
//    private long timeLastFlushedToDisk;
//
//    @Inject
//    public FileFlushingJob(AppendOnlyLog appendOnlyLog) {
//        this.appendOnlyLog = appendOnlyLog;
//        this.timeLastFlushedToDisk = 0;
//    }
//
//    @Scheduled(fixedDelay = "${aol.flush-interval}")
//    void flushFileToDisk() throws IOException {
//
//        if (!appendOnlyLog.isInitialized()) {
//            return;
//        }
//
//        long lastUpdateTime = appendOnlyLog.getLastUpdateTime();
//        if(lastUpdateTime <= this.timeLastFlushedToDisk) {
//            return;
//        }
//
//        this.timeLastFlushedToDisk = System.nanoTime();
//        appendOnlyLog.flushToDisk();
//    }
//}
