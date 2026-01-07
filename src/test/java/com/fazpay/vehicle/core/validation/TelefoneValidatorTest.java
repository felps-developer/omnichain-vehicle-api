package com.fazpay.vehicle.core.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Telefone Validator Tests")
class TelefoneValidatorTest {

    private TelefoneValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TelefoneValidator();
    }

    @Test
    @DisplayName("Should validate mobile phone with formatting")
    void shouldValidateMobilePhoneWithFormatting() {
        assertThat(validator.isValid("(11) 98765-4321", null)).isTrue();
        assertThat(validator.isValid("(21) 99999-8888", null)).isTrue();
    }

    @Test
    @DisplayName("Should validate mobile phone without formatting")
    void shouldValidateMobilePhoneWithoutFormatting() {
        assertThat(validator.isValid("11987654321", null)).isTrue();
        assertThat(validator.isValid("21999998888", null)).isTrue();
    }

    @Test
    @DisplayName("Should validate landline phone with formatting")
    void shouldValidateLandlinePhoneWithFormatting() {
        assertThat(validator.isValid("(11) 3456-7890", null)).isTrue();
        assertThat(validator.isValid("(21) 2345-6789", null)).isTrue();
    }

    @Test
    @DisplayName("Should validate landline phone without formatting")
    void shouldValidateLandlinePhoneWithoutFormatting() {
        assertThat(validator.isValid("1134567890", null)).isTrue();
        assertThat(validator.isValid("2123456789", null)).isTrue();
    }

    @Test
    @DisplayName("Should reject phone with invalid DDD")
    void shouldRejectPhoneWithInvalidDdd() {
        assertThat(validator.isValid("(00) 98765-4321", null)).isFalse();
        assertThat(validator.isValid("(10) 98765-4321", null)).isFalse();
        assertThat(validator.isValid("0098765432", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject mobile phone without 9th digit")
    void shouldRejectMobilePhoneWithout9thDigit() {
        assertThat(validator.isValid("(11) 88765-4321", null)).isFalse();
        assertThat(validator.isValid("11887654321", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject phone with less than 10 digits")
    void shouldRejectPhoneWithLessThan10Digits() {
        assertThat(validator.isValid("119876543", null)).isFalse();
        assertThat(validator.isValid("(11) 9876-543", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject phone with more than 11 digits")
    void shouldRejectPhoneWithMoreThan11Digits() {
        assertThat(validator.isValid("119876543210", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject null phone")
    void shouldRejectNullPhone() {
        assertThat(validator.isValid(null, null)).isFalse();
    }

    @Test
    @DisplayName("Should reject blank phone")
    void shouldRejectBlankPhone() {
        assertThat(validator.isValid("", null)).isFalse();
        assertThat(validator.isValid("   ", null)).isFalse();
    }

    @Test
    @DisplayName("Should validate all valid DDDs")
    void shouldValidateAllValidDdds() {
        // Testing some valid DDDs
        assertThat(validator.isValid("(11) 98765-4321", null)).isTrue(); // SP
        assertThat(validator.isValid("(21) 98765-4321", null)).isTrue(); // RJ
        assertThat(validator.isValid("(31) 98765-4321", null)).isTrue(); // MG
        assertThat(validator.isValid("(85) 98765-4321", null)).isTrue(); // CE
    }
}

