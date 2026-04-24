package com.prasac.authservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Boolean enabled;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
