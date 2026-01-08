package com.fazpay.vehicle.core.constants;

/**
 * Centralized error messages for better maintainability and consistency.
 * Following Clean Code principles - avoid magic strings.
 */
public final class ErrorMessages {
    
    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // Customer related messages
    public static final String CUSTOMER_NOT_FOUND = "Customer not found with %s: %s";
    public static final String CUSTOMER_CPF_ALREADY_EXISTS = "Customer with CPF %s already exists";
    public static final String CUSTOMER_EMAIL_ALREADY_EXISTS = "Customer with email %s already exists";
    
    // Vehicle related messages
    public static final String VEHICLE_NOT_FOUND = "Vehicle not found with %s: %s";
    public static final String VEHICLE_PLACA_ALREADY_EXISTS = "Vehicle with license plate %s already exists";
    
    // User related messages
    public static final String USER_NOT_FOUND = "User not found with %s: %s";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    
    // Authentication messages
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    
    // Validation messages
    public static final String PATCH_REQUEST_EMPTY = "At least one field must be provided for update";
    
    // General messages
    public static final String RESOURCE_NOT_FOUND = "%s not found with %s: %s";
}

