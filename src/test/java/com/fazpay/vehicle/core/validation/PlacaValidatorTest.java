package com.fazpay.vehicle.core.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Placa Validator Tests")
class PlacaValidatorTest {

    private PlacaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PlacaValidator();
    }

    @Test
    @DisplayName("Should validate old format plate")
    void shouldValidateOldFormatPlate() {
        assertThat(validator.isValid("ABC1234", null)).isTrue();
        assertThat(validator.isValid("XYZ9876", null)).isTrue();
    }

    @Test
    @DisplayName("Should validate new Mercosul format plate")
    void shouldValidateNewMercosulFormatPlate() {
        assertThat(validator.isValid("ABC1D23", null)).isTrue();
        assertThat(validator.isValid("XYZ9A87", null)).isTrue();
    }

    @Test
    @DisplayName("Should validate lowercase plate and convert to uppercase")
    void shouldValidateLowercasePlate() {
        assertThat(validator.isValid("abc1234", null)).isTrue();
        assertThat(validator.isValid("abc1d23", null)).isTrue();
    }

    @Test
    @DisplayName("Should validate mixed case plate")
    void shouldValidateMixedCasePlate() {
        assertThat(validator.isValid("AbC1234", null)).isTrue();
        assertThat(validator.isValid("aBc1D23", null)).isTrue();
    }

    @Test
    @DisplayName("Should reject plate with less than 7 characters")
    void shouldRejectPlateWithLessThan7Characters() {
        assertThat(validator.isValid("ABC123", null)).isFalse();
        assertThat(validator.isValid("AB1234", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject plate with more than 7 characters")
    void shouldRejectPlateWithMoreThan7Characters() {
        assertThat(validator.isValid("ABC12345", null)).isFalse();
        assertThat(validator.isValid("ABCD1234", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject plate with invalid format")
    void shouldRejectPlateWithInvalidFormat() {
        assertThat(validator.isValid("1234ABC", null)).isFalse();
        assertThat(validator.isValid("AB12345", null)).isFalse();
        assertThat(validator.isValid("ABCDEFG", null)).isFalse();
        assertThat(validator.isValid("1234567", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject null plate")
    void shouldRejectNullPlate() {
        assertThat(validator.isValid(null, null)).isFalse();
    }

    @Test
    @DisplayName("Should reject blank plate")
    void shouldRejectBlankPlate() {
        assertThat(validator.isValid("", null)).isFalse();
        assertThat(validator.isValid("   ", null)).isFalse();
    }

    @Test
    @DisplayName("Should validate plate with spaces and remove them")
    void shouldValidatePlateWithSpaces() {
        assertThat(validator.isValid("ABC 1234", null)).isTrue();
        assertThat(validator.isValid("ABC 1D23", null)).isTrue();
    }

    @Test
    @DisplayName("Should reject plate with special characters")
    void shouldRejectPlateWithSpecialCharacters() {
        assertThat(validator.isValid("ABC-1234", null)).isFalse();
        assertThat(validator.isValid("ABC@1234", null)).isFalse();
    }
}

