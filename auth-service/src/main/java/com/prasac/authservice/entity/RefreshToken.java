package com.prasac.authservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime expiresAt;
    private Boolean revoked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
