package com.bennghe.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * DTO này nhận dữ liệu từ form login gửi lên.
 * Ví dụ: { "username": "admin", "password": "Admin@123" }
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    private String password;
}
