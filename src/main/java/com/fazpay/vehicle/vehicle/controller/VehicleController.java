package com.fazpay.vehicle.vehicle.controller;

import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleResponse;
import com.fazpay.vehicle.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/veiculos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehicles", description = "Vehicle management endpoints")
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    @GetMapping
    @Operation(summary = "List vehicles with pagination and filters")
    public ResponseEntity<Page<VehicleResponse>> findAll(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String cor,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        
        log.debug("GET /api/v1/veiculos - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<VehicleResponse> vehicles = vehicleService.findWithFilters(marca, modelo, cor, pageable);
        return ResponseEntity.ok(vehicles);
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
    
    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle")
    public ResponseEntity<VehicleResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody VehicleRequest request) {
        log.info("PUT /api/v1/veiculos/{}", id);
        VehicleResponse vehicle = vehicleService.update(id, request);
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

