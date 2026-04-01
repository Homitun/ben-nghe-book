package com.bennghe.bookstore.repository;

import com.bennghe.bookstore.entity.HaravanSyncLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HaravanSyncLogRepository extends JpaRepository<HaravanSyncLog, Integer> {
    Page<HaravanSyncLog> findAllByOrderBySyncDateDesc(Pageable pageable);
}
