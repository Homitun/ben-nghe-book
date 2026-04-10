package com.bennghe.bookstore.controller;

import com.bennghe.bookstore.dto.request.LoginRequest;
import com.bennghe.bookstore.dto.response.ApiResponse;
import com.bennghe.bookstore.dto.response.AuthResponse;
import com.bennghe.bookstore.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Controller xử lý các request liên quan đến ĐĂNG NHẬP
 * Frontend gọi: POST /api/auth/login
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /*
     * POST /api/auth/login
     * Nhận { username, password } → trả về thông tin user + token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Đăng nhập thành công"));
    }
}
