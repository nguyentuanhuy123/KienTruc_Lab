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
import org.springframework.transaction.annotation.Transactional;

/**
 * ============================================================
 * SERVICE-WRITE - Persist tu WriteQueue vao DATABASE
 * ============================================================
 * Service-Write KHONG duoc goi truc tiep tu PU-BE.
 * No chi duoc kich hoat qua MessageGrid (write-queue).
 *
 * ─────────────────────────────────────────────
 * KHI NAO GHI DATABASE:
 *   - Khi nhan duoc DataEntry tu write-queue
 *   - Thuc hien INSERT hoac UPDATE vao DB
 *
 * KHI NAO GHI REDIS:
 *   - SAU KHI ghi DB thanh cong → update Redis voi status=SYNCED
 *   - Muc dich: dam bao Redis phan anh dung trang thai da duoc persist
 *
 * KHI NAO XOA REDIS (evict):
 *   - KHONG evict sau khi ghi DB — data van can o Redis cho cac PU doc
 *   - Chi evict khi co chinh sach expiry (TTL tu RedisSpace)
 *
 * SERVICE-WRITE KHONG doc Database (chi ghi).
 * ─────────────────────────────────────────────
 *
 * WRITE FLOW (duoc goi boi MessageGrid):
 *   WriteQueue → ServiceWrite
 *     → [1] INSERT/UPDATE DB
 *     → [2] sync Redis (status=SYNCED)
 * ============================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceWrite {

    private final MessageGrid messageGrid;
    private final DataEntryRepository repository;
    private final RedisSpace redisSpace;

    private volatile boolean running = true;

    /**
     * Worker thread lang nghe write-queue.
     * Khoi dong ngay khi application san sang.
     */
    @Async("messageGridExecutor")
    @EventListener(ApplicationReadyEvent.class)
    public void startWriteWorker() {
        log.info("[ServiceWrite] Worker started, listening to write-queue...");
        while (running) {
            try {
                DataEntry entry = messageGrid.pollFromWriteQueue();
                if (entry != null) {
                    persistToDatabase(entry);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("[ServiceWrite] Worker interrupted");
                break;
            } catch (Exception e) {
                log.error("[ServiceWrite] Error processing write: {}", e.getMessage(), e);
            }
        }
        log.info("[ServiceWrite] Worker stopped.");
    }

    /**
     * GHI DATABASE → SYNC REDIS.
     *
     * Buoc 1: INSERT hoac UPDATE vao DB (nguon su that lau dai).
     * Buoc 2: Ghi lai vao Redis voi status=SYNCED.
     *         → Dan bao cac PU khac (neu co) nhin thay trang thai moi nhat.
     *
     * Duoc goi boi worker (tu write-queue), KHONG duoc goi tu PU-BE.
     */
    @Transactional
    public void persistToDatabase(DataEntry entry) {
        log.debug("[ServiceWrite] Persisting key={} to DATABASE", entry.getKey());

        entry.setStatus(DataEntry.DataStatus.PROCESSING);

        // [1] GHI DATABASE
        DataEntry saved;
        if (repository.existsByKey(entry.getKey())) {
            DataEntry existing = repository.findByKey(entry.getKey()).orElse(entry);
            existing.setValue(entry.getValue());
            existing.setStatus(DataEntry.DataStatus.PERSISTED);
            saved = repository.save(existing);
        } else {
            entry.setStatus(DataEntry.DataStatus.PERSISTED);
            saved = repository.save(entry);
        }
        log.info("[ServiceWrite] Persisted to DB — id={}, key={}", saved.getId(), saved.getKey());

        // [2] SYNC REDIS — cap nhat status thanh SYNCED de phan anh da persist
        //     KHONG evict: data van phai o Redis cho cac lan doc tiep theo
        saved.setStatus(DataEntry.DataStatus.SYNCED);
        redisSpace.save(saved.getKey(), saved);
        log.debug("[ServiceWrite] Synced back to Redis — key={}", saved.getKey());
    }

    public void stop() {
        this.running = false;
    }
}
