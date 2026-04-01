package com.bennghe.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalesRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "quantity_sold", nullable = false)
    private Integer quantitySold;

    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    @Column(name = "haravan_order_id", length = 100)
    private String haravanOrderId;

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @CreationTimestamp
    @Column(name = "synced_at", updatable = false)
    private LocalDateTime syncedAt;
}
