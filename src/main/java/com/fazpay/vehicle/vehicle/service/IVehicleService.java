package com.fazpay.vehicle.vehicle.service;

import com.fazpay.vehicle.vehicle.dto.VehiclePatchRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IVehicleService {
    
    Page<VehicleResponse> findAll(Pageable pageable);
    
    Page<VehicleResponse> findWithFilters(String marca, String modelo, String cor, Pageable pageable);
    
    List<VehicleResponse> findAll();
    
    VehicleResponse findById(UUID id);
    
    VehicleResponse findByPlaca(String placa);
    
    VehicleResponse create(VehicleRequest request);
    
    VehicleResponse partialUpdate(UUID id, VehiclePatchRequest request);
    
    void delete(UUID id);
}

