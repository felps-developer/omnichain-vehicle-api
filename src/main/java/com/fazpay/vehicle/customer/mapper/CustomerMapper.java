package com.fazpay.vehicle.customer.mapper;

import com.fazpay.vehicle.customer.dto.CustomerRequest;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import com.fazpay.vehicle.customer.model.Customer;
import org.springframework.stereotype.Component;

/**
 * Mapper for Customer entity and DTOs.
 * Following Single Responsibility Principle - dedicated class for mapping logic.
 */
@Component
public class CustomerMapper {
    
    /**
     * Maps Customer entity to CustomerResponse DTO.
     *
     * @param customer the customer entity
     * @return the customer response DTO
     */
    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        
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
    
    /**
     * Maps CustomerRequest DTO to Customer entity.
     *
     * @param request the customer request DTO
     * @return the customer entity
     */
    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        
        return Customer.builder()
                .nome(request.getNome())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .telefone(request.getTelefone())
                .build();
    }
}

