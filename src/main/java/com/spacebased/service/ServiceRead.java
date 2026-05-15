package com.spacebased.service;

import com.spacebased.messagegrid.MessageGrid;
import com.spacebased.model.DataEntry;
import com.spacebased.repository.DataEntryRepository;
import com.spacebased.space.RedisSpace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ============================================================
 * SERVICE-READ - Doc tu DATABASE va sync vao Redis
 * ============================================================
 * Service-Read KHONG duoc goi truc tiep tu PU-BE.
 * No chi duoc kich hoat qua MessageGrid (read-queue).
 *
 * ─────────────────────────────────────────────
 * KHI NAO DOC DATABASE:
 *   - Khi nhan duoc key tu read-queue (cache miss)
 *   - Sau khi doc DB, sync ket qua vao Redis de PU-BE retry doc
 *
 * KHI NAO GHI REDIS:
 *   - Sau khi fetch tu DB thanh cong → ghi vao Redis (status=SYNCED)
 *   - Day la buoc "sync back" trong flow read-miss
 *
 * SERVICE-READ KHONG ghi Database.
 * ─────────────────────────────────────────────
 *
 * READ-MISS FLOW (duoc goi boi MessageGrid):
 *   ReadQueue → ServiceRead
 *     → [1] fetch DB
 *     → [2] sync Redis (PU-BE retry se thay data)
 * ============================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceRead {

    private final MessageGrid messageGrid;
    private final DataEntryRepository repository;
    private final RedisSpace redisSpace;

    private volatile boolean running = true;

    /**
     * Worker thread lang nghe read-queue.
     * Khoi dong ngay khi application san sang.
     */
    @Async("messageGridExecutor")
    @EventListener(ApplicationReadyEvent.class)
    public void startReadWorker() {
        log.info("[ServiceRead] Worker started, listening to read-queue...");
        while (running) {
            try {
                String key = messageGrid.pollFromReadQueue();
                if (key != null) {
                    fetchFromDatabaseAndSyncToRedis(key);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("[ServiceRead] Worker interrupted");
                break;
            } catch (Exception e) {
                log.error("[ServiceRead] Error processing read: {}", e.getMessage(), e);
            }
        }
        log.info("[ServiceRead] Worker stopped.");
    }

    /**
     * DOC DATABASE → SYNC REDIS.
     *
     * Day la toan bo trach nhiem cua ServiceRead:
     *   1. Doc tu DB (nguon su that lau dai)
     *   2. Ghi vao Redis (de PU-BE retry doc duoc)
     *
     * Duoc goi boi worker (tu read-queue), KHONG duoc goi tu PU-BE.
     */
    public Optional<DataEntry> fetchFromDatabaseAndSyncToRedis(String key) {
        log.debug("[ServiceRead] Fetching key={} from DATABASE", key);

        Optional<DataEntry> dbEntry = repository.findByKey(key);

        if (dbEntry.isPresent()) {
            DataEntry entry = dbEntry.get();
            entry.setStatus(DataEntry.DataStatus.SYNCED);

            // GHI REDIS — PU-BE dang retry se doc duoc sau buoc nay
            redisSpace.save(key, entry);
            log.info("[ServiceRead] DB → Redis synced, key={}", key);
            return Optional.of(entry);
        }

        log.debug("[ServiceRead] key={} not found in DATABASE", key);
        return Optional.empty();
    }

    public void stop() {
        this.running = false;
    }
}
