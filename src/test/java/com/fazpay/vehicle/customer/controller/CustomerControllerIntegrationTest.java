package com.fazpay.vehicle.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazpay.vehicle.core.security.JwtTokenProvider;
import com.fazpay.vehicle.customer.dto.CustomerPatchRequest;
import com.fazpay.vehicle.customer.dto.CustomerRequest;
import com.fazpay.vehicle.customer.model.Customer;
import com.fazpay.vehicle.customer.repository.CustomerRepository;
import com.fazpay.vehicle.user.model.User;
import com.fazpay.vehicle.user.repository.UserRepository;
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Customer Controller Integration Tests")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String jwtToken;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
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
                .cpf("11144477735")
                .email("joao@example.com")
                .telefone("(11) 98765-4321")
                .build();
        customerRepository.save(testCustomer);
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() throws Exception {
        CustomerRequest request = CustomerRequest.builder()
                .nome("Maria Santos")
                .cpf("52998224725")
                .email("maria@example.com")
                .telefone("(21) 99999-8888")
                .build();

        mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.email").value("maria@example.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Should return 400 when creating customer with invalid CPF")
    void shouldReturn400WhenCreatingCustomerWithInvalidCpf() throws Exception {
        CustomerRequest request = CustomerRequest.builder()
                .nome("Maria Santos")
                .cpf("11111111111")
                .email("maria@example.com")
                .telefone("(21) 99999-8888")
                .build();

        mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should list customers with pagination")
    void shouldListCustomersWithPagination() throws Exception {
        mockMvc.perform(get("/api/v1/clientes")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].nome").exists());
    }

    @Test
    @DisplayName("Should get customer by ID")
    void shouldGetCustomerById() throws Exception {
        mockMvc.perform(get("/api/v1/clientes/{id}", testCustomer.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCustomer.getId().toString()))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("11144477735"));
    }

    @Test
    @DisplayName("Should delete customer")
    void shouldDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/v1/clientes/{id}", testCustomer.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 403 when no JWT token provided")
    void shouldReturn403WhenNoJwtTokenProvided() throws Exception {
        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isForbidden());
    }
}

