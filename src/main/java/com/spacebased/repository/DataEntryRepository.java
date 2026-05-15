package com.spacebased.repository;

import com.spacebased.model.DataEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository - Tầng truy cập DATABASE
 * Được dùng bởi service-read và service-write
 */
@Repository
public interface DataEntryRepository extends JpaRepository<DataEntry, Long> {

    Optional<DataEntry> findByKey(String key);

    boolean existsByKey(String key);
}
