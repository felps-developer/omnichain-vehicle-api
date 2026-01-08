package com.fazpay.vehicle.auth.service;

import com.fazpay.vehicle.auth.dto.LoginRequest;
import com.fazpay.vehicle.auth.dto.LoginResponse;
import com.fazpay.vehicle.auth.dto.RegisterRequest;
import com.fazpay.vehicle.user.model.User;

/**
 * Service interface for Authentication operations.
 * Following Dependency Inversion Principle - depend on abstractions, not concretions.
 */
public interface IAuthService {
    
    /**
     * Authenticate user and generate JWT token.
     *
     * @param request login request with username and password
     * @return login response with JWT token
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * Register a new user.
     *
     * @param request registration request
     * @return login response with JWT token
     */
    LoginResponse register(RegisterRequest request);
    
    /**
     * Get current authenticated user information.
     *
     * @param username username of the current user
     * @return user entity
     */
    User getCurrentUser(String username);
}

