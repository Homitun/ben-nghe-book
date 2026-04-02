package com.bennghe.bookstore.repository;

import com.bennghe.bookstore.entity.SalesRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesRecordRepository extends JpaRepository<SalesRecord, Integer> {

    @Query("""
        SELECT sr FROM SalesRecord sr
        WHERE (:from IS NULL OR sr.saleDate >= :from)
          AND (:to IS NULL OR sr.saleDate <= :to)
        ORDER BY sr.saleDate DESC
        """)
    List<SalesRecord> findByDateRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("""
        SELECT sr.book.id, sr.book.sku, sr.book.title, SUM(sr.quantitySold) AS totalSold
        FROM SalesRecord sr
        WHERE (:fromDate IS NULL OR sr.saleDate >= :fromDate)
          AND (:toDate IS NULL OR sr.saleDate <= :toDate)
        GROUP BY sr.book.id, sr.book.sku, sr.book.title
        ORDER BY totalSold DESC
        """)
    Page<Object[]> getTopSellingBooks(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(sr.quantitySold), 0)
        FROM SalesRecord sr
        WHERE (:fromDate IS NULL OR sr.saleDate >= :fromDate)
          AND (:toDate IS NULL OR sr.saleDate <= :toDate)
        """)
    Integer getTotalSold(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
        SELECT CAST(sr.saleDate AS LocalDate), SUM(sr.quantitySold)
        FROM SalesRecord sr
        WHERE sr.saleDate BETWEEN :fromDate AND :toDate
        GROUP BY CAST(sr.saleDate AS LocalDate)
        ORDER BY CAST(sr.saleDate AS LocalDate)
        """)
    List<Object[]> getDailySales(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}
