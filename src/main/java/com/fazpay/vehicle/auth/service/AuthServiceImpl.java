package com.fazpay.vehicle.auth.service;

import com.fazpay.vehicle.auth.dto.LoginRequest;
import com.fazpay.vehicle.auth.dto.LoginResponse;
import com.fazpay.vehicle.auth.dto.RegisterRequest;
import com.fazpay.vehicle.core.constants.ErrorMessages;
import com.fazpay.vehicle.core.exception.BusinessException;
import com.fazpay.vehicle.core.exception.ResourceNotFoundException;
import com.fazpay.vehicle.core.security.JwtTokenProvider;
import com.fazpay.vehicle.user.model.User;
import com.fazpay.vehicle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of Authentication service operations.
 * Following SOLID principles and Clean Code practices.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Authenticating user: {}", request.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        log.info("User authenticated successfully: {}", request.getUsername());
        return new LoginResponse(token, request.getUsername());
    }
    
    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        
        validateUsernameUniqueness(request.getUsername());
        validateEmailUniqueness(request.getEmail());
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());
        
        String token = tokenProvider.generateToken(user.getUsername());
        
        return new LoginResponse(token, user.getUsername());
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(String username) {
        log.debug("Getting current user info for: {}", username);
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    // ==================== Private Helper Methods ====================
    
    /**
     * Validates if username is unique.
     */
    private void validateUsernameUniqueness(String username) {
        if (userRepository.existsByUsername(username)) {
            log.error("Username already exists: {}", username);
            throw new BusinessException(ErrorMessages.USERNAME_ALREADY_EXISTS);
        }
    }
    
    /**
     * Validates if email is unique.
     */
    private void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            log.error("Email already exists: {}", email);
            throw new BusinessException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }
    }
}

