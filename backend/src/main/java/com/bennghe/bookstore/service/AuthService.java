package com.bennghe.bookstore.service;

import com.bennghe.bookstore.dto.request.LoginRequest;
import com.bennghe.bookstore.dto.response.AuthResponse;
import com.bennghe.bookstore.entity.User;
import com.bennghe.bookstore.exception.AppException;
import com.bennghe.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/*
 * Service xử lý logic ĐĂNG NHẬP
 * Quy trình:
 *   1. Tìm user theo username trong DB
 *   2. So sánh password người dùng nhập với password_hash trong DB
 *   3. Nếu đúng → trả về thông tin user + token (giả lập)
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        // Bước 1: Tìm user theo username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException("Tên đăng nhập không tồn tại"));

        // Bước 2: Kiểm tra user có bị khoá không
        if (!user.getIsActive()) {
            throw new AppException("Tài khoản đã bị vô hiệu hoá");
        }

        // Bước 3: So sánh password
        // BCrypt.matches(password nhập, password hash trong DB)
        boolean match = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!match) {
            throw new AppException("Mật khẩu không đúng");
        }

        // Bước 4: Tạo token giả lập (sau này thay bằng JWT thật)
        String fakeToken = "fake-" + UUID.randomUUID().toString();

        // Bước 5: Trả về thông tin user
        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .token(fakeToken)
                .build();
    }
}
