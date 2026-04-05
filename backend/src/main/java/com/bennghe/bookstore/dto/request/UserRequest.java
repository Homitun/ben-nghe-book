package com.bennghe.bookstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/*
 * DTO gửi lên server khi tạo mới hoặc cập nhật tài khoản.
 */
@Data
public class UserRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    private String username;

    // Password: bắt buộc khi tạo mới, không bắt buộc khi cập nhật
    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    private String password;

    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Vai trò không được để trống")
    private String roleName;  // "ADMIN" | "MANAGER" | "STAFF"
}
