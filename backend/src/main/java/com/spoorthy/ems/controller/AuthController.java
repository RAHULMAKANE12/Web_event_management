package com.spoorthy.ems.controller;

import com.spoorthy.ems.dto.ApiResponse;
import com.spoorthy.ems.dto.LoginRequest;
import com.spoorthy.ems.dto.RegisterRequest;
import com.spoorthy.ems.entity.User;
import com.spoorthy.ems.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        Map<String, Object> userData = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "rollNumber", user.getRollNumber(),
                "role", user.getRole().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful! Please login.", userData));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> userData = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful!", userData));
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(@PathVariable Long id) {
        User user = authService.getUserById(id);
        Map<String, Object> userData = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "rollNumber", user.getRollNumber(),
                "mobile", user.getMobile(),
                "role", user.getRole().name()
        );
        return ResponseEntity.ok(ApiResponse.success(userData));
    }
}
