package org.efrenjm.investingtracker.domain.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @Mock
    private org.efrenjm.investingtracker.domain.ports.outbound.utils.ValidationPort
            validationOperations;

    @InjectMocks private ValidationService validationService;

    @Test
    void isValidEmailShouldDelegateToOutboundPort() {
        when(validationOperations.isValidEmail("user@example.com")).thenReturn(true);

        boolean result = validationService.isValidEmail("user@example.com");

        assertTrue(result);
        verify(validationOperations).isValidEmail("user@example.com");
    }

    @Test
    void isValidPhoneShouldDelegateToOutboundPort() {
        when(validationOperations.isValidPhone("+5215551234567")).thenReturn(true);

        boolean result = validationService.isValidPhone("+5215551234567");

        assertTrue(result);
        verify(validationOperations).isValidPhone("+5215551234567");
    }

    @Test
    void isValidPasswordWhenPasswordMeetsPolicyShouldReturnTrue() {
        assertTrue(validationService.isValidPassword("Valid123!"));
    }

    @Test
    void isValidPasswordWhenPasswordViolatesPolicyShouldReturnFalse() {
        assertFalse(validationService.isValidPassword("short1!"));
        assertFalse(validationService.isValidPassword("NOLOWERCASE123!"));
        assertFalse(validationService.isValidPassword("nouppercase123!"));
        assertFalse(validationService.isValidPassword("NoSpecial123"));
        assertFalse(validationService.isValidPassword("Has Space1!"));
    }
}
