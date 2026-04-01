package com.bennghe.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(length = 20)
    private String isbn;

    @Column(nullable = false, length = 500)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(length = 100)
    private String warehouse;

    @Column(length = 100)
    private String shelf;

    @Column(name = "import_price", precision = 18, scale = 2)
    private BigDecimal importPrice;

    @Column(name = "sale_price", precision = 18, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "cover_price", precision = 18, scale = 2)
    private BigDecimal coverPrice;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "weight_gram")
    private Integer weightGram;

    @Column(length = 50)
    private String size;

    @Column(name = "target_audience", length = 255)
    private String targetAudience;

    @Column(length = 255)
    private String distributor;

    @Column(length = 255)
    private String translator;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    @Builder.Default
    private Set<Author> authors = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
