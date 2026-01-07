package com.fazpay.vehicle.vehicle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazpay.vehicle.customer.model.Customer;
import com.fazpay.vehicle.customer.repository.CustomerRepository;
import com.fazpay.vehicle.core.security.JwtTokenProvider;
import com.fazpay.vehicle.user.model.User;
import com.fazpay.vehicle.user.repository.UserRepository;
import com.fazpay.vehicle.vehicle.dto.VehiclePatchRequest;
import com.fazpay.vehicle.vehicle.dto.VehicleRequest;
import com.fazpay.vehicle.vehicle.model.Vehicle;
import com.fazpay.vehicle.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Vehicle Controller Integration Tests")
class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String jwtToken;
    private Vehicle testVehicle;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Clean up
        vehicleRepository.deleteAll();
        customerRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        User testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password(passwordEncoder.encode("senha123"))
                .build();
        userRepository.save(testUser);

        // Generate JWT token
        jwtToken = tokenProvider.generateToken("testuser");

        // Create test customer
        testCustomer = Customer.builder()
                .nome("João Silva")
                .cpf("12345678909")
                .email("joao@example.com")
                .telefone("(11) 98765-4321")
                .build();
        customerRepository.save(testCustomer);

        // Create test vehicle
        testVehicle = Vehicle.builder()
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2023)
                .cor("Prata")
                .customer(testCustomer)
                .build();
        vehicleRepository.save(testVehicle);
    }

    @Test
    @DisplayName("Should create vehicle successfully")
    void shouldCreateVehicleSuccessfully() throws Exception {
        VehicleRequest request = VehicleRequest.builder()
                .placa("XYZ5678")
                .marca("Honda")
                .modelo("Civic")
                .ano(2022)
                .cor("Preto")
                .clienteId(testCustomer.getId())
                .build();

        mockMvc.perform(post("/api/v1/veiculos")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("XYZ5678"))
                .andExpect(jsonPath("$.marca").value("Honda"))
                .andExpect(jsonPath("$.modelo").value("Civic"))
                .andExpect(jsonPath("$.ano").value(2022))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating vehicle with invalid placa")
    void shouldReturn400WhenCreatingVehicleWithInvalidPlaca() throws Exception {
        VehicleRequest request = VehicleRequest.builder()
                .placa("INVALID")  // Invalid plate format
                .marca("Honda")
                .modelo("Civic")
                .ano(2022)
                .cor("Preto")
                .clienteId(testCustomer.getId())
                .build();

        mockMvc.perform(post("/api/v1/veiculos")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when creating vehicle with invalid year")
    void shouldReturn400WhenCreatingVehicleWithInvalidYear() throws Exception {
        VehicleRequest request = VehicleRequest.builder()
                .placa("XYZ5678")
                .marca("Honda")
                .modelo("Civic")
                .ano(1800)  // Invalid year
                .cor("Preto")
                .clienteId(testCustomer.getId())
                .build();

        mockMvc.perform(post("/api/v1/veiculos")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should list vehicles with pagination")
    void shouldListVehiclesWithPagination() throws Exception {
        mockMvc.perform(get("/api/v1/veiculos")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].placa").exists());
    }

    @Test
    @DisplayName("Should get vehicle by ID")
    void shouldGetVehicleById() throws Exception {
        mockMvc.perform(get("/api/v1/veiculos/{id}", testVehicle.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testVehicle.getId().toString()))
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    @DisplayName("Should get vehicle by placa")
    void shouldGetVehicleByPlaca() throws Exception {
        mockMvc.perform(get("/api/v1/veiculos/placa/{placa}", "ABC1234")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    @DisplayName("Should return 404 when vehicle not found")
    void shouldReturn404WhenVehicleNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/veiculos/{id}", "00000000-0000-0000-0000-000000000000")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should partially update vehicle")
    void shouldPartiallyUpdateVehicle() throws Exception {
        VehiclePatchRequest patchRequest = VehiclePatchRequest.builder()
                .cor("Azul")
                .build();

        mockMvc.perform(patch("/api/v1/veiculos/{id}", testVehicle.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cor").value("Azul"))
                .andExpect(jsonPath("$.placa").value("ABC1234")); // Placa unchanged
    }

    @Test
    @DisplayName("Should delete vehicle (soft delete)")
    void shouldDeleteVehicle() throws Exception {
        mockMvc.perform(delete("/api/v1/veiculos/{id}", testVehicle.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        // Verify vehicle is soft deleted
        mockMvc.perform(get("/api/v1/veiculos/{id}", testVehicle.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter vehicles by marca")
    void shouldFilterVehiclesByMarca() throws Exception {
        mockMvc.perform(get("/api/v1/veiculos")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("marca", "Toyota")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].marca").value(containsString("Toyota")));
    }

    @Test
    @DisplayName("Should return 401 when no JWT token provided")
    void shouldReturn401WhenNoJwtTokenProvided() throws Exception {
        mockMvc.perform(get("/api/v1/veiculos"))
                .andExpect(status().isUnauthorized());
    }
}

