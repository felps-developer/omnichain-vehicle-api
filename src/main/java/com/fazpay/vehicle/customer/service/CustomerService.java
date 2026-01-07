package com.fazpay.vehicle.customer.service;

import com.fazpay.vehicle.core.exception.BusinessException;
import com.fazpay.vehicle.core.exception.ResourceNotFoundException;
import com.fazpay.vehicle.customer.dto.CustomerPatchRequest;
import com.fazpay.vehicle.customer.dto.CustomerRequest;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import com.fazpay.vehicle.customer.model.Customer;
import com.fazpay.vehicle.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(Pageable pageable) {
        log.debug("Finding all customers with pagination");
        return customerRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findWithFilters(String nome, LocalDateTime dataCriacao, Pageable pageable) {
        log.debug("Finding customers with filters - name: {}, created: {}", nome, dataCriacao);
        return customerRepository.findWithFilters(nome, dataCriacao, pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        log.debug("Finding all customers without pagination");
        return customerRepository.findAllByDeletedAtIsNull().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        log.debug("Finding customer by id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return mapToResponse(customer);
    }
    
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        log.info("Creating new customer with CPF: {}", request.getCpf());
        
        if (customerRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("Customer with CPF " + request.getCpf() + " already exists");
        }
        
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Customer with email " + request.getEmail() + " already exists");
        }
        
        Customer customer = Customer.builder()
                .nome(request.getNome())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .telefone(request.getTelefone())
                .build();
        
        customer = customerRepository.save(customer);
        log.info("Customer created successfully with id: {}", customer.getId());
        
        return mapToResponse(customer);
    }
    
    @Transactional
    @CacheEvict(value = "customers", allEntries = true)
    public CustomerResponse partialUpdate(UUID id, CustomerPatchRequest request) {
        log.info("Partially updating customer with id: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        // Update only fields that are not null
        if (request.getNome() != null) {
            customer.setNome(request.getNome());
        }
        
        if (request.getCpf() != null) {
            // Check if CPF is being changed and if it already exists
            if (!customer.getCpf().equals(request.getCpf()) && customerRepository.existsByCpf(request.getCpf())) {
                throw new BusinessException("Customer with CPF " + request.getCpf() + " already exists");
            }
            customer.setCpf(request.getCpf());
        }
        
        if (request.getEmail() != null) {
            // Check if email is being changed and if it already exists
            if (!customer.getEmail().equals(request.getEmail()) && customerRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Customer with email " + request.getEmail() + " already exists");
            }
            customer.setEmail(request.getEmail());
        }
        
        if (request.getTelefone() != null) {
            customer.setTelefone(request.getTelefone());
        }
        
        customer = customerRepository.save(customer);
        log.info("Customer updated successfully with id: {}", id);
        
        return mapToResponse(customer);
    }
    
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting customer with id: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        customerRepository.delete(customer); // This triggers soft delete via @SQLDelete
        log.info("Customer soft deleted successfully with id: {}", id);
    }
    
    private CustomerResponse mapToResponse(Customer customer) {
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
}

