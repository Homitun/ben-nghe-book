package com.bennghe.bookstore.service;

import com.bennghe.bookstore.dto.request.BookRequest;
import com.bennghe.bookstore.dto.response.BookResponse;
import com.bennghe.bookstore.entity.Book;
import com.bennghe.bookstore.entity.Publisher;
import com.bennghe.bookstore.exception.AppException;
import com.bennghe.bookstore.repository.BookRepository;
import com.bennghe.bookstore.repository.PublisherRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;

    public BookService(BookRepository bookRepository, PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
    }

    // =========================================================
    // GET ALL - lấy danh sách book
    // =========================================================
    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .peek(book -> {
                    if (book.getPublisher() != null) Hibernate.initialize(book.getPublisher());
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET BY ID - lấy 1 sách
    // =========================================================
    @Transactional(readOnly = true)
    public BookResponse getBookById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy sách với ID: " + id));
        if (book.getPublisher() != null) Hibernate.initialize(book.getPublisher());
        return toResponse(book);
    }

    // =========================================================
    // GET BY SKU - tìm sách theo SKU
    // =========================================================
    @Transactional(readOnly = true)
    public BookResponse getBookBySku(String sku) {
        Book book = bookRepository.findBySku(sku)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy sách với SKU: " + sku));
        if (book.getPublisher() != null) Hibernate.initialize(book.getPublisher());
        return toResponse(book);
    }

    // =========================================================
    // CHECK SKU EXISTS - kiểm tra SKU tồn tại
    // =========================================================
    public boolean checkSkuExists(String sku) {
        return bookRepository.existsBySku(sku);
    }

    // =========================================================
    // CREATE - tạo book mới
    // =========================================================
    @Transactional
    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsBySku(request.getSku())) {
            throw AppException.conflict("SKU '" + request.getSku() + "' đã tồn tại");
        }

        Publisher publisher = resolvePublisher(request);

        Book book = Book.builder()
                .sku(request.getSku())
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .publicationYear(request.getPublicationYear())
                .description(request.getDescription())
                .warehouse(request.getWarehouse())
                .shelf(request.getShelf())
                .importPrice(request.getImportPrice())
                .salePrice(request.getSalePrice())
                .coverPrice(request.getCoverPrice())
                .pageCount(request.getPageCount())
                .weightGram(request.getWeightGram())
                .size(request.getSize())
                .targetAudience(request.getTargetAudience())
                .distributor(request.getDistributor())
                .translator(request.getTranslator())
                .publisher(publisher)
                .isActive(true)
                .build();

        Book saved = bookRepository.save(book);
        if (saved.getPublisher() != null) Hibernate.initialize(saved.getPublisher());
        return toResponse(saved);
    }

    // =========================================================
    // UPDATE - cập nhật book
    // =========================================================
    @Transactional
    public BookResponse updateBook(Integer id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy sách với ID: " + id));

        // Kiểm tra SKU mới có trùng với sách khác không
        if (request.getSku() != null && !request.getSku().equals(book.getSku())) {
            if (bookRepository.existsBySku(request.getSku())) {
                throw AppException.conflict("SKU '" + request.getSku() + "' đã tồn tại");
            }
            book.setSku(request.getSku());
        }

        // Cập nhật publisher (ưu tiên name trước, sau đó id)
        if (request.getPublisherId() != null || (request.getPublisherName() != null && !request.getPublisherName().isBlank())) {
            book.setPublisher(resolvePublisher(request));
        }

        // Cập nhật các trường khác (chỉ cập nhật nếu có giá trị)
        if (request.getTitle() != null) book.setTitle(request.getTitle());
        if (request.getIsbn() != null) book.setIsbn(request.getIsbn());
        if (request.getDescription() != null) book.setDescription(request.getDescription());
        if (request.getWarehouse() != null) book.setWarehouse(request.getWarehouse());
        if (request.getShelf() != null) book.setShelf(request.getShelf());
        if (request.getImportPrice() != null) book.setImportPrice(request.getImportPrice());
        if (request.getSalePrice() != null) book.setSalePrice(request.getSalePrice());
        if (request.getCoverPrice() != null) book.setCoverPrice(request.getCoverPrice());
        if (request.getPageCount() != null) book.setPageCount(request.getPageCount());
        if (request.getWeightGram() != null) book.setWeightGram(request.getWeightGram());
        if (request.getSize() != null) book.setSize(request.getSize());
        if (request.getTargetAudience() != null) book.setTargetAudience(request.getTargetAudience());
        if (request.getDistributor() != null) book.setDistributor(request.getDistributor());
        if (request.getTranslator() != null) book.setTranslator(request.getTranslator());
        if (request.getPublicationYear() != null) book.setPublicationYear(request.getPublicationYear());

        Book saved = bookRepository.save(book);
        if (saved.getPublisher() != null) Hibernate.initialize(saved.getPublisher());
        return toResponse(saved);
    }

    // =========================================================
    // DELETE - xóa book (soft delete)
    // =========================================================
    @Transactional
    public void deleteBook(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy sách với ID: " + id));
        book.setIsActive(false);
        bookRepository.save(book);
    }

    // =========================================================
    // FILTER - lọc sách theo publisherId
    // =========================================================
    @Transactional(readOnly = true)
    public List<BookResponse> filterBooksByPublisher(Integer publisherId) {
        List<Book> books = bookRepository.findByPublisher(publisherId);
        return books.stream()
                .peek(book -> {
                    if (book.getPublisher() != null) Hibernate.initialize(book.getPublisher());
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // HELPER - xử lý publisher (ưu tiên name trước, sau đó id)
    // =========================================================
    private Publisher resolvePublisher(BookRequest request) {
        // Ưu tiên publisherName trước
        if (request.getPublisherName() != null && !request.getPublisherName().isBlank()) {
            return publisherRepository.findByName(request.getPublisherName().trim())
                    .orElseThrow(() -> AppException.notFound("Không tìm thấy nhà xuất bản: " + request.getPublisherName()));
        }
        // Sau đó xử lý publisherId
        if (request.getPublisherId() != null) {
            return publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> AppException.notFound("Không tìm thấy nhà xuất bản với ID: " + request.getPublisherId()));
        }
        // Không có thông tin publisher
        return null;
    }

    // =========================================================
    // HELPER - convert Book entity -> BookResponse DTO
    // =========================================================
    private BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .sku(book.getSku())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .publisherName(book.getPublisher() != null ? book.getPublisher().getName() : null)
                .publicationYear(book.getPublicationYear())
                .description(book.getDescription())
                .warehouse(book.getWarehouse())
                .shelf(book.getShelf())
                .importPrice(book.getImportPrice() != null ? book.getImportPrice().toString() : null)
                .salePrice(book.getSalePrice() != null ? book.getSalePrice().toString() : null)
                .coverPrice(book.getCoverPrice() != null ? book.getCoverPrice().toString() : null)
                .pageCount(book.getPageCount())
                .weightGram(book.getWeightGram())
                .size(book.getSize())
                .targetAudience(book.getTargetAudience())
                .distributor(book.getDistributor())
                .translator(book.getTranslator())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
