package com.bennghe.bookstore.service;

import com.bennghe.bookstore.dto.request.UserRequest;
import com.bennghe.bookstore.dto.response.UserResponse;
import com.bennghe.bookstore.entity.Role;
import com.bennghe.bookstore.entity.User;
import com.bennghe.bookstore.exception.AppException;
import com.bennghe.bookstore.repository.RoleRepository;
import com.bennghe.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Service xử lý logic CRUD TÀI KHOẢN (Users)
 * - getAll   : lấy danh sách tất cả tài khoản
 * - getById  : lấy 1 tài khoản theo ID
 * - create   : tạo tài khoản mới
 * - update   : cập nhật tài khoản
 * - delete   : xoá (soft delete = isActive = false)
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // =========================================================
    // GET ALL - lấy danh sách tài khoản
    // =========================================================
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET BY ID - lấy 1 tài khoản
    // =========================================================
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản với ID: " + id));
        return toResponse(user);
    }

    // =========================================================
    // CREATE - tạo tài khoản mới
    // =========================================================
    @Transactional
    public UserResponse createUser(UserRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            throw AppException.conflict("Username '" + request.getUsername() + "' đã tồn tại");
        }

        // Kiểm tra email đã tồn tại chưa (nếu có)
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsByEmail(request.getEmail())) {
            throw AppException.conflict("Email '" + request.getEmail() + "' đã tồn tại");
        }

        // Tìm role
        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> AppException.badRequest("Vai trò '" + request.getRoleName() + "' không tồn tại"));

        // Mã hoá password trước khi lưu
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Build entity
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(hashedPassword)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(role)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    // =========================================================
    // UPDATE - cập nhật tài khoản
    // =========================================================
    @Transactional
    public UserResponse updateUser(Integer id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản với ID: " + id));

        // Nếu đổi username → kiểm tra không trùng với user khác
        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw AppException.conflict("Username '" + request.getUsername() + "' đã tồn tại");
        }

        // Cập nhật role
        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> AppException.badRequest("Vai trò '" + request.getRoleName() + "' không tồn tại"));

        // Cập nhật các trường
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(role);

        // Nếu có nhập password mới → mã hoá và lưu
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    // =========================================================
    // DELETE - xoá mềm (soft delete)
    // =========================================================
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản với ID: " + id));

        user.setIsActive(false);  // soft delete
        userRepository.save(user);
    }

    // =========================================================
    // Chuyển User entity → UserResponse DTO
    // =========================================================
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roleName(user.getRole().getName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
