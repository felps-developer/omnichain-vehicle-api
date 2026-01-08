package com.fazpay.vehicle.customer.service;

import com.fazpay.vehicle.customer.dto.CustomerPatchRequest;
import com.fazpay.vehicle.customer.dto.CustomerRequest;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ICustomerService {
    
    Page<CustomerResponse> findAll(Pageable pageable);
    
    Page<CustomerResponse> findWithFilters(String nome, LocalDateTime dataCriacao, Pageable pageable);
    
    List<CustomerResponse> findAll();
    
    CustomerResponse findById(UUID id);
    
    CustomerResponse create(CustomerRequest request);
    
    CustomerResponse partialUpdate(UUID id, CustomerPatchRequest request);
    
    void delete(UUID id);
}

