package com.bennghe.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "haravan_sync_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HaravanSyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sync_date")
    private LocalDateTime syncDate;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "synced_by")
    private User syncedBy;

    @Column(name = "records_processed")
    @Builder.Default
    private Integer recordsProcessed = 0;

    @Column(name = "records_succeeded")
    @Builder.Default
    private Integer recordsSucceeded = 0;

    @Column(name = "records_failed")
    @Builder.Default
    private Integer recordsFailed = 0;

    @Column(length = 20)
    private String status;

    @Column(name = "error_details", columnDefinition = "NVARCHAR(MAX)")
    private String errorDetails;

    @CreationTimestamp
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
