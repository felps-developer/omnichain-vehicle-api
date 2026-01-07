package com.fazpay.vehicle.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PlacaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPlaca {
    String message() default "Placa inválida. Use o formato ABC1234 (antigo) ou ABC1D23 (Mercosul)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

