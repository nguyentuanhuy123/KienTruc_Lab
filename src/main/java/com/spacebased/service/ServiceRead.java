package com.spacebased.service;


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

import com.spacebased.config.RabbitConfig;
import com.spacebased.model.DataEntry;
import com.spacebased.repository.DataEntryRepository;
import com.spacebased.space.RedisSpace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceRead {

    private final DataEntryRepository repository;
    private final RedisSpace redisSpace;

    @RabbitListener(queues = RabbitConfig.READ_QUEUE)
    public void fetchFromDatabaseAndSyncToRedis(String key) {
        log.debug("[ServiceRead] received key={} from RabbitMQ", key);

        Optional<DataEntry> dbEntry = repository.findByKey(key);
        if (dbEntry.isPresent()) {
            DataEntry entry = dbEntry.get();
            entry.setStatus(DataEntry.DataStatus.SYNCED);
            redisSpace.save(key, entry);
            log.info("[ServiceRead] DB → Redis synced for key={}", key);
        } else {
            log.debug("[ServiceRead] key={} not found in DB", key);
        }
    }
}