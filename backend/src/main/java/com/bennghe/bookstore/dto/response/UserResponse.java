package com.bennghe.bookstore.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String roleName;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
