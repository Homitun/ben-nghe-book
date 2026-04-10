package com.bennghe.bookstore.controller;

import com.bennghe.bookstore.dto.request.BookRequest;
import com.bennghe.bookstore.dto.response.ApiResponse;
import com.bennghe.bookstore.dto.response.BookResponse;
import com.bennghe.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // GET /api/books - Lấy danh sách tất cả sách
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success(books, "Lấy danh sách sách thành công"));
    }

    // GET /api/books/{id} - Lấy 1 sách
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Integer id) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success(book, "Lấy sách thành công"));
    }

    // GET /api/books/sku/{sku} - Tìm sách theo SKU
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookBySku(@PathVariable String sku) {
        BookResponse book = bookService.getBookBySku(sku);
        return ResponseEntity.ok(ApiResponse.success(book, "Tìm sách theo SKU thành công"));
    }

    // GET /api/books/sku/{sku}/exists - Kiểm tra SKU tồn tại
    @GetMapping("/sku/{sku}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkSkuExists(@PathVariable String sku) {
        boolean exists = bookService.checkSkuExists(sku);
        return ResponseEntity.ok(ApiResponse.success(exists, "Kiểm tra SKU thành công"));
    }

    // GET /api/books/filter?publisherId=1 - Lọc sách theo NXB
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<BookResponse>>> filterBooksByPublisher(
            @RequestParam(required = false) Integer publisherId) {
        List<BookResponse> books = bookService.filterBooksByPublisher(publisherId);
        return ResponseEntity.ok(ApiResponse.success(books, "Lọc sách thành công"));
    }

    // POST /api/books - Tạo 1 quyển sách mới
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@RequestBody BookRequest request) {
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.ok(ApiResponse.success(book, "Tạo sách thành công"));
    }

    // PUT /api/books/{id} - Cập nhật sách
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Integer id,
            @RequestBody BookRequest request) {
        BookResponse book = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(book, "Cập nhật sách thành công"));
    }

    // DELETE /api/books/{id} - Xóa sách (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa sách thành công"));
    }
}