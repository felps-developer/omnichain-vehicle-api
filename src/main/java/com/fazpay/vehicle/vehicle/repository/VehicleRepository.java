package com.fazpay.vehicle.vehicle.repository;

import com.fazpay.vehicle.vehicle.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {
    
    Optional<Vehicle> findByPlaca(String placa);
    
    List<Vehicle> findByCustomerId(UUID customerId);
    
    boolean existsByPlaca(String placa);
    
    @Query("SELECT v FROM Vehicle v WHERE " +
           "(:marca IS NULL OR LOWER(v.marca) LIKE LOWER(CONCAT('%', :marca, '%'))) AND " +
           "(:modelo IS NULL OR LOWER(v.modelo) LIKE LOWER(CONCAT('%', :modelo, '%'))) AND " +
           "(:cor IS NULL OR LOWER(v.cor) LIKE LOWER(CONCAT('%', :cor, '%')))")
    Page<Vehicle> findWithFilters(@Param("marca") String marca,
                                  @Param("modelo") String modelo,
                                  @Param("cor") String cor,
                                  Pageable pageable);
    
    List<Vehicle> findAllByDeletedAtIsNull();
}

