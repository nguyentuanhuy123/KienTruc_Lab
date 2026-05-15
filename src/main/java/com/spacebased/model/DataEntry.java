package com.spacebased.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DataEntry - Entity được lưu trong cả Redis Space và Database
 * Implements Serializable để có thể serialize vào Redis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "data_entries")
public class DataEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_key", nullable = false)
    private String key;

    @Column(columnDefinition = "TEXT")
    private String value;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum DataStatus {
        PENDING,        // Vừa nhận từ CLIENT-request
        IN_SPACE,       // Đang lưu trong Redis Space (RED'S)
        PROCESSING,     // Đang xử lý qua Message Grid
        PERSISTED,      // Đã lưu vào Database
        SYNCED          // Đã đồng bộ lại Redis sau DB
    }
}
