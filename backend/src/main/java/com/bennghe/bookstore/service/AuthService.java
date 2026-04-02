package com.bennghe.bookstore.service;

import com.bennghe.bookstore.dto.request.LoginRequest;
import com.bennghe.bookstore.dto.response.AuthResponse;
import com.bennghe.bookstore.entity.User;
import com.bennghe.bookstore.repository.UserRepository;
import com.bennghe.bookstore.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String token = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow();

            return AuthResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .role(user.getRole().getName())
                    .permissions(
                            user.getRole().getPermissions().stream()
                                    .map(p -> p.getName())
                                    .collect(Collectors.toList())
                    )
                    .token(token)
                    .build();

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Sai tên đăng nhập hoặc mật khẩu");
        } catch (DisabledException e) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        } catch (AuthenticationException e) {
            throw new RuntimeException("Xác thực thất bại: " + e.getMessage());
        }
    }

    public AuthResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .permissions(
                        user.getRole().getPermissions().stream()
                                .map(p -> p.getName())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
