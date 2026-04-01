package com.bennghe.bookstore.config;

import com.bennghe.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Chạy 1 lần khi khởi động: cập nhật password hash cho các tài khoản seed trong DB.
 * Sau khi chạy xong lần đầu, các dòng này sẽ không làm gì vì hash đã đúng.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.admin-password}")
    private String adminPassword;

    @Value("${app.init.manager-password}")
    private String managerPassword;

    @Value("${app.init.staff-password}")
    private String staffPassword;

    @Bean
    ApplicationRunner initPasswords() {
        return args -> {
            resetPassword("admin",    adminPassword);
            resetPassword("manager1", managerPassword);
            resetPassword("manager2", managerPassword);
            resetPassword("staff1",   staffPassword);
            resetPassword("staff2",   staffPassword);
            log.info("DataInitializer: password hashes updated.");
        };
    }

    private void resetPassword(String username, String rawPassword) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        });
    }
}
