package com.fazpay.vehicle.vehicle.service;

import com.fazpay.vehicle.core.constants.CacheNames;
import com.fazpay.vehicle.core.constants.ErrorMessages;
import com.fazpay.vehicle.core.exception.BusinessException;
import com.fazpay.vehicle.core.exception.ResourceNotFoundException;
import com.fazpay.vehicle.customer.model.Customer;
import com.fazpay.vehicle.customer.repository.CustomerRepository;
import com.fazpay.vehicle.vehicle.dto.VehiclePatchRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleResponse;
import com.fazpay.vehicle.vehicle.mapper.VehicleMapper;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements IVehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleMapper vehicleMapper;
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.VEHICLES, key = "'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<VehicleResponse> findAll(Pageable pageable) {
        log.debug("Finding all vehicles with pagination - page: {}, size: {}", 
                  pageable.getPageNumber(), pageable.getPageSize());
        return vehicleRepository.findAllWithCustomer(pageable)
                .map(vehicleMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponse> findWithFilters(String marca, String modelo, String cor, Pageable pageable) {
        log.debug("Finding vehicles with filters - brand: {}, model: {}, color: {}", marca, modelo, cor);
        return vehicleRepository.findWithFilters(marca, modelo, cor, pageable)
                .map(vehicleMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> findAll() {
        log.debug("Finding all vehicles without pagination");
        return vehicleRepository.findAllByDeletedAtIsNull().stream()
                .map(vehicleMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.VEHICLES, key = "#id")
    public VehicleResponse findById(UUID id) {
        log.debug("Finding vehicle by id: {}", id);
        Vehicle vehicle = findVehicleByIdOrThrow(id);
        return vehicleMapper.toResponse(vehicle);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.VEHICLES, key = "'placa_' + #placa")
    public VehicleResponse findByPlaca(String placa) {
        log.debug("Finding vehicle by license plate: {}", placa);
        Vehicle vehicle = vehicleRepository.findByPlaca(placa)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "placa", placa));
        return vehicleMapper.toResponse(vehicle);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = CacheNames.VEHICLES, allEntries = true)
    public VehicleResponse create(VehicleRequest request) {
        log.info("Creating new vehicle with plate: {}", request.getPlaca());
        
        validatePlacaUniqueness(request.getPlaca(), null);
        
        Customer customer = findCustomerByIdOrThrow(request.getClienteId());
        
        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setCustomer(customer);
        
        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created successfully with id: {}", vehicle.getId());
        
        return vehicleMapper.toResponse(vehicle);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = CacheNames.VEHICLES, allEntries = true)
    public VehicleResponse partialUpdate(UUID id, VehiclePatchRequest request) {
        log.info("Partially updating vehicle with id: {}", id);
        
        validatePatchRequestNotEmpty(request);
        
        Vehicle vehicle = findVehicleByIdOrThrow(id);
        
        updateVehicleFields(vehicle, request, id);
        
        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated successfully with id: {}", id);
        
        return vehicleMapper.toResponse(vehicle);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = CacheNames.VEHICLES, allEntries = true)
    public void delete(UUID id) {
        log.info("Deleting vehicle with id: {}", id);
        
        Vehicle vehicle = findVehicleByIdOrThrow(id);
        vehicleRepository.delete(vehicle);
        
        log.info("Vehicle soft deleted successfully with id: {}", id);
    }
    
    // Métodos auxiliares privados
    
    private Vehicle findVehicleByIdOrThrow(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
    }
    
    private Customer findCustomerByIdOrThrow(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }
    
    private void validatePlacaUniqueness(String placa, UUID excludeId) {
        if (placa == null) {
            return;
        }
        
        vehicleRepository.findByPlaca(placa).ifPresent(existingVehicle -> {
            if (excludeId == null || !existingVehicle.getId().equals(excludeId)) {
                throw new BusinessException(String.format(ErrorMessages.VEHICLE_PLACA_ALREADY_EXISTS, placa));
            }
        });
    }
    
    private void validatePatchRequestNotEmpty(VehiclePatchRequest request) {
        if (request.getPlaca() == null && 
            request.getMarca() == null && 
            request.getModelo() == null && 
            request.getAno() == null && 
            request.getCor() == null && 
            request.getClienteId() == null) {
            throw new BusinessException(ErrorMessages.PATCH_REQUEST_EMPTY);
        }
    }
    
    private void updateVehicleFields(Vehicle vehicle, VehiclePatchRequest request, UUID vehicleId) {
        if (request.getPlaca() != null) {
            validatePlacaUniqueness(request.getPlaca(), vehicleId);
            vehicle.setPlaca(request.getPlaca());
        }
        
        if (request.getMarca() != null) {
            vehicle.setMarca(request.getMarca());
        }
        
        if (request.getModelo() != null) {
            vehicle.setModelo(request.getModelo());
        }
        
        if (request.getAno() != null) {
            vehicle.setAno(request.getAno());
        }
        
        if (request.getCor() != null) {
            vehicle.setCor(request.getCor());
        }
        
        if (request.getClienteId() != null) {
            Customer customer = findCustomerByIdOrThrow(request.getClienteId());
            vehicle.setCustomer(customer);
        }
    }
}

