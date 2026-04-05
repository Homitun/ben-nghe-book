package com.bennghe.bookstore.controller;

import com.bennghe.bookstore.dto.request.UserRequest;
import com.bennghe.bookstore.dto.response.ApiResponse;
import com.bennghe.bookstore.dto.response.UserResponse;
import com.bennghe.bookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controller xử lý CRUD TÀI KHOẢN (Users)
 *
 * GET    /api/users          → Lấy danh sách tài khoản
 * GET    /api/users/{id}     → Lấy 1 tài khoản
 * POST   /api/users          → Tạo tài khoản mới
 * PUT    /api/users/{id}     → Cập nhật tài khoản
 * DELETE /api/users/{id}     → Xoá tài khoản (soft delete)
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
     * GET /api/users
     * Lấy danh sách tất cả tài khoản
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách tài khoản thành công"));
    }

    /*
     * GET /api/users/{id}
     * Lấy thông tin 1 tài khoản
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Integer id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "Lấy thông tin tài khoản thành công"));
    }

    /*
     * POST /api/users
     * Tạo tài khoản mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request) {

        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(user, "Tạo tài khoản thành công"));
    }

    /*
     * PUT /api/users/{id}
     * Cập nhật tài khoản
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserRequest request) {

        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(user, "Cập nhật tài khoản thành công"));
    }

    /*
     * DELETE /api/users/{id}
     * Xoá tài khoản (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoá tài khoản thành công"));
    }
}