package com.fazpay.vehicle.vehicle.service;

import com.fazpay.vehicle.core.exception.BusinessException;
import com.fazpay.vehicle.core.exception.ResourceNotFoundException;
import com.fazpay.vehicle.customer.model.Customer;
import com.fazpay.vehicle.customer.repository.CustomerRepository;
import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleResponse;
import com.fazpay.vehicle.vehicle.model.Vehicle;
import com.fazpay.vehicle.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    
    @Transactional(readOnly = true)
    public Page<VehicleResponse> findAll(Pageable pageable) {
        log.debug("Finding all vehicles with pagination");
        return vehicleRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<VehicleResponse> findWithFilters(String marca, String modelo, String cor, Pageable pageable) {
        log.debug("Finding vehicles with filters - brand: {}, model: {}, color: {}", marca, modelo, cor);
        return vehicleRepository.findWithFilters(marca, modelo, cor, pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public List<VehicleResponse> findAll() {
        log.debug("Finding all vehicles without pagination");
        return vehicleRepository.findAllByDeletedAtIsNull().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "vehicles", key = "#id")
    public VehicleResponse findById(UUID id) {
        log.debug("Finding vehicle by id: {}", id);
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return mapToResponse(vehicle);
    }
    
    @Transactional(readOnly = true)
    public VehicleResponse findByPlaca(String placa) {
        log.debug("Finding vehicle by license plate: {}", placa);
        Vehicle vehicle = vehicleRepository.findByPlaca(placa)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "placa", placa));
        return mapToResponse(vehicle);
    }
    
    @Transactional
    @CacheEvict(value = "vehicles", allEntries = true)
    public VehicleResponse create(VehicleRequest request) {
        log.info("Creating new vehicle with plate: {}", request.getPlaca());
        
        if (vehicleRepository.existsByPlaca(request.getPlaca())) {
            throw new BusinessException("Vehicle with license plate " + request.getPlaca() + " already exists");
        }
        
        Customer customer = customerRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getClienteId()));
        
        Vehicle vehicle = Vehicle.builder()
                .placa(request.getPlaca())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .ano(request.getAno())
                .cor(request.getCor())
                .customer(customer)
                .build();
        
        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created successfully with id: {}", vehicle.getId());
        
        return mapToResponse(vehicle);
    }
    
    @Transactional
    @CacheEvict(value = "vehicles", allEntries = true)
    public VehicleResponse update(UUID id, VehicleRequest request) {
        log.info("Updating vehicle with id: {}", id);
        
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        
        // Check if plate is being changed and if it already exists
        if (!vehicle.getPlaca().equals(request.getPlaca()) && vehicleRepository.existsByPlaca(request.getPlaca())) {
            throw new BusinessException("Vehicle with license plate " + request.getPlaca() + " already exists");
        }
        
        Customer customer = customerRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getClienteId()));
        
        vehicle.setPlaca(request.getPlaca());
        vehicle.setMarca(request.getMarca());
        vehicle.setModelo(request.getModelo());
        vehicle.setAno(request.getAno());
        vehicle.setCor(request.getCor());
        vehicle.setCustomer(customer);
        
        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated successfully with id: {}", id);
        
        return mapToResponse(vehicle);
    }
    
    @Transactional
    @CacheEvict(value = "vehicles", allEntries = true)
    public void delete(UUID id) {
        log.info("Deleting vehicle with id: {}", id);
        
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        
        vehicleRepository.delete(vehicle); // This triggers soft delete via @SQLDelete
        log.info("Vehicle soft deleted successfully with id: {}", id);
    }
    
    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .placa(vehicle.getPlaca())
                .marca(vehicle.getMarca())
                .modelo(vehicle.getModelo())
                .ano(vehicle.getAno())
                .cor(vehicle.getCor())
                .clienteId(vehicle.getCustomer().getId())
                .clienteNome(vehicle.getCustomer().getNome())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}

