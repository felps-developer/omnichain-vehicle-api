package com.fazpay.vehicle.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public void initialize(ValidTelefone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        if (telefone == null || telefone.isBlank()) {
            return false;
        }

        // Remove non-digits
        String digits = telefone.replaceAll("\\D", "");

        // Telefone must have 10 or 11 digits (with or without 9th digit for mobile)
        // Format: (XX) XXXX-XXXX or (XX) 9XXXX-XXXX
        if (digits.length() != 10 && digits.length() != 11) {
            return false;
        }

        // First two digits (DDD) must be between 11 and 99
        int ddd = Integer.parseInt(digits.substring(0, 2));
        if (ddd < 11 || ddd > 99) {
            return false;
        }

        // For 11 digits (mobile), 3rd digit must be 9
        if (digits.length() == 11) {
            if (digits.charAt(2) != '9') {
                return false;
            }
        }

        return true;
    }
}

