package com.fazpay.vehicle.vehicle.dto;

import com.fazpay.vehicle.core.validation.ValidPlaca;
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
public class VehiclePatchRequest {
    
    @ValidPlaca
    private String placa;
    
    @Size(min = 2, max = 50, message = "Marca deve ter entre 2 e 50 caracteres")
    private String marca;
    
    @Size(min = 2, max = 50, message = "Modelo deve ter entre 2 e 50 caracteres")
    private String modelo;
    
    @Min(value = 1900, message = "Ano deve ser maior ou igual a 1900")
    @Max(value = 2100, message = "Ano deve ser menor ou igual a 2100")
    private Integer ano;
    
    @Size(min = 3, max = 30, message = "Cor deve ter entre 3 e 30 caracteres")
    private String cor;
    
    private UUID clienteId;
}

