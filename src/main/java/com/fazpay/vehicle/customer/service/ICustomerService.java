package com.fazpay.vehicle.customer.service;

import com.fazpay.vehicle.customer.dto.CustomerPatchRequest;
import com.fazpay.vehicle.customer.dto.CustomerRequest;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Customer operations.
 * Following Dependency Inversion Principle - depend on abstractions, not concretions.
 */
public interface ICustomerService {
    
    /**
     * Find all customers with pagination.
     *
     * @param pageable pagination information
     * @return page of customer responses
     */
    Page<CustomerResponse> findAll(Pageable pageable);
    
    /**
     * Find customers with filters and pagination.
     *
     * @param nome filter by name (optional)
     * @param dataCriacao filter by creation date (optional)
     * @param pageable pagination information
     * @return page of customer responses
     */
    Page<CustomerResponse> findWithFilters(String nome, LocalDateTime dataCriacao, Pageable pageable);
    
    /**
     * Find all customers without pagination.
     *
     * @return list of all customer responses
     */
    List<CustomerResponse> findAll();
    
    /**
     * Find customer by ID.
     *
     * @param id customer ID
     * @return customer response
     */
    CustomerResponse findById(UUID id);
    
    /**
     * Create a new customer.
     *
     * @param request customer creation request
     * @return created customer response
     */
    CustomerResponse create(CustomerRequest request);
    
    /**
     * Partially update a customer.
     *
     * @param id customer ID
     * @param request partial update request
     * @return updated customer response
     */
    CustomerResponse partialUpdate(UUID id, CustomerPatchRequest request);
    
    /**
     * Delete a customer (soft delete).
     *
     * @param id customer ID
     */
    void delete(UUID id);
}

