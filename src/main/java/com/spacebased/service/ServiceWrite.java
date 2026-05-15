package com.spacebased.service;


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

import com.spacebased.config.RabbitConfig;
import com.spacebased.model.DataEntry;
import com.spacebased.repository.DataEntryRepository;
import com.spacebased.space.RedisSpace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceWrite {

    private final DataEntryRepository repository;
    private final RedisSpace redisSpace;

    @RabbitListener(queues = RabbitConfig.WRITE_QUEUE)
    @Transactional
    public void persistToDatabase(DataEntry entry) {
        log.debug("[ServiceWrite] received key={} from RabbitMQ", entry.getKey());

        entry.setStatus(DataEntry.DataStatus.PROCESSING);

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

        saved.setStatus(DataEntry.DataStatus.SYNCED);
        redisSpace.save(saved.getKey(), saved);

        log.info("[ServiceWrite] DB persisted and Redis synced for key={}", saved.getKey());
    }
}