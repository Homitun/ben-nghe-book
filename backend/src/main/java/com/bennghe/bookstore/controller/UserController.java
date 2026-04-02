package com.bennghe.bookstore.controller;

  import com.bennghe.bookstore.dto.request.CreateUserRequest;
  import com.bennghe.bookstore.dto.response.ApiResponse;
  import com.bennghe.bookstore.dto.response.UserResponse;
  import com.bennghe.bookstore.entity.Role;
  import com.bennghe.bookstore.service.UserService;
  import jakarta.validation.Valid;
  import lombok.RequiredArgsConstructor;
  import org.springframework.data.domain.Page;
  import org.springframework.data.domain.Pageable;
  import org.springframework.http.ResponseEntity;
  import org.springframework.security.access.prepost.PreAuthorize;
  import org.springframework.web.bind.annotation.*;

  import java.util.List;

  @RestController
  @RequestMapping("/api/admin/users")
  @RequiredArgsConstructor
  public class UserController {

      private final UserService userService ;

      /**
       * GET /api/admin/users
       * Lấy danh sách user có phân trang
       */
      @GetMapping
      @PreAuthorize("hasAuthority('USER_READ')")
      public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
          Page<UserResponse> page = userService.getAllUsers(pageable);
          return ResponseEntity.ok(ApiResponse.ok(page));
      }

      /**
       * GET /api/admin/users/:id
       * Lấy thông tin 1 user
       */
      @GetMapping("/{id}")
      @PreAuthorize("hasAuthority('USER_READ')")
      public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Integer id) {
          UserResponse user = userService.getById(id);
          return ResponseEntity.ok(ApiResponse.ok(user));
      }

      /**
       * POST /api/admin/users
       * Tạo user mới
       */
      @PostMapping
      @PreAuthorize("hasAuthority('USER_CREATE')")
      public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
          UserResponse user = userService.createUser(request);
          return ResponseEntity.ok(ApiResponse.ok("Tạo người dùng thành công", user));
      }

      /**
       * PUT /api/admin/users/:id
       * Cập nhật user
       */
      @PutMapping("/{id}")
      @PreAuthorize("hasAuthority('USER_UPDATE')")
      public ResponseEntity<ApiResponse<UserResponse>> updateUser(
              @PathVariable Integer id,
              @Valid @RequestBody CreateUserRequest request) {
          UserResponse user = userService.updateUser(id, request);
          return ResponseEntity.ok(ApiResponse.ok("Cập nhật thành công", user));
      }

      /**
       * PATCH /api/admin/users/:id/toggle
       * Bật/tắt trạng thái user
       */
      @PatchMapping("/{id}/toggle")
      @PreAuthorize("hasAuthority('USER_UPDATE')")
      public ResponseEntity<ApiResponse<UserResponse>> toggleActive(@PathVariable Integer id) {
          UserResponse user = userService.toggleActive(id);
          return ResponseEntity.ok(ApiResponse.ok(
                  user.getIsActive() ? "Đã kích hoạt tài khoản" : "Đã vô hiệu hóa tài khoản",
                  user
          ));
      }

      /**
       * DELETE /api/admin/users/:id
       * Xóa user
       */
      @DeleteMapping("/{id}")
      @PreAuthorize("hasAuthority('USER_DELETE')")
      public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer id) {
          userService.deleteUser(id);
          return ResponseEntity.ok(ApiResponse.ok("Xóa người dùng thành công", null));
      }

      /**
       * GET /api/admin/users/roles
       * Lấy danh sách vai trò (cho dropdown)
       */
      @GetMapping("/roles")
      @PreAuthorize("hasAuthority('USER_READ')")
      public ResponseEntity<ApiResponse<List<Role>>> getRoles() {
          return ResponseEntity.ok(ApiResponse.ok(userService.getAllRoles()));
      }
  }