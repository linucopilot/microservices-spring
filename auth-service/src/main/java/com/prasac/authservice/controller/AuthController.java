package com.prasac.authservice.controller;

import com.prasac.authservice.dto.LoginRequest;
import com.prasac.authservice.entity.User;
import com.prasac.authservice.service.AuthService;
import com.prasac.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody User user) {
        User registered = authService.register(user);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", registered));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<?>> validateToken(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        boolean valid = authService.validateToken(jwt);
        return ResponseEntity.ok(ApiResponse.success(valid ? "Token valid" : "Token invalid", valid));
    }
}
