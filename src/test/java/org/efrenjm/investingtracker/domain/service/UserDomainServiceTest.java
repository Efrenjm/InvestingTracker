package org.efrenjm.investingtracker.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidEmailException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidOldPasswordException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidPasswordException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidPhoneNumberException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.ReusedPasswordException;
import org.efrenjm.investingtracker.domain.ports.inbound.ValidationPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.PasswordEncoderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {

    @Mock private PasswordEncoderPort passwordEncoder;

    @Mock private ValidationPort validationService;

    @Mock private UserVerificationService userVerificationService;

    @InjectMocks private UserDomainService userDomainService;

    @Test
    void createUserValidEmailCreatesUser() {
        String username = "user@example.com";
        VerificationRequest mockRequest =
                VerificationRequest.builder()
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(username)
                        .build();

        when(validationService.isValidEmail(username)).thenReturn(true);
        when(userVerificationService.createRequest(CodeUsage.EMAIL_VERIFICATION, username))
                .thenReturn(mockRequest);

        User user = userDomainService.createUser(username);

        assertFalse(user.isActive());
        assertEquals(username, user.getUsername());
        assertTrue(user.getVerificationRequest().isPresent());
        assertEquals(
                CodeUsage.EMAIL_VERIFICATION, user.getVerificationRequest().get().getCodeUsage());
        verify(userVerificationService).createRequest(CodeUsage.EMAIL_VERIFICATION, username);
    }

    @Test
    void createUserValidPhoneCreatesUser() {
        String username = "+5215551234567";
        VerificationRequest mockRequest =
                VerificationRequest.builder()
                        .codeUsage(CodeUsage.PHONE_VERIFICATION)
                        .credential(username)
                        .build();

        when(validationService.isValidEmail(username)).thenReturn(false);
        when(userVerificationService.createRequest(CodeUsage.PHONE_VERIFICATION, username))
                .thenReturn(mockRequest);

        User user = userDomainService.createUser(username);

        assertFalse(user.isActive());
        assertEquals(username, user.getUsername());
        assertTrue(user.getVerificationRequest().isPresent());
        assertEquals(
                CodeUsage.PHONE_VERIFICATION, user.getVerificationRequest().get().getCodeUsage());
        verify(userVerificationService).createRequest(CodeUsage.PHONE_VERIFICATION, username);
    }

    @Test
    void createUserShouldSetCreatedAtAndUpdatedAt() {
        String username = "user@example.com";
        VerificationRequest mockRequest =
                VerificationRequest.builder()
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(username)
                        .build();

        when(validationService.isValidEmail(username)).thenReturn(true);
        when(userVerificationService.createRequest(CodeUsage.EMAIL_VERIFICATION, username))
                .thenReturn(mockRequest);

        User user = userDomainService.createUser(username);

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void resetUnverifiedPasswordValidPasswordUpdatesPassword() {
        User user = User.builder().build();
        String newPassword = "newPassword123";
        String encodedPassword = "encodedPassword";

        when(validationService.isValidPassword(newPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        User updatedUser = userDomainService.resetUnverifiedPassword(user, newPassword);

        assertEquals(encodedPassword, updatedUser.getPassword());
        assertNotNull(updatedUser.getUpdatedAt());
    }

    @Test
    void resetUnverifiedPasswordInvalidPasswordThrowsException() {
        User user = User.builder().build();
        String newPassword = "bad";

        when(validationService.isValidPassword(newPassword)).thenReturn(false);

        assertThrows(
                InvalidPasswordException.class,
                () -> {
                    userDomainService.resetUnverifiedPassword(user, newPassword);
                });
    }

    @Test
    void updateCredentialWhenEmailInvalidThrowsException() {
        User user = User.builder().build();
        String email = "invalid-email";

        when(validationService.isValidEmail(email)).thenReturn(false);

        assertThrows(
                InvalidEmailException.class,
                () ->
                        userDomainService.updateCredential(
                                user, CodeUsage.EMAIL_VERIFICATION, email));
    }

    @Test
    void updateCredentialWhenPhoneInvalidThrowsException() {
        User user = User.builder().build();
        String phone = "abc123";

        when(validationService.isValidPhone(phone)).thenReturn(false);

        assertThrows(
                InvalidPhoneNumberException.class,
                () ->
                        userDomainService.updateCredential(
                                user, CodeUsage.PHONE_VERIFICATION, phone));
    }

    @Test
    void updateCredentialWhenPasswordResetAndInvalidPasswordThrowsException() {
        User user = User.builder().password("oldEncoded").build();
        String password = "bad";

        when(passwordEncoder.encode(password)).thenReturn("encoded");
        when(validationService.isValidPassword(password)).thenReturn(false);

        assertThrows(
                InvalidPasswordException.class,
                () -> userDomainService.updateCredential(user, CodeUsage.PASSWORD_RESET, password));
    }

    @Test
    void updateCredentialWhenPasswordResetAndReusedPasswordThrowsException() {
        User user = User.builder().password("encoded-pass").build();
        String password = "NewPassword123!";

        when(passwordEncoder.encode(password)).thenReturn("encoded-pass");
        when(validationService.isValidPassword(password)).thenReturn(true);

        assertThrows(
                ReusedPasswordException.class,
                () -> userDomainService.updateCredential(user, CodeUsage.PASSWORD_RESET, password));
    }

    @Test
    void
            updateCredentialWhenPasswordResetAndValidShouldCreateVerificationRequestWithEncodedCredential() {
        User user = User.builder().password("old-pass").build();
        String password = "NewPassword123!";
        String encoded = "encoded-new-pass";
        VerificationRequest request =
                VerificationRequest.builder()
                        .codeUsage(CodeUsage.PASSWORD_RESET)
                        .credential(encoded)
                        .code("ABC123")
                        .build();

        when(passwordEncoder.encode(password)).thenReturn(encoded);
        when(validationService.isValidPassword(password)).thenReturn(true);
        when(userVerificationService.createRequest(CodeUsage.PASSWORD_RESET, encoded))
                .thenReturn(request);

        User updatedUser =
                userDomainService.updateCredential(user, CodeUsage.PASSWORD_RESET, password);

        assertTrue(updatedUser.getVerificationRequest().isPresent());
        assertEquals(encoded, updatedUser.getVerificationRequest().get().getCredential());
        verify(userVerificationService).createRequest(CodeUsage.PASSWORD_RESET, encoded);
    }

    @Test
    void updateCredentialWhenEmailValidShouldCreateVerificationRequest() {
        User user = User.builder().build();
        String email = "new@example.com";
        VerificationRequest request =
                VerificationRequest.builder()
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(email)
                        .code("ABC123")
                        .build();

        when(validationService.isValidEmail(email)).thenReturn(true);
        when(userVerificationService.createRequest(CodeUsage.EMAIL_VERIFICATION, email))
                .thenReturn(request);

        User updatedUser =
                userDomainService.updateCredential(user, CodeUsage.EMAIL_VERIFICATION, email);

        assertTrue(updatedUser.getVerificationRequest().isPresent());
        assertEquals(
                CodeUsage.EMAIL_VERIFICATION,
                updatedUser.getVerificationRequest().get().getCodeUsage());
        assertEquals(email, updatedUser.getVerificationRequest().get().getCredential());
    }

    @Test
    void updatePasswordUserHasNoPasswordSetsNewPassword() {
        User user = User.builder().build();
        String newPassword = "newPassword123";
        String encodedPassword = "encodedPassword";

        when(validationService.isValidPassword(newPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        User updatedUser = userDomainService.updatePassword(user, newPassword, null);

        assertEquals(encodedPassword, updatedUser.getPassword());
        assertNotNull(updatedUser.getUpdatedAt());
    }

    @Test
    void updatePasswordUserHasPasswordValidOldPasswordSetsNewPassword() {
        User user = User.builder().password("oldEncoded").build();
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        String encodedNewPassword = "encodedNewPassword";

        when(passwordEncoder.matches(oldPassword, "oldEncoded")).thenReturn(true);
        when(passwordEncoder.matches(newPassword, "oldEncoded")).thenReturn(false);
        when(validationService.isValidPassword(newPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        User updatedUser = userDomainService.updatePassword(user, newPassword, oldPassword);

        assertEquals(encodedNewPassword, updatedUser.getPassword());
    }

    @Test
    void updatePasswordUserHasPasswordInvalidOldPasswordThrowsException() {
        User user = User.builder().password("oldEncoded").build();
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword123";

        when(passwordEncoder.matches(oldPassword, "oldEncoded")).thenReturn(false);

        assertThrows(
                InvalidOldPasswordException.class,
                () -> {
                    userDomainService.updatePassword(user, newPassword, oldPassword);
                });
    }

    @Test
    void updatePasswordUserHasPasswordReusedPasswordThrowsException() {
        User user = User.builder().password("oldEncoded").build();
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";

        when(passwordEncoder.matches(oldPassword, "oldEncoded")).thenReturn(true);
        when(passwordEncoder.matches(newPassword, "oldEncoded")).thenReturn(true);

        assertThrows(
                ReusedPasswordException.class,
                () -> {
                    userDomainService.updatePassword(user, newPassword, oldPassword);
                });
    }

    @Test
    void updatePasswordUserHasPasswordNullOldPasswordThrowsException() {
        User user = User.builder().password("oldEncoded").build();

        assertThrows(
                InvalidOldPasswordException.class,
                () -> {
                    userDomainService.updatePassword(user, "NewPassword123!", null);
                });
    }

    @Test
    void updatePasswordInvalidNewPasswordThrowsException() {
        User user = User.builder().build();
        String newPassword = "short";

        when(validationService.isValidPassword(newPassword)).thenReturn(false);

        assertThrows(
                InvalidPasswordException.class,
                () -> {
                    userDomainService.updatePassword(user, newPassword, null);
                });
    }
}
