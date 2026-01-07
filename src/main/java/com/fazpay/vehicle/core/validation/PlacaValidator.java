package com.fazpay.vehicle.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlacaValidator implements ConstraintValidator<ValidPlaca, String> {

    @Override
    public void initialize(ValidPlaca constraintAnnotation) {
    }

    @Override
    public boolean isValid(String placa, ConstraintValidatorContext context) {
        if (placa == null || placa.isBlank()) {
            return false;
        }

        // Remove spaces and convert to uppercase
        placa = placa.replaceAll("\\s", "").toUpperCase();

        // Placa must have exactly 7 characters
        if (placa.length() != 7) {
            return false;
        }

        // Old format: ABC1234 (3 letters + 4 digits)
        boolean isOldFormat = placa.matches("[A-Z]{3}[0-9]{4}");

        // New Mercosul format: ABC1D23 (3 letters + 1 digit + 1 letter + 2 digits)
        boolean isNewFormat = placa.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}");

        return isOldFormat || isNewFormat;
    }
}

