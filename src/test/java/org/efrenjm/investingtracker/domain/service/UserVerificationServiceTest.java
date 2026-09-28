package org.efrenjm.investingtracker.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeRefreshDisabledException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidCodeException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.NoVerificationInProcessException;
import org.junit.jupiter.api.Test;

class UserVerificationServiceTest {

    private final UserVerificationService userVerificationService = new UserVerificationService();

    @Test
    void createRequestShouldGenerateSixCharAlphanumericCodeAndSetMetadata() {
        long before = System.currentTimeMillis();
        VerificationRequest request =
                userVerificationService.createRequest(
                        CodeUsage.EMAIL_VERIFICATION, "user@example.com");
        long after = System.currentTimeMillis();

        assertNotNull(request.getCode());
        assertEquals(6, request.getCode().length());
        assertTrue(request.getCode().matches("^[A-Z0-9]{6}$"));
        assertEquals(CodeUsage.EMAIL_VERIFICATION, request.getCodeUsage());
        assertEquals("user@example.com", request.getCredential());
        assertNotNull(request.getExpiration());
        assertNotNull(request.getRefreshPause());
        assertTrue(request.getExpiration().getTime() >= before + 599_000L);
        assertTrue(request.getExpiration().getTime() <= after + 601_000L);
        assertTrue(request.getRefreshPause().getTime() >= before + 59_000L);
        assertTrue(request.getRefreshPause().getTime() <= after + 61_000L);
    }

    @Test
    void refreshRequestWhenRequestNotRefreshableShouldThrowCodeRefreshDisabledException() {
        VerificationRequest request =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential("user@example.com")
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                        .build();

        assertThrows(
                CodeRefreshDisabledException.class,
                () -> userVerificationService.refreshRequest(request));
    }

    @Test
    void refreshRequestWhenRequestRefreshableShouldCreateNewRequestKeepingUsageAndCredential() {
        VerificationRequest request =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.PHONE_VERIFICATION)
                        .credential("+5215551234567")
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() - 60_000))
                        .build();

        VerificationRequest refreshed = userVerificationService.refreshRequest(request);

        assertEquals(CodeUsage.PHONE_VERIFICATION, refreshed.getCodeUsage());
        assertEquals("+5215551234567", refreshed.getCredential());
        assertNotEquals("ABC123", refreshed.getCode());
    }

    @Test
    void validateRequestWhenCodeDoesNotMatchShouldThrowInvalidCodeException() {
        VerificationRequest request =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential("user@example.com")
                        .expiration(new Date(System.currentTimeMillis() + 10_000))
                        .refreshPause(new Date(System.currentTimeMillis() - 1_000))
                        .build();

        assertThrows(
                InvalidCodeException.class,
                () -> userVerificationService.validateRequest(request, "ZZZ999"));
    }

    @Test
    void validateRequestWhenCodeMatchesButExpiredShouldThrowCodeExpiredException() {
        VerificationRequest request =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential("user@example.com")
                        .expiration(new Date(System.currentTimeMillis() - 1_000))
                        .refreshPause(new Date(System.currentTimeMillis() - 1_000))
                        .build();

        assertThrows(
                CodeExpiredException.class,
                () -> userVerificationService.validateRequest(request, "ABC123"));
    }

    @Test
    void validateRequestWhenCodeMatchesAndNotExpiredShouldNotThrow() {
        VerificationRequest request =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential("user@example.com")
                        .expiration(new Date(System.currentTimeMillis() + 10_000))
                        .refreshPause(new Date(System.currentTimeMillis() - 1_000))
                        .build();

        assertDoesNotThrow(() -> userVerificationService.validateRequest(request, "ABC123"));
    }

    @Test
    void completeRequestWhenNoVerificationInProcessShouldThrowNoVerificationInProcessException() {
        User user = User.builder().build();

        assertThrows(
                NoVerificationInProcessException.class,
                () -> userVerificationService.completeRequest(user));
    }

    @Test
    void completeRequestWhenEmailVerificationShouldSetEmailAndClearRequest() {
        User user =
                User.builder()
                        .verificationRequest(
                                VerificationRequest.builder()
                                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                                        .credential("user@example.com")
                                        .code("ABC123")
                                        .expiration(new Date(System.currentTimeMillis() + 10_000))
                                        .refreshPause(new Date(System.currentTimeMillis() - 1_000))
                                        .build())
                        .build();

        userVerificationService.completeRequest(user);

        assertEquals("user@example.com", user.getEmail());
        assertTrue(user.getVerificationRequest().isEmpty());
    }

    @Test
    void completeRequestWhenPhoneVerificationShouldSetPhoneAndClearRequest() {
        User user =
                User.builder()
                        .verificationRequest(
                                VerificationRequest.builder()
                                        .codeUsage(CodeUsage.PHONE_VERIFICATION)
                                        .credential("+5215551234567")
                                        .code("ABC123")
                                        .expiration(new Date(System.currentTimeMillis() + 10_000))
                                        .refreshPause(new Date(System.currentTimeMillis() - 1_000))
                                        .build())
                        .build();

        userVerificationService.completeRequest(user);

        assertEquals("+5215551234567", user.getPhoneNumber());
        assertTrue(user.getVerificationRequest().isEmpty());
    }

    @Test
    void completeRequestWhenPasswordResetShouldSetPasswordAndClearRequest() {
        User user =
                User.builder()
                        .verificationRequest(
                                VerificationRequest.builder()
                                        .codeUsage(CodeUsage.PASSWORD_RESET)
                                        .credential("encoded-password")
                                        .code("ABC123")
                                        .expiration(new Date(System.currentTimeMillis() + 10_000))
                                        .refreshPause(new Date(System.currentTimeMillis() - 1_000))
                                        .build())
                        .build();

        userVerificationService.completeRequest(user);

        assertEquals("encoded-password", user.getPassword());
        assertTrue(user.getVerificationRequest().isEmpty());
    }
}
