package com.fazpay.vehicle.customer.service;

import com.fazpay.vehicle.core.constants.CacheNames;
import com.fazpay.vehicle.core.constants.ErrorMessages;
import com.fazpay.vehicle.core.exception.BusinessException;
import com.fazpay.vehicle.core.exception.ResourceNotFoundException;
import com.fazpay.vehicle.customer.dto.CustomerPatchRequest;
import com.fazpay.vehicle.customer.dto.CustomerRequest;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import com.fazpay.vehicle.customer.mapper.CustomerMapper;
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
public class CustomerServiceImpl implements ICustomerService {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.CUSTOMERS, key = "'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<CustomerResponse> findAll(Pageable pageable) {
        log.debug("Finding all customers with pagination - page: {}, size: {}", 
                  pageable.getPageNumber(), pageable.getPageSize());
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findWithFilters(String nome, LocalDateTime dataCriacao, Pageable pageable) {
        log.debug("Finding customers with filters - name: {}, created: {}", nome, dataCriacao);
        return customerRepository.findWithFilters(nome, dataCriacao, pageable)
                .map(customerMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        log.debug("Finding all customers without pagination");
        return customerRepository.findAllByDeletedAtIsNull().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.CUSTOMERS, key = "#id")
    public CustomerResponse findById(UUID id) {
        log.debug("Finding customer by id: {}", id);
        Customer customer = findCustomerByIdOrThrow(id);
        return customerMapper.toResponse(customer);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = CacheNames.CUSTOMERS, allEntries = true)
    public CustomerResponse create(CustomerRequest request) {
        log.info("Creating new customer with CPF: {}", request.getCpf());
        
        validateCpfUniqueness(request.getCpf(), null);
        validateEmailUniqueness(request.getEmail(), null);
        
        Customer customer = customerMapper.toEntity(request);
        customer = customerRepository.save(customer);
        
        log.info("Customer created successfully with id: {}", customer.getId());
        return customerMapper.toResponse(customer);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = CacheNames.CUSTOMERS, allEntries = true)
    public CustomerResponse partialUpdate(UUID id, CustomerPatchRequest request) {
        log.info("Partially updating customer with id: {}", id);
        
        validatePatchRequestNotEmpty(request);
        
        Customer customer = findCustomerByIdOrThrow(id);
        
        updateCustomerFields(customer, request, id);
        
        customer = customerRepository.save(customer);
        log.info("Customer updated successfully with id: {}", id);
        
        return customerMapper.toResponse(customer);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = CacheNames.CUSTOMERS, allEntries = true)
    public void delete(UUID id) {
        log.info("Deleting customer with id: {}", id);
        
        Customer customer = findCustomerByIdOrThrow(id);
        customerRepository.delete(customer);
        
        log.info("Customer soft deleted successfully with id: {}", id);
    }
    
    // Métodos auxiliares privados
    
    private Customer findCustomerByIdOrThrow(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }
    
    private void validateCpfUniqueness(String cpf, UUID excludeId) {
        if (cpf == null) {
            return;
        }
        
        customerRepository.findByCpf(cpf).ifPresent(existingCustomer -> {
            if (excludeId == null || !existingCustomer.getId().equals(excludeId)) {
                throw new BusinessException(String.format(ErrorMessages.CUSTOMER_CPF_ALREADY_EXISTS, cpf));
            }
        });
    }
    
    private void validateEmailUniqueness(String email, UUID excludeId) {
        if (email == null) {
            return;
        }
        
        customerRepository.findByEmail(email).ifPresent(existingCustomer -> {
            if (excludeId == null || !existingCustomer.getId().equals(excludeId)) {
                throw new BusinessException(String.format(ErrorMessages.CUSTOMER_EMAIL_ALREADY_EXISTS, email));
            }
        });
    }
    
    private void validatePatchRequestNotEmpty(CustomerPatchRequest request) {
        if (request.getNome() == null && 
            request.getCpf() == null && 
            request.getEmail() == null && 
            request.getTelefone() == null) {
            throw new BusinessException(ErrorMessages.PATCH_REQUEST_EMPTY);
        }
    }
    
    private void updateCustomerFields(Customer customer, CustomerPatchRequest request, UUID customerId) {
        if (request.getNome() != null) {
            customer.setNome(request.getNome());
        }
        
        if (request.getCpf() != null) {
            validateCpfUniqueness(request.getCpf(), customerId);
            customer.setCpf(request.getCpf());
        }
        
        if (request.getEmail() != null) {
            validateEmailUniqueness(request.getEmail(), customerId);
            customer.setEmail(request.getEmail());
        }
        
        if (request.getTelefone() != null) {
            customer.setTelefone(request.getTelefone());
        }
    }
}

