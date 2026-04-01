package com.bennghe.bookstore.repository;

import com.bennghe.bookstore.entity.SalesRecord;
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

    @Query(value = """
        SELECT TOP :topN sr.book_id, b.sku, b.title, SUM(sr.quantity_sold) AS total_sold
        FROM sales_records sr
        JOIN books b ON b.id = sr.book_id
        WHERE (:fromDate IS NULL OR sr.sale_date >= :fromDate)
          AND (:toDate   IS NULL OR sr.sale_date <= :toDate)
        GROUP BY sr.book_id, b.sku, b.title
        ORDER BY total_sold DESC
        """, nativeQuery = true)
    List<Object[]> getTopSellingBooks(
            @Param("topN") int topN,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query(value = """
        SELECT SUM(quantity_sold) FROM sales_records
        WHERE (:fromDate IS NULL OR sale_date >= :fromDate)
          AND (:toDate   IS NULL OR sale_date <= :toDate)
        """, nativeQuery = true)
    Integer getTotalSold(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query(value = """
        SELECT CAST(sale_date AS DATE) AS day, SUM(quantity_sold) AS qty
        FROM sales_records
        WHERE sale_date BETWEEN :fromDate AND :toDate
        GROUP BY CAST(sale_date AS DATE)
        ORDER BY day
        """, nativeQuery = true)
    List<Object[]> getDailySales(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}
