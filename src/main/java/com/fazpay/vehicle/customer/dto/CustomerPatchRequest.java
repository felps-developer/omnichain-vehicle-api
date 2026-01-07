package com.fazpay.vehicle.customer.dto;

import com.fazpay.vehicle.core.validation.ValidCpf;
import com.fazpay.vehicle.core.validation.ValidTelefone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPatchRequest {
    
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @ValidCpf
    private String cpf;
    
    @Email(message = "Email inválido")
    private String email;
    
    @ValidTelefone
    private String telefone;
}

