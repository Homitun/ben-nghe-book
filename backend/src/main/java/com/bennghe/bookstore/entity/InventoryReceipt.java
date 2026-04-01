package com.bennghe.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_receipts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryReceipt {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "receipt_code", unique = true, length = 50)
    private String receiptCode;

    @Column(name = "receipt_date")
    private LocalDateTime receiptDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imported_by", nullable = false)
    private User importedBy;

    @Column(length = 500)
    private String notes;

    @Column(name = "import_source", length = 20)
    @Builder.Default
    private String importSource = "MANUAL";

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryReceiptItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
