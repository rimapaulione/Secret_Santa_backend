package com.secretsanta.backend.service;

import com.secretsanta.backend.dto.request.LoginRequest;
import com.secretsanta.backend.dto.request.RegisterRequest;
import com.secretsanta.backend.dto.response.AdminResponse;
import com.secretsanta.backend.dto.response.LoginResponse;
import com.secretsanta.backend.exception.ConflictException;
import com.secretsanta.backend.exception.UnauthorizedException;
import com.secretsanta.backend.model.Admin;
import com.secretsanta.backend.repository.AdminRepository;
import com.secretsanta.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Register a new admin
     */
    @Transactional
    public AdminResponse register(RegisterRequest request) {
        // Check if email already exists
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        // Create new admin entity
        Admin admin = Admin.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Save to database
        Admin savedAdmin = adminRepository.save(admin);

        // Return response DTO
        return AdminResponse.from(savedAdmin);
    }

    /**
     * Login and generate JWT token
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = tokenProvider.generateToken(authentication);

        // Find admin
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        // Return login response with token and admin info
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(tokenProvider.getExpirationMs())
                .admin(AdminResponse.from(admin))
                .build();
    }

    /**
     * Get currently authenticated admin email
     */
    public String getCurrentAdminEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }
        return authentication.getName();
    }

    /**
     * Get currently authenticated admin
     */
    @Transactional(readOnly = true)
    public Admin getCurrentAdmin() {
        String email = getCurrentAdminEmail();
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Admin not found"));
    }
}
