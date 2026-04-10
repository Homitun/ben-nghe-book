package com.bennghe.bookstore.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Integer id;
    private String sku;
    private String isbn;
    private String title;
    private String publisherName;
    private Integer publicationYear;
    private String description;
    private String coverImageUrl;
    private String warehouse;
    private String shelf;
    private String importPrice;
    private String salePrice;
    private String coverPrice;
    private Integer pageCount;
    private Integer weightGram;
    private String size;
    private String targetAudience;
    private String distributor;
    private String translator;
    private String authors;
    private Integer stockQuantity;
    private Integer totalSold;
    private String publisherLogoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
