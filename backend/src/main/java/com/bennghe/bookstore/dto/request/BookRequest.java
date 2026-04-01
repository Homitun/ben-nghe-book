package com.bennghe.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookRequest {
    @NotBlank
    private String sku;

    private String isbn;

    @NotBlank
    private String title;

    private Integer publisherId;

    private Integer publicationYear;
    private String description;
    private String warehouse;
    private String shelf;

    @NotNull
    private BigDecimal importPrice;

    @NotNull
    private BigDecimal salePrice;

    private BigDecimal coverPrice;
    private Integer pageCount;
    private Integer weightGram;
    private String size;
    private String targetAudience;
    private String distributor;
    private String translator;

    private List<Integer> authorIds;   // existing authors
    private List<String> authorNames;  // new authors (will be created if not exist)
}
