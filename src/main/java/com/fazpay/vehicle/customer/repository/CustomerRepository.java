package com.fazpay.vehicle.customer.repository;

import com.fazpay.vehicle.customer.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    
    Optional<Customer> findByCpf(String cpf);
    
    Optional<Customer> findByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT c FROM Customer c WHERE " +
           "(:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:dataCriacao IS NULL OR DATE(c.createdAt) = DATE(:dataCriacao))")
    Page<Customer> findWithFilters(@Param("nome") String nome, 
                                   @Param("dataCriacao") LocalDateTime dataCriacao,
                                   Pageable pageable);
    
    List<Customer> findAllByDeletedAtIsNull();
}

