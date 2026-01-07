package com.fazpay.vehicle.vehicle.controller;

import com.fazpay.vehicle.core.dto.PageResponse;
import com.fazpay.vehicle.vehicle.dto.VehiclePatchRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleResponse;
import com.fazpay.vehicle.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/veiculos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehicles", description = "Vehicle management endpoints")
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    @GetMapping
    @Operation(
        summary = "List vehicles with pagination and filters",
        description = "Returns a paginated list of vehicles. " +
                      "Optional filters: marca, modelo, cor. " +
                      "Optional sort: field name (default: placa). Example: ?page=0&size=10&sort=marca"
    )
    public ResponseEntity<PageResponse<VehicleResponse>> findAll(
            @Parameter(description = "Filter by vehicle brand (partial match)")
            @RequestParam(required = false) String marca,
            
            @Parameter(description = "Filter by vehicle model (partial match)")
            @RequestParam(required = false) String modelo,
            
            @Parameter(description = "Filter by vehicle color (partial match)")
            @RequestParam(required = false) String cor,
            
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size (default: 10)")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort field (optional). Available: placa, marca, modelo, ano, cor, createdAt")
            @RequestParam(required = false) String sort) {
        
        log.debug("GET /api/v1/veiculos - page: {}, size: {}", page, size);
        
        PageRequest pageable = sort != null 
            ? PageRequest.of(page, size, Sort.by(sort))
            : PageRequest.of(page, size, Sort.by("placa"));
        
        Page<VehicleResponse> pageResult = vehicleService.findWithFilters(marca, modelo, cor, pageable);
        
        PageResponse<VehicleResponse> response = new PageResponse<>(
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
    @Operation(summary = "List all vehicles without pagination")
    public ResponseEntity<List<VehicleResponse>> findAllWithoutPagination() {
        log.debug("GET /api/v1/veiculos/all");
        List<VehicleResponse> vehicles = vehicleService.findAll();
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID")
    public ResponseEntity<VehicleResponse> findById(@PathVariable UUID id) {
        log.debug("GET /api/v1/veiculos/{}", id);
        VehicleResponse vehicle = vehicleService.findById(id);
        return ResponseEntity.ok(vehicle);
    }
    
    @GetMapping("/placa/{placa}")
    @Operation(summary = "Get vehicle by license plate")
    public ResponseEntity<VehicleResponse> findByPlaca(@PathVariable String placa) {
        log.debug("GET /api/v1/veiculos/placa/{}", placa);
        VehicleResponse vehicle = vehicleService.findByPlaca(placa);
        return ResponseEntity.ok(vehicle);
    }
    
    @PostMapping
    @Operation(summary = "Create new vehicle")
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        log.info("POST /api/v1/veiculos");
        VehicleResponse vehicle = vehicleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Update vehicle (partial update)",
               description = "Update one or more vehicle fields. All fields are optional.")
    public ResponseEntity<VehicleResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody VehiclePatchRequest request) {
        log.info("PATCH /api/v1/veiculos/{}", id);
        VehicleResponse vehicle = vehicleService.partialUpdate(id, request);
        return ResponseEntity.ok(vehicle);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/veiculos/{}", id);
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

