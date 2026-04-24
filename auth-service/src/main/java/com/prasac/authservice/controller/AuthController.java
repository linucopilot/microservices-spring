package com.prasac.authservice.controller;

import com.prasac.authservice.dto.LoginRequest;
import com.prasac.authservice.dto.RegisterRequest;
import com.prasac.authservice.service.AuthService;
import com.prasac.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authService.register(request)));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<?>> validateToken(@RequestHeader("Authorization") String token) {
        String jwt = authService.extractBearerToken(token);
        boolean valid = authService.validateToken(jwt);
        return ResponseEntity.ok(ApiResponse.success(valid ? "Token valid" : "Token invalid", valid));
    }
}
