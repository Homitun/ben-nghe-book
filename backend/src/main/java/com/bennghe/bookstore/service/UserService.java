package com.bennghe.bookstore.service;

  import com.bennghe.bookstore.dto.request.CreateUserRequest;
  import com.bennghe.bookstore.dto.response.UserResponse;
  import com.bennghe.bookstore.entity.*;
  import com.bennghe.bookstore.exception.AppException;
  import com.bennghe.bookstore.repository.RoleRepository;
  import com.bennghe.bookstore.repository.UserRepository;
  import lombok.RequiredArgsConstructor;
  import org.springframework.data.domain.Page;
  import org.springframework.data.domain.Pageable;
  import org.springframework.security.crypto.password.PasswordEncoder;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;

  import java.util.List;

  @Service
  @RequiredArgsConstructor
  public class UserService {

      private final UserRepository userRepository;
      private final RoleRepository roleRepository;
      private final PasswordEncoder passwordEncoder;

      /** Lấy danh sách tất cả user (phân trang) */
      @Transactional(readOnly = true)
      public Page<UserResponse> getAllUsers(Pageable pageable) {
          return userRepository.findAll(pageable).map(this::toResponse);
      }

      /** Lấy 1 user theo ID */
      @Transactional(readOnly = true)
      public UserResponse getById(Integer id) {
          User user = userRepository.findById(id)
                  .orElseThrow(() -> AppException.notFound("Không tìm thấy người dùng"));
          return toResponse(user);
      }

      /** Tạo user mới */
      @Transactional
      public UserResponse createUser(CreateUserRequest request) {
          // 1. Kiểm tra username đã tồn tại chưa
          if (userRepository.existsByUsername(request.getUsername())) {
              throw AppException.conflict("Tên đăng nhập đã tồn tại");
          }

          // 2. Kiểm tra email đã tồn tại chưa (nếu có email)
          if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
              throw AppException.conflict("Email đã được sử dụng");
          }

          // 3. Lấy role từ DB
          Role role = roleRepository.findById(request.getRoleId())
                  .orElseThrow(() -> AppException.badRequest("Vai trò không hợp lệ"));

          // 4. Tạo user entity
          User user = User.builder()
                  .username(request.getUsername())
                  .passwordHash(passwordEncoder.encode(request.getPassword())) // MÃ HÓA PASSWORD!
                  .fullName(request.getFullName())
                  .email(request.getEmail())
                  .role(role)
                  .isActive(true)
                  .build();

          User saved = userRepository.save(user);
          return toResponse(saved);
      }

      /** Cập nhật thông tin user (không đổi password ở đây) */
      @Transactional
      public UserResponse updateUser(Integer id, CreateUserRequest request) {
          User user = userRepository.findById(id)
                  .orElseThrow(() -> AppException.notFound("Không tìm thấy người dùng"));

          // Cập nhật các trường
          user.setFullName(request.getFullName());
          user.setEmail(request.getEmail());

          // Đổi role nếu có
          if (request.getRoleId() != null) {
              Role role = roleRepository.findById(request.getRoleId())
                      .orElseThrow(() -> AppException.badRequest("Vai trò không hợp lệ"));
              user.setRole(role);
          }

          // Đổi password nếu có (password là optional trong update)
          if (request.getPassword() != null && !request.getPassword().isBlank()) {
              user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
          }

          User saved = userRepository.save(user);
          return toResponse(saved);
      }

      /** Bật / tắt user (không xóa khỏi DB) */
      @Transactional
      public UserResponse toggleActive(Integer id) {
          User user = userRepository.findById(id)
                  .orElseThrow(() -> AppException.notFound("Không tìm thấy người dùng"));
          user.setIsActive(!user.getIsActive());
          User saved = userRepository.save(user);
          return toResponse(saved);
      }

      /** Xóa user */
      @Transactional
      public void deleteUser(Integer id) {
          if (!userRepository.existsById(id)) {
              throw AppException.notFound("Không tìm thấy người dùng");
          }
          userRepository.deleteById(id);
      }

      /** Lấy danh sách vai trò (để frontend hiển thị dropdown) */
      @Transactional(readOnly = true)
      public List<Role> getAllRoles() {
          return roleRepository.findAll();
      }

      /** Convert Entity → DTO phản hồi (không bao giờ trả password) */
      private UserResponse toResponse(User user) {
          return UserResponse.builder()
                  .id(user.getId())
                  .username(user.getUsername())
                  .fullName(user.getFullName())
                  .email(user.getEmail())
                  .roleName(user.getRole().getName())
                  .isActive(user.getIsActive())
                  .createdAt(user.getCreatedAt())
                  .build();
      }
  }
