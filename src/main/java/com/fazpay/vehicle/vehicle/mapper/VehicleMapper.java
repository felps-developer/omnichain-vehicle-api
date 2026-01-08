package com.fazpay.vehicle.vehicle.mapper;

import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleResponse;
import com.fazpay.vehicle.vehicle.model.Vehicle;
import org.springframework.stereotype.Component;

/**
 * Mapper for Vehicle entity and DTOs.
 * Following Single Responsibility Principle - dedicated class for mapping logic.
 */
@Component
public class VehicleMapper {
    
    /**
     * Maps Vehicle entity to VehicleResponse DTO.
     *
     * @param vehicle the vehicle entity
     * @return the vehicle response DTO
     */
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
    
    /**
     * Maps VehicleRequest DTO to Vehicle entity (without customer).
     *
     * @param request the vehicle request DTO
     * @return the vehicle entity (customer needs to be set separately)
     */
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

