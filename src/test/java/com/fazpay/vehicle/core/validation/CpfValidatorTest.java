package com.fazpay.vehicle.core.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CPF Validator Tests")
class CpfValidatorTest {

    private CpfValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CpfValidator();
    }

    @Test
    @DisplayName("Should validate valid CPF with dots and dash")
    void shouldValidateValidCpfWithFormatting() {
        assertThat(validator.isValid("123.456.789-09", null)).isTrue();
    }

    @Test
    @DisplayName("Should validate valid CPF without formatting")
    void shouldValidateValidCpfWithoutFormatting() {
        assertThat(validator.isValid("12345678909", null)).isTrue();
    }

    @Test
    @DisplayName("Should reject CPF with all same digits")
    void shouldRejectCpfWithAllSameDigits() {
        assertThat(validator.isValid("111.111.111-11", null)).isFalse();
        assertThat(validator.isValid("00000000000", null)).isFalse();
        assertThat(validator.isValid("99999999999", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject CPF with invalid check digits")
    void shouldRejectCpfWithInvalidCheckDigits() {
        assertThat(validator.isValid("123.456.789-00", null)).isFalse();
        assertThat(validator.isValid("12345678900", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject CPF with less than 11 digits")
    void shouldRejectCpfWithLessThan11Digits() {
        assertThat(validator.isValid("123456789", null)).isFalse();
        assertThat(validator.isValid("123.456.789", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject CPF with more than 11 digits")
    void shouldRejectCpfWithMoreThan11Digits() {
        assertThat(validator.isValid("123456789012", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject null CPF")
    void shouldRejectNullCpf() {
        assertThat(validator.isValid(null, null)).isFalse();
    }

    @Test
    @DisplayName("Should reject blank CPF")
    void shouldRejectBlankCpf() {
        assertThat(validator.isValid("", null)).isFalse();
        assertThat(validator.isValid("   ", null)).isFalse();
    }

    @Test
    @DisplayName("Should reject CPF with letters")
    void shouldRejectCpfWithLetters() {
        assertThat(validator.isValid("abc.def.ghi-jk", null)).isFalse();
    }

    @Test
    @DisplayName("Should validate known valid CPFs")
    void shouldValidateKnownValidCpfs() {
        // Known valid CPFs
        assertThat(validator.isValid("11144477735", null)).isTrue();
        assertThat(validator.isValid("52998224725", null)).isTrue();
    }
}

