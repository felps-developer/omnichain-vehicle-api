package com.fazpay.vehicle.auth.controller;

import com.fazpay.vehicle.auth.dto.LoginRequest;
import com.fazpay.vehicle.auth.dto.LoginResponse;
import com.fazpay.vehicle.auth.service.AuthService;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for user: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the customer data associated with the authenticated user")
    public ResponseEntity<CustomerResponse> getCurrentUser(Authentication authentication) {
        log.debug("Current user request from: {}", authentication.getName());
        CustomerResponse response = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(response);
    }
}

