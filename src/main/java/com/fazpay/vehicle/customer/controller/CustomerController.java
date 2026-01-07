package com.fazpay.vehicle.customer.controller;

import com.fazpay.vehicle.customer.dto.CustomerRequest;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import com.fazpay.vehicle.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customers", description = "Customer management endpoints")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    @Operation(summary = "List customers with pagination and filters")
    public ResponseEntity<Page<CustomerResponse>> findAll(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataCriacao,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        
        log.debug("GET /api/v1/clientes - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<CustomerResponse> customers = customerService.findWithFilters(nome, dataCriacao, pageable);
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/all")
    @Operation(summary = "List all customers without pagination")
    public ResponseEntity<List<CustomerResponse>> findAllWithoutPagination() {
        log.debug("GET /api/v1/clientes/all");
        List<CustomerResponse> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        log.debug("GET /api/v1/clientes/{}", id);
        CustomerResponse customer = customerService.findById(id);
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping
    @Operation(summary = "Create new customer")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        log.info("POST /api/v1/clientes");
        CustomerResponse customer = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id, 
                                                   @Valid @RequestBody CustomerRequest request) {
        log.info("PUT /api/v1/clientes/{}", id);
        CustomerResponse customer = customerService.update(id, request);
        return ResponseEntity.ok(customer);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/clientes/{}", id);
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

