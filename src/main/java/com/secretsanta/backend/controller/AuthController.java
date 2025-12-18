package com.secretsanta.backend.controller;

import com.secretsanta.backend.dto.request.LoginRequest;
import com.secretsanta.backend.dto.request.RegisterRequest;
import com.secretsanta.backend.dto.response.AdminResponse;
import com.secretsanta.backend.dto.response.LoginResponse;
import com.secretsanta.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * POST /api/auth/register
     * Register a new admin account
     */
    @PostMapping("/register")
    public ResponseEntity<AdminResponse> register(@Valid @RequestBody RegisterRequest request) {
        AdminResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/auth/login
     * Authenticate and receive JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
