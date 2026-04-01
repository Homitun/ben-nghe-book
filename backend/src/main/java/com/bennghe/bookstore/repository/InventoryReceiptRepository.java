package com.bennghe.bookstore.repository;

import com.bennghe.bookstore.entity.InventoryReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryReceiptRepository extends JpaRepository<InventoryReceipt, Integer> {

    Page<InventoryReceipt> findAllByOrderByReceiptDateDesc(Pageable pageable);

    @Query("""
        SELECT ir FROM InventoryReceipt ir
        WHERE (:from IS NULL OR ir.receiptDate >= :from)
          AND (:to IS NULL OR ir.receiptDate <= :to)
        ORDER BY ir.receiptDate DESC
        """)
    List<InventoryReceipt> findByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query(value = """
        SELECT COALESCE(SUM(iri.quantity), 0)
        FROM inventory_receipt_items iri
        JOIN inventory_receipts ir ON ir.id = iri.receipt_id
        WHERE iri.book_id = :bookId
        """, nativeQuery = true)
    Integer getTotalImportedByBook(@Param("bookId") Integer bookId);
}
