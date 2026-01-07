package com.fazpay.vehicle.customer.service;

import com.fazpay.vehicle.core.exception.BusinessException;
import com.fazpay.vehicle.core.exception.ResourceNotFoundException;
import com.fazpay.vehicle.customer.dto.CustomerPatchRequest;
import com.fazpay.vehicle.customer.dto.CustomerRequest;
import com.fazpay.vehicle.customer.dto.CustomerResponse;
import com.fazpay.vehicle.customer.model.Customer;
import com.fazpay.vehicle.customer.repository.CustomerRepository;
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
@DisplayName("Customer Service Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerRequest customerRequest;
    private CustomerPatchRequest customerPatchRequest;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        
        customer = Customer.builder()
                .id(customerId)
                .nome("João Silva")
                .cpf("12345678909")
                .email("joao@example.com")
                .telefone("(11) 98765-4321")
                .build();

        customerRequest = CustomerRequest.builder()
                .nome("João Silva")
                .cpf("12345678909")
                .email("joao@example.com")
                .telefone("(11) 98765-4321")
                .build();

        customerPatchRequest = CustomerPatchRequest.builder()
                .nome("João Silva Atualizado")
                .build();
    }

    @Test
    @DisplayName("Should find all customers with pagination")
    void shouldFindAllCustomersWithPagination() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        List<Customer> customers = Arrays.asList(customer);
        Page<Customer> customerPage = new PageImpl<>(customers, pageable, 1);
        
        when(customerRepository.findWithFilters(null, null, pageable)).thenReturn(customerPage);

        // When
        Page<CustomerResponse> result = customerService.findWithFilters(null, null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNome()).isEqualTo("João Silva");
        verify(customerRepository).findWithFilters(null, null, pageable);
    }

    @Test
    @DisplayName("Should find customer by ID")
    void shouldFindCustomerById() {
        // Given
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        CustomerResponse result = customerService.findById(customerId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(customerId);
        assertThat(result.getNome()).isEqualTo("João Silva");
        verify(customerRepository).findById(customerId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer not found by ID")
    void shouldThrowExceptionWhenCustomerNotFoundById() {
        // Given
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customerService.findById(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer")
                .hasMessageContaining(customerId.toString());
        
        verify(customerRepository).findById(customerId);
    }

    @Test
    @DisplayName("Should create new customer")
    void shouldCreateNewCustomer() {
        // Given
        when(customerRepository.existsByCpf(customerRequest.getCpf())).thenReturn(false);
        when(customerRepository.existsByEmail(customerRequest.getEmail())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        CustomerResponse result = customerService.create(customerRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("João Silva");
        assertThat(result.getCpf()).isEqualTo("12345678909");
        verify(customerRepository).existsByCpf(customerRequest.getCpf());
        verify(customerRepository).existsByEmail(customerRequest.getEmail());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when CPF already exists")
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        // Given
        when(customerRepository.existsByCpf(customerRequest.getCpf())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> customerService.create(customerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CPF");
        
        verify(customerRepository).existsByCpf(customerRequest.getCpf());
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(customerRepository.existsByCpf(customerRequest.getCpf())).thenReturn(false);
        when(customerRepository.existsByEmail(customerRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> customerService.create(customerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("email");
        
        verify(customerRepository).existsByCpf(customerRequest.getCpf());
        verify(customerRepository).existsByEmail(customerRequest.getEmail());
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should partially update customer")
    void shouldPartiallyUpdateCustomer() {
        // Given
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        CustomerResponse result = customerService.partialUpdate(customerId, customerPatchRequest);

        // Then
        assertThat(result).isNotNull();
        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should update only nome when provided")
    void shouldUpdateOnlyNomeWhenProvided() {
        // Given
        CustomerPatchRequest patchRequest = CustomerPatchRequest.builder()
                .nome("Novo Nome")
                .build();
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        customerService.partialUpdate(customerId, patchRequest);

        // Then
        assertThat(customer.getNome()).isEqualTo("Novo Nome");
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Should delete customer (soft delete)")
    void shouldDeleteCustomer() {
        // Given
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        customerService.delete(customerId);

        // Then
        verify(customerRepository).findById(customerId);
        verify(customerRepository).delete(customer);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existing customer")
    void shouldThrowExceptionWhenDeletingNonExistingCustomer() {
        // Given
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customerService.delete(customerId))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    @DisplayName("Should find all customers without pagination")
    void shouldFindAllCustomersWithoutPagination() {
        // Given
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findAllByDeletedAtIsNull()).thenReturn(customers);

        // When
        List<CustomerResponse> result = customerService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("João Silva");
        verify(customerRepository).findAllByDeletedAtIsNull();
    }
}

