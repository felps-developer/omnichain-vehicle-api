package com.fazpay.vehicle.auth.service;

import com.fazpay.vehicle.auth.dto.LoginRequest;
import com.fazpay.vehicle.auth.dto.LoginResponse;
import com.fazpay.vehicle.core.security.JwtTokenProvider;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import com.fazpay.vehicle.customer.model.Customer;
import com.fazpay.vehicle.customer.repository.CustomerRepository;
import com.fazpay.vehicle.user.model.User;
import com.fazpay.vehicle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    
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
    
    @Transactional(readOnly = true)
    public CustomerResponse getCurrentUser(String username) {
        log.debug("Getting current user data for: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getCustomerId() == null) {
            log.warn("User {} has no associated customer", username);
            return null;
        }
        
        Customer customer = customerRepository.findById(user.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        return mapToCustomerResponse(customer);
    }
    
    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .nome(customer.getNome())
                .cpf(customer.getCpf())
                .email(customer.getEmail())
                .telefone(customer.getTelefone())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}

