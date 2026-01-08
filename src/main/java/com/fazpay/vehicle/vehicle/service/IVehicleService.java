package com.fazpay.vehicle.vehicle.service;

import com.fazpay.vehicle.vehicle.dto.VehiclePatchRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Vehicle operations.
 * Following Dependency Inversion Principle - depend on abstractions, not concretions.
 */
public interface IVehicleService {
    
    /**
     * Find all vehicles with pagination.
     *
     * @param pageable pagination information
     * @return page of vehicle responses
     */
    Page<VehicleResponse> findAll(Pageable pageable);
    
    /**
     * Find vehicles with filters and pagination.
     *
     * @param marca filter by brand (optional)
     * @param modelo filter by model (optional)
     * @param cor filter by color (optional)
     * @param pageable pagination information
     * @return page of vehicle responses
     */
    Page<VehicleResponse> findWithFilters(String marca, String modelo, String cor, Pageable pageable);
    
    /**
     * Find all vehicles without pagination.
     *
     * @return list of all vehicle responses
     */
    List<VehicleResponse> findAll();
    
    /**
     * Find vehicle by ID.
     *
     * @param id vehicle ID
     * @return vehicle response
     */
    VehicleResponse findById(UUID id);
    
    /**
     * Find vehicle by license plate.
     *
     * @param placa license plate
     * @return vehicle response
     */
    VehicleResponse findByPlaca(String placa);
    
    /**
     * Create a new vehicle.
     *
     * @param request vehicle creation request
     * @return created vehicle response
     */
    VehicleResponse create(VehicleRequest request);
    
    /**
     * Partially update a vehicle.
     *
     * @param id vehicle ID
     * @param request partial update request
     * @return updated vehicle response
     */
    VehicleResponse partialUpdate(UUID id, VehiclePatchRequest request);
    
    /**
     * Delete a vehicle (soft delete).
     *
     * @param id vehicle ID
     */
    void delete(UUID id);
}

