package com.spacebased.controller;

import com.spacebased.model.ClientRequestDTO;
import com.spacebased.model.DataEntry;
import com.spacebased.processingunit.PuBe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================
 * CLIENT-REQUEST Controller - REST API Entry Point
 * ============================================================
 * Nhận request từ client (HTTP) và chuyển vào PU-BE.
 *
 * Sơ đồ:
 *   [HTTP Client] ──read/write──► ClientRequestController ──► PU-BE
 *
 * Endpoints:
 *   POST /api/write    → ghi dữ liệu
 *   GET  /api/read/{key} → đọc dữ liệu
 *   GET  /api/health   → kiểm tra trạng thái
 * ============================================================
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientRequestController {

    private final PuBe puBe;

    /**
     * CLIENT-REQUEST: Ghi dữ liệu vào hệ thống
     * POST /api/write
     * Body: { "key": "user:1", "value": "John Doe" }
     */
    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> write(@RequestBody ClientRequestDTO request) {
        log.info("[CLIENT-request] POST /write → key={}", request.getKey());

        if (request.getKey() == null || request.getKey().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "key is required"));
        }

        DataEntry result = puBe.processWrite(request.getKey(), request.getValue());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Data written to Space (async persist to DB)");
        response.put("key", result.getKey());
        response.put("status", result.getStatus());

        return ResponseEntity.ok(response);
    }

    /**
     * CLIENT-REQUEST: Đọc dữ liệu từ hệ thống
     * GET /api/read/{key}
     */
    @GetMapping("/read/{key}")
    public ResponseEntity<Map<String, Object>> read(@PathVariable String key) {
        log.info("[CLIENT-request] GET /read/{}", key);

        Optional<DataEntry> result = puBe.processRead(key);

        if (result.isPresent()) {
            DataEntry entry = result.get();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("key", entry.getKey());
            response.put("value", entry.getValue());
            response.put("status", entry.getStatus());
            response.put("id", entry.getId());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Health check endpoint
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "Space-Based Architecture");
        status.put("status", "UP");
        status.put("components", Map.of(
                "PU-BE", "ACTIVE",
                "RED'S (Redis)", "CONNECTED",
                "MessageGrid", "RUNNING",
                "ServiceRead", "RUNNING",
                "ServiceWrite", "RUNNING"
        ));
        return ResponseEntity.ok(status);
    }

    /**
     * Demo: Tạo dữ liệu mẫu
     * POST /api/demo
     */
    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> demo() {
        log.info("[CLIENT-request] POST /demo - Seeding sample data");

        String[] keys = {"user:1", "user:2", "product:A", "product:B"};
        String[] values = {"Alice", "Bob", "Laptop", "Phone"};

        for (int i = 0; i < keys.length; i++) {
            puBe.processWrite(keys[i], values[i]);
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Demo data seeded successfully",
                "count", keys.length
        ));
    }
}
