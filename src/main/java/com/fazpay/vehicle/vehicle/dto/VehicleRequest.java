package com.fazpay.vehicle.vehicle.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {
    
    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "[A-Z]{3}[0-9][A-Z0-9][0-9]{2}",
            message = "Invalid license plate format (expected: ABC1234 or ABC1D23)")
    private String placa;
    
    @NotBlank(message = "Brand is required")
    private String marca;
    
    @NotBlank(message = "Model is required")
    private String modelo;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be greater than or equal to 1900")
    @Max(value = 2100, message = "Year must be less than or equal to 2100")
    private Integer ano;
    
    @NotBlank(message = "Color is required")
    private String cor;
    
    @NotNull(message = "Customer ID is required")
    private UUID clienteId;
}

