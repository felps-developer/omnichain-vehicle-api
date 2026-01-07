package com.fazpay.vehicle.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private String username;
    
    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
        this.type = "Bearer";
    }
}

