package com.fazpay.vehicle.customer.controller;

import com.fazpay.vehicle.core.dto.PageResponse;
import com.fazpay.vehicle.customer.dto.CustomerPatchRequest;
import com.fazpay.vehicle.customer.dto.CustomerRequest;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import com.fazpay.vehicle.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    @Operation(
        summary = "List customers with pagination and filters",
        description = "Returns a paginated list of customers. " +
                      "Optional filters: nome, dataCriacao. " +
                      "Optional sort: field name (default: nome). Example: ?page=0&size=10&sort=nome"
    )
    public ResponseEntity<PageResponse<CustomerResponse>> findAll(
            @Parameter(description = "Filter by customer name (partial match)")
            @RequestParam(required = false) String nome,
            
            @Parameter(description = "Filter by creation date (ISO format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataCriacao,
            
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size (default: 10)")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort field (optional). Available: nome, cpf, email, createdAt")
            @RequestParam(required = false) String sort) {
        
        log.debug("GET /api/v1/clientes - page: {}, size: {}", page, size);
        
        PageRequest pageable = sort != null 
            ? PageRequest.of(page, size, Sort.by(sort))
            : PageRequest.of(page, size, Sort.by("nome"));
        
        Page<CustomerResponse> pageResult = customerService.findWithFilters(nome, dataCriacao, pageable);
        
        PageResponse<CustomerResponse> response = new PageResponse<>(
            pageResult.getContent(),
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.isFirst(),
            pageResult.isLast()
        );
        
        return ResponseEntity.ok(response);
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
    
    @PatchMapping("/{id}")
    @Operation(summary = "Update customer (partial update)", 
               description = "Update one or more customer fields. All fields are optional.")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id, 
                                                   @Valid @RequestBody CustomerPatchRequest request) {
        log.info("PATCH /api/v1/clientes/{}", id);
        CustomerResponse customer = customerService.partialUpdate(id, request);
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

