package com.spacebased;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * ============================================================
 * SPACE-BASED ARCHITECTURE - Correct Flow
 * ============================================================
 *
 *                ┌───────────────────────┐
 *                │       CLIENT          │
 *                └──────────┬────────────┘
 *                           │
 *                    request/read/write
 *                           │
 *                           ▼
 *                ┌───────────────────────┐
 *                │        PU-BE          │
 *                │  (Processing Unit)    │
 *                └───────┬───────┬───────┘
 *                        │       │
 *           [W] write    │       │ [R] read
 *           [R] retry    │       │
 *                        ▼       ▼
 *                ┌───────────────────────┐
 *                │        REDIS          │
 *                │    (Space/Cache)      │
 *                └───────┬───────┬───────┘
 *                        │       │
 *    [W] async publish   │       │ [R] miss → publish
 *                        ▼       ▼
 *                ┌───────────────────────┐
 *                │    MESSAGE QUEUE      │
 *                │  (write / read req)   │
 *                └───────┬───────┬───────┘
 *                        │       │
 *       write queue      │       │ read queue
 *                        ▼       ▼
 *              ┌─────────────┐ ┌─────────────┐
 *              │service-write│ │service-read │
 *              └──────┬──────┘ └──────┬──────┘
 *                     │  DB write     │  DB read
 *                     │               │
 *                     └───────┬───────┘
 *                             ▼
 *                    ┌────────────────┐
 *                    │    DATABASE    │
 *                    └────────────────┘
 *                             │
 *              sync back to Redis (both services)
 *
 * ============================================================
 * KHI NAO DOC/GHI REDIS vs DATABASE
 * ============================================================
 *
 * REDIS (Space) — doc/ghi boi PU-BE truc tiep:
 *   - WRITE: PU-BE ghi Redis NGAY → tra CLIENT → publish WriteQueue
 *   - READ:  PU-BE doc Redis TRUOC → HIT tra luon; MISS → publish ReadQueue
 *   - SYNC:  ServiceWrite/ServiceRead cap nhat Redis sau khi DB xong
 *
 * DATABASE — chi duoc truy cap qua MessageGrid (async):
 *   - WRITE: WriteQueue → ServiceWrite → INSERT/UPDATE DB → sync Redis
 *   - READ:  ReadQueue  → ServiceRead  → SELECT DB        → sync Redis
 *
 * PU-BE KHONG BAO GIO goi truc tiep Database.
 * PU-BE KHONG BAO GIO goi ServiceRead / ServiceWrite.
 *
 * ============================================================
 * FLOW CHI TIET:
 *
 * [1] WRITE:
 *   CLIENT → PU-BE → Redis(write) → return CLIENT
 *                 → WriteQueue → ServiceWrite → DB
 *                                             → Redis(sync, status=SYNCED)
 *
 * [2] READ (cache hit):
 *   CLIENT → PU-BE → Redis(read=HIT) → return CLIENT
 *
 * [3] READ (cache miss):
 *   CLIENT → PU-BE → Redis(read=MISS)
 *                 → ReadQueue → ServiceRead → DB → Redis(sync, status=SYNCED)
 *          → PU-BE retry Redis → return CLIENT
 * ============================================================
 */
@SpringBootApplication
@EnableAsync
public class SpaceBasedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaceBasedApplication.class, args);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("  Space-Based Architecture is RUNNING!");
        System.out.println("  API Health: http://localhost:8087/api/health");
        System.out.println("  Read  API:  http://localhost:8087/api/read/{key}");
        System.out.println("  Write API:  http://localhost:8087/api/write");
        System.out.println("  H2 Console: http://localhost:8087/h2-console");
        System.out.println("=".repeat(50) + "\n");
    }
}
