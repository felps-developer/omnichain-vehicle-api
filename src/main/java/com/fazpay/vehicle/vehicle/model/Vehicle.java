package com.fazpay.vehicle.vehicle.model;

import com.fazpay.vehicle.customer.model.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "veiculos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE veiculos SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private UUID id;
    
    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "[A-Z]{3}[0-9][A-Z0-9][0-9]{2}", 
             message = "Invalid license plate format (expected: ABC1234 or ABC1D23)")
    @Column(nullable = false, unique = true, length = 7)
    private String placa;
    
    @NotBlank(message = "Brand is required")
    @Column(nullable = false, length = 50)
    private String marca;
    
    @NotBlank(message = "Model is required")
    @Column(nullable = false, length = 50)
    private String modelo;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be greater than or equal to 1900")
    @Max(value = 2100, message = "Year must be less than or equal to 2100")
    @Column(nullable = false)
    private Integer ano;
    
    @NotBlank(message = "Color is required")
    @Column(nullable = false, length = 30)
    private String cor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Customer customer;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}

