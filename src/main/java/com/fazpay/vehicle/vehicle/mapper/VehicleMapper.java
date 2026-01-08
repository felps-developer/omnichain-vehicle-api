package com.fazpay.vehicle.vehicle.mapper;

import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleResponse;
import com.fazpay.vehicle.vehicle.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {
    
    public VehicleResponse toResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .placa(vehicle.getPlaca())
                .marca(vehicle.getMarca())
                .modelo(vehicle.getModelo())
                .ano(vehicle.getAno())
                .cor(vehicle.getCor())
                .clienteId(vehicle.getCustomer().getId())
                .clienteNome(vehicle.getCustomer().getNome())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
    
    public Vehicle toEntity(VehicleRequest request) {
        if (request == null) {
            return null;
        }
        
        return Vehicle.builder()
                .placa(request.getPlaca())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .ano(request.getAno())
                .cor(request.getCor())
                .build();
    }
}

