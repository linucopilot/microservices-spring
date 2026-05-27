package com.prasac.authservice.service;

import com.prasac.authservice.dto.LoginRequest;
import com.prasac.authservice.dto.LoginResponse;
import com.prasac.authservice.dto.RefreshTokenRequest;
import com.prasac.authservice.dto.RegisterRequest;
import com.prasac.authservice.dto.UserResponse;
import com.prasac.authservice.entity.RefreshToken;
import com.prasac.authservice.entity.User;
import com.prasac.authservice.exception.BadRequestException;
import com.prasac.authservice.exception.UnauthorizedException;
import com.prasac.authservice.mapper.RefreshTokenMapper;
import com.prasac.authservice.mapper.UserMapper;
import com.prasac.authservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        return issueTokens(user);
    }

    public UserResponse register(RegisterRequest request) {
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .enabled(Boolean.TRUE)
                .build();

        userMapper.insert(user);
        return UserResponse.from(userMapper.selectById(user.getId()));
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenMapper.selectByToken(request.getRefreshToken());
        if (storedToken == null || Boolean.TRUE.equals(storedToken.getRevoked())) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenMapper.revokeByToken(request.getRefreshToken());
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = userMapper.selectById(storedToken.getUserId());
        if (user == null || !Boolean.TRUE.equals(user.getEnabled())) {
            refreshTokenMapper.revokeByToken(request.getRefreshToken());
            throw new UnauthorizedException("User is not available");
        }

        refreshTokenMapper.revokeByToken(request.getRefreshToken());
        return issueTokens(user);
    }

    public String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid authorization header");
        }

        return authorizationHeader.substring(7);
    }

    private LoginResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        refreshTokenMapper.insert(RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken)
                .expiresAt(jwtTokenProvider.getRefreshTokenExpiry())
                .revoked(Boolean.FALSE)
                .build());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}
