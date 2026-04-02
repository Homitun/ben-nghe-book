package com.bennghe.bookstore.dto.response;

  import lombok.*;
  import java.util.List;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class AuthResponse {
      private Integer userId;
      private String username;
      private String fullName;
      private String role;              // "ADMIN" | "MANAGER" | "STAFF"
      private List<String> permissions; // ["USER_READ", "BOOK_CREATE", ...]
      private String token;
  }