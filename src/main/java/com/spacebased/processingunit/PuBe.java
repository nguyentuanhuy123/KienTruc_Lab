package com.spacebased.processingunit;

import com.spacebased.messagegrid.MessageGrid;
import com.spacebased.model.DataEntry;
import com.spacebased.space.RedisSpace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ============================================================
 * PU-BE - Processing Unit Backend
 * ============================================================
 * PU-BE la dau moi duy nhat xu ly request tu CLIENT.
 * PU-BE CHI giao tiep voi:
 *   - RedisSpace  (doc/ghi truc tiep)
 *   - MessageGrid (publish message async)
 *
 * PU-BE KHONG giao tiep truc tiep voi Database.
 * PU-BE KHONG goi ServiceRead / ServiceWrite.
 *
 * ─────────────────────────────────────────────
 * KHI NAO DOC/GHI REDIS (Space):
 *   WRITE → ghi vao Redis NGAY (in-memory, fast)
 *           CLIENT nhan ket qua sau buoc nay
 *   READ  → doc tu Redis truoc (cache hit = tra ve luon)
 *
 * KHI NAO DOC/GHI DATABASE:
 *   WRITE → KHONG truc tiep; chi publish MQ
 *           → ServiceWrite tu persist async
 *           → ServiceWrite sync lai Redis sau khi ghi DB xong
 *   READ miss → KHONG truc tiep; chi publish MQ
 *             → ServiceRead fetch DB → sync Redis
 *             → PU-BE retry doc Redis cho den khi co data
 * ─────────────────────────────────────────────
 *
 * WRITE FLOW:
 *   CLIENT → PU-BE
 *     → [1] write Redis (immediate, CLIENT gets response)
 *     → [2] publish WriteQueue
 *             → (async) ServiceWrite → DB
 *             → (async) ServiceWrite → sync Redis (status=SYNCED)
 *
 * READ FLOW (cache hit):
 *   CLIENT → PU-BE → read Redis → return CLIENT
 *
 * READ FLOW (cache miss):
 *   CLIENT → PU-BE → read Redis (miss)
 *          → publish ReadQueue
 *                → (async) ServiceRead → DB → sync Redis
 *          → retry read Redis (poll until synced)
 *          → return CLIENT
 * ============================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PuBe {

    private final RedisSpace redisSpace;
    private final MessageGrid messageGrid;

    /** Retry poll Redis sau cache-miss, cho ServiceRead sync xong */
    private static final int    READ_RETRY_COUNT    = 10;
    private static final long   READ_RETRY_DELAY_MS = 100;

    // ----------------------------------------------------------------
    // WRITE
    // ----------------------------------------------------------------

    /**
     * WRITE:
     *   [1] GHI REDIS ngay — source of truth tam thoi, tra ve CLIENT luon
     *   [2] PUBLISH WriteQueue — ServiceWrite async ghi DB, roi sync Redis
     *
     * PU-BE khong doi DB xong moi tra loi → latency thap.
     */
    public DataEntry processWrite(String key, String value) {
        log.info("[PU-BE] WRITE → key={}", key);

        DataEntry entry = DataEntry.builder()
                .key(key)
                .value(value)
                .status(DataEntry.DataStatus.IN_SPACE)
                .build();

        // [1] GHI REDIS — immediate, CLIENT nhan ket qua ngay
        redisSpace.write(key, entry);
        log.debug("[PU-BE] write → Redis done, key={}", key);

        // [2] PUBLISH WriteQueue — DB duoc ghi ASYNC (khong block CLIENT)
        messageGrid.publishToWriteQueue(entry);
        log.debug("[PU-BE] publish → WriteQueue done, key={}", key);

        return entry;
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    /**
     * READ:
     *   [1] DOC REDIS — neu hit tra ve luon
     *   [2] PUBLISH ReadQueue — ServiceRead se fetch DB va sync Redis
     *   [3] RETRY doc Redis — doi ServiceRead sync xong roi tra ve CLIENT
     *
     * PU-BE khong goi truc tiep ServiceRead hay Database.
     * Moi tuong tac voi DB deu di qua MessageGrid.
     */
    public Optional<DataEntry> processRead(String key) {
        log.info("[PU-BE] READ → key={}", key);

        // [1] DOC REDIS truoc (cache hit)
        Optional<DataEntry> hit = redisSpace.read(key);
        if (hit.isPresent()) {
            log.debug("[PU-BE] cache HIT → Redis, key={}", key);
            return hit;
        }

        // [2] cache MISS → publish ReadQueue
        //     ServiceRead se: DB → sync Redis
        log.debug("[PU-BE] cache MISS → publish ReadQueue, key={}", key);
        messageGrid.publishToReadQueue(key);

        // [3] Retry doc Redis, cho ServiceRead sync xong
        for (int i = 1; i <= READ_RETRY_COUNT; i++) {
            try {
                Thread.sleep(READ_RETRY_DELAY_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
            Optional<DataEntry> synced = redisSpace.read(key);
            if (synced.isPresent()) {
                log.debug("[PU-BE] Redis synced after {} retry(ies), key={}", i, key);
                return synced;
            }
        }

        log.debug("[PU-BE] key={} not found (not in DB either)", key);
        return Optional.empty();
    }
}
