package com.spacebased.space;

import com.spacebased.model.DataEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * ============================================================
 * REDIS SPACE - In-Memory Data Grid (Tuple Space)
 * ============================================================
 * Day la "Space" trong Space-Based Architecture.
 * Moi PU (Processing Unit) doc/ghi qua day thay vi qua DB.
 *
 * ─────────────────────────────────────────────
 * KHI NAO GHI REDIS:
 *   write()  → PU-BE goi ngay khi nhan WRITE request (status=IN_SPACE)
 *   save()   → ServiceWrite/ServiceRead goi sau khi DB xong (status=SYNCED)
 *
 * KHI NAO DOC REDIS:
 *   read()   → PU-BE goi dau tien moi request READ
 *              Cache HIT  → tra ve luon, khong can xuong DB
 *              Cache MISS → PU-BE publish ReadQueue
 *
 * KHI NAO XOA REDIS (evict):
 *   evict()  → Chi dung khi can chien luoc xoa chu dong
 *              (vi du: invalidate sau khi delete record khoi DB)
 *              Trong WRITE/READ binh thuong: KHONG evict,
 *              de TTL tu dong het han theo DEFAULT_TTL_MINUTES
 * ─────────────────────────────────────────────
 *
 * TTL mac dinh: 30 phut.
 * Key format:  "space:<key>"
 * ============================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSpace {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SPACE_PREFIX       = "space:";
    private static final long   DEFAULT_TTL_MINUTES = 30;

    // ----------------------------------------------------------------
    // WRITE — PU-BE goi ngay khi nhan request ghi
    // ----------------------------------------------------------------

    /**
     * Ghi entry vao Redis voi TTL.
     * Duoc goi boi PU-BE ngay sau khi nhan WRITE request.
     * status = IN_SPACE (chua persist xuong DB).
     */
    public void write(String key, DataEntry entry) {
        redisTemplate.opsForValue().set(
                SPACE_PREFIX + key, entry, DEFAULT_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("[Redis] write — key={}, status={}", key, entry.getStatus());
    }

    // ----------------------------------------------------------------
    // READ — PU-BE goi dau tien moi request doc
    // ----------------------------------------------------------------

    /**
     * Doc entry tu Redis.
     * Duoc goi boi PU-BE truoc khi xuong DB.
     * Cache HIT  → tra ve Optional.of(entry)
     * Cache MISS → tra ve Optional.empty()
     */
    public Optional<DataEntry> read(String key) {
        Object value = redisTemplate.opsForValue().get(SPACE_PREFIX + key);
        if (value instanceof DataEntry entry) {
            log.debug("[Redis] read HIT — key={}", key);
            return Optional.of(entry);
        }
        log.debug("[Redis] read MISS — key={}", key);
        return Optional.empty();
    }

    // ----------------------------------------------------------------
    // SAVE — ServiceWrite/ServiceRead goi sau khi DB xong
    // ----------------------------------------------------------------

    /**
     * Cap nhat entry trong Redis sau khi da persist/fetch tu DB.
     * Duoc goi boi ServiceWrite (sau ghi DB) va ServiceRead (sau doc DB).
     * status = SYNCED (da dong bo voi DB).
     */
    public void save(String key, DataEntry entry) {
        redisTemplate.opsForValue().set(
                SPACE_PREFIX + key, entry, DEFAULT_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("[Redis] save (sync-back) — key={}, status={}", key, entry.getStatus());
    }

    // ----------------------------------------------------------------
    // EVICT — Chi dung khi can xoa chu dong (vi du: delete record)
    // ----------------------------------------------------------------

    /**
     * Xoa key khoi Redis.
     * KHONG goi sau WRITE binh thuong — de TTL tu het han.
     * Chi goi khi can invalidate chu dong (vi du: xoa record khoi DB).
     */
    public void evict(String key) {
        redisTemplate.delete(SPACE_PREFIX + key);
        log.debug("[Redis] evict — key={}", key);
    }

    // ----------------------------------------------------------------
    // HELPER
    // ----------------------------------------------------------------

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(SPACE_PREFIX + key));
    }
}
