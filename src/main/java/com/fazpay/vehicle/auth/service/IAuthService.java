package com.fazpay.vehicle.auth.service;

import com.fazpay.vehicle.auth.dto.LoginRequest;
import com.fazpay.vehicle.auth.dto.LoginResponse;
import com.fazpay.vehicle.auth.dto.RegisterRequest;
import com.fazpay.vehicle.user.model.User;

public interface IAuthService {
    
    LoginResponse login(LoginRequest request);
    
    LoginResponse register(RegisterRequest request);
    
    User getCurrentUser(String username);
}

