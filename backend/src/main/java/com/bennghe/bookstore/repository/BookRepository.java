package com.bennghe.bookstore.repository;

import com.bennghe.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    Optional<Book> findBySku(String sku);
    boolean existsBySku(String sku);

    @Query("""
        SELECT b FROM Book b
        WHERE b.isActive = true
          AND (:keyword IS NULL OR b.sku LIKE %:keyword% OR b.title LIKE %:keyword%)
        """)
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        WHERE b.isActive = true
          AND (:publisherId IS NULL OR b.publisher.id = :publisherId)
        """)
    List<Book> findByPublisher(@Param("publisherId") Integer publisherId);

    @Query("""
        SELECT b FROM Book b JOIN b.authors a
        WHERE b.isActive = true AND a.id = :authorId
        """)
    List<Book> findByAuthor(@Param("authorId") Integer authorId);

    // Books with total imported vs total sold (for stock calculation)
    @Query(value = """
        SELECT b.id, b.sku, b.title,
               COALESCE(SUM(iri.quantity), 0) AS total_imported,
               COALESCE(sr.total_sold, 0)     AS total_sold,
               COALESCE(SUM(iri.quantity), 0) - COALESCE(sr.total_sold, 0) AS current_stock
        FROM books b
        LEFT JOIN inventory_receipt_items iri ON iri.book_id = b.id
        LEFT JOIN (
            SELECT book_id, SUM(quantity_sold) AS total_sold
            FROM sales_records GROUP BY book_id
        ) sr ON sr.book_id = b.id
        WHERE b.is_active = 1
        GROUP BY b.id, b.sku, b.title, sr.total_sold
        ORDER BY current_stock DESC
        """, nativeQuery = true)
    List<Object[]> getStockSummary();
}
