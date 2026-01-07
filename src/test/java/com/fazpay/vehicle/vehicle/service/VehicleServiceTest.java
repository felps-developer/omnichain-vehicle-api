package com.fazpay.vehicle.vehicle.service;

import com.fazpay.vehicle.core.exception.BusinessException;
import com.fazpay.vehicle.core.exception.ResourceNotFoundException;
import com.fazpay.vehicle.customer.model.Customer;
import com.fazpay.vehicle.customer.repository.CustomerRepository;
import com.fazpay.vehicle.vehicle.dto.VehiclePatchRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleResponse;
import com.fazpay.vehicle.vehicle.model.Vehicle;
import com.fazpay.vehicle.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Vehicle Service Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private Customer customer;
    private VehicleRequest vehicleRequest;
    private VehiclePatchRequest vehiclePatchRequest;
    private UUID vehicleId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        vehicleId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        customer = Customer.builder()
                .id(customerId)
                .nome("João Silva")
                .cpf("12345678909")
                .email("joao@example.com")
                .telefone("(11) 98765-4321")
                .build();

        vehicle = Vehicle.builder()
                .id(vehicleId)
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2023)
                .cor("Prata")
                .customer(customer)
                .build();

        vehicleRequest = VehicleRequest.builder()
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2023)
                .cor("Prata")
                .clienteId(customerId)
                .build();

        vehiclePatchRequest = VehiclePatchRequest.builder()
                .cor("Azul")
                .build();
    }

    @Test
    @DisplayName("Should find all vehicles with pagination")
    void shouldFindAllVehiclesWithPagination() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        List<Vehicle> vehicles = Arrays.asList(vehicle);
        Page<Vehicle> vehiclePage = new PageImpl<>(vehicles, pageable, 1);
        
        when(vehicleRepository.findWithFilters(null, null, null, pageable)).thenReturn(vehiclePage);

        // When
        Page<VehicleResponse> result = vehicleService.findWithFilters(null, null, null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPlaca()).isEqualTo("ABC1234");
        verify(vehicleRepository).findWithFilters(null, null, null, pageable);
    }

    @Test
    @DisplayName("Should find vehicle by ID")
    void shouldFindVehicleById() {
        // Given
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        // When
        VehicleResponse result = vehicleService.findById(vehicleId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(vehicleId);
        assertThat(result.getPlaca()).isEqualTo("ABC1234");
        verify(vehicleRepository).findById(vehicleId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vehicle not found by ID")
    void shouldThrowExceptionWhenVehicleNotFoundById() {
        // Given
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vehicleService.findById(vehicleId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle")
                .hasMessageContaining(vehicleId.toString());
        
        verify(vehicleRepository).findById(vehicleId);
    }

    @Test
    @DisplayName("Should find vehicle by plate")
    void shouldFindVehicleByPlate() {
        // Given
        when(vehicleRepository.findByPlaca("ABC1234")).thenReturn(Optional.of(vehicle));

        // When
        VehicleResponse result = vehicleService.findByPlaca("ABC1234");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPlaca()).isEqualTo("ABC1234");
        verify(vehicleRepository).findByPlaca("ABC1234");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vehicle not found by plate")
    void shouldThrowExceptionWhenVehicleNotFoundByPlate() {
        // Given
        when(vehicleRepository.findByPlaca("ABC1234")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vehicleService.findByPlaca("ABC1234"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle")
                .hasMessageContaining("ABC1234");
        
        verify(vehicleRepository).findByPlaca("ABC1234");
    }

    @Test
    @DisplayName("Should create new vehicle")
    void shouldCreateNewVehicle() {
        // Given
        when(vehicleRepository.existsByPlaca(vehicleRequest.getPlaca())).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        // When
        VehicleResponse result = vehicleService.create(vehicleRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPlaca()).isEqualTo("ABC1234");
        assertThat(result.getMarca()).isEqualTo("Toyota");
        verify(vehicleRepository).existsByPlaca(vehicleRequest.getPlaca());
        verify(customerRepository).findById(customerId);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when plate already exists")
    void shouldThrowExceptionWhenPlateAlreadyExists() {
        // Given
        when(vehicleRepository.existsByPlaca(vehicleRequest.getPlaca())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> vehicleService.create(vehicleRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("license plate");
        
        verify(vehicleRepository).existsByPlaca(vehicleRequest.getPlaca());
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        when(vehicleRepository.existsByPlaca(vehicleRequest.getPlaca())).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vehicleService.create(vehicleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer");
        
        verify(vehicleRepository).existsByPlaca(vehicleRequest.getPlaca());
        verify(customerRepository).findById(customerId);
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should partially update vehicle")
    void shouldPartiallyUpdateVehicle() {
        // Given
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        // When
        VehicleResponse result = vehicleService.partialUpdate(vehicleId, vehiclePatchRequest);

        // Then
        assertThat(result).isNotNull();
        verify(vehicleRepository).findById(vehicleId);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should update only cor when provided")
    void shouldUpdateOnlyCorWhenProvided() {
        // Given
        VehiclePatchRequest patchRequest = VehiclePatchRequest.builder()
                .cor("Azul")
                .build();
        
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        vehicleService.partialUpdate(vehicleId, patchRequest);

        // Then
        assertThat(vehicle.getCor()).isEqualTo("Azul");
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    @DisplayName("Should delete vehicle (soft delete)")
    void shouldDeleteVehicle() {
        // Given
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        // When
        vehicleService.delete(vehicleId);

        // Then
        verify(vehicleRepository).findById(vehicleId);
        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existing vehicle")
    void shouldThrowExceptionWhenDeletingNonExistingVehicle() {
        // Given
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vehicleService.delete(vehicleId))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(vehicleRepository).findById(vehicleId);
        verify(vehicleRepository, never()).delete(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should find all vehicles without pagination")
    void shouldFindAllVehiclesWithoutPagination() {
        // Given
        List<Vehicle> vehicles = Arrays.asList(vehicle);
        when(vehicleRepository.findAllByDeletedAtIsNull()).thenReturn(vehicles);

        // When
        List<VehicleResponse> result = vehicleService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlaca()).isEqualTo("ABC1234");
        verify(vehicleRepository).findAllByDeletedAtIsNull();
    }
}

