package com.spacebased.messagegrid;

import com.spacebased.model.DataEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ============================================================
 * MESSAGE GRID - Bộ truyền message bất đồng bộ
 * ============================================================
 * Trong sơ đồ có 2 luồng message:
 *
 *  1. WRITE path:
 *     RED'S ──save──► MessageGrid ──► service-write
 *
 *  2. READ path:
 *     PU-BE ──message──► MessageGrid ──► service-read ──► DB
 *
 * MessageGrid đóng vai trò buffer/queue giữa các thành phần.
 * ============================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageGrid {

    // Queue cho luồng GHI (write path)
    private final BlockingQueue<DataEntry> writeQueue = new LinkedBlockingQueue<>(1000);

    // Queue cho luồng ĐỌC (read path)
    private final BlockingQueue<String> readQueue = new LinkedBlockingQueue<>(1000);

    // ----------------------------------------------------------------
    // WRITE QUEUE - từ RED'S → service-write
    // ----------------------------------------------------------------

    /**
     * RED'S gửi message "save" vào write queue
     */
    public void publishToWriteQueue(DataEntry entry) {
        boolean added = writeQueue.offer(entry);
        if (added) {
            log.debug("[MessageGrid] published to write-queue → key={}", entry.getKey());
        } else {
            log.warn("[MessageGrid] write-queue FULL! key={}", entry.getKey());
        }
    }

    /**
     * service-write poll message từ write queue
     */
    public DataEntry pollFromWriteQueue() throws InterruptedException {
        return writeQueue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public int writeQueueSize() {
        return writeQueue.size();
    }

    // ----------------------------------------------------------------
    // READ QUEUE - từ PU-BE → service-read
    // ----------------------------------------------------------------

    /**
     * PU-BE gửi message "database lookup" vào read queue
     */
    @Async("messageGridExecutor")
    public void publishToReadQueue(String key) {
        boolean added = readQueue.offer(key);
        if (added) {
            log.debug("[MessageGrid] published to read-queue → key={}", key);
        } else {
            log.warn("[MessageGrid] read-queue FULL! key={}", key);
        }
    }

    /**
     * service-read poll message từ read queue
     */
    public String pollFromReadQueue() throws InterruptedException {
        return readQueue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public int readQueueSize() {
        return readQueue.size();
    }
}
