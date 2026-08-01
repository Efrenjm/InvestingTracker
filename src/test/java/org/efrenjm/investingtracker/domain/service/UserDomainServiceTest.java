package org.efrenjm.investingtracker.domain.service;

import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidOldPasswordException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidPasswordException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.ReusedPasswordException;
import org.efrenjm.investingtracker.domain.ports.inbound.ValidationPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.PasswordEncoderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {

	@Mock
	private PasswordEncoderPort passwordEncoder;

	@Mock
	private ValidationPort validationService;

	@Mock
	private UserVerificationService userVerificationService;

	@InjectMocks
	private UserDomainService userDomainService;

	@Test
	void createUser_ValidEmail_CreatesUser() {
		String username = "user@example.com";
		VerificationRequest mockRequest = VerificationRequest.builder()
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential(username)
				.build();

		when(validationService.isValidEmail(username)).thenReturn(true);
		when(userVerificationService.createRequest(CodeUsage.EMAIL_VERIFICATION, username)).thenReturn(mockRequest);

		User user = userDomainService.createUser(username);

		assertFalse(user.isActive());
		assertEquals(username, user.getUsername());
		assertTrue(user.getVerificationRequest().isPresent());
		assertEquals(CodeUsage.EMAIL_VERIFICATION, user.getVerificationRequest().get().getCodeUsage());
		verify(userVerificationService).createRequest(CodeUsage.EMAIL_VERIFICATION, username);
	}

	@Test
	void resetUnverifiedPassword_ValidPassword_UpdatesPassword() {
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
	void resetUnverifiedPassword_InvalidPassword_ThrowsException() {
		User user = User.builder().build();
		String newPassword = "bad";

		when(validationService.isValidPassword(newPassword)).thenReturn(false);

		assertThrows(InvalidPasswordException.class, () -> {
			userDomainService.resetUnverifiedPassword(user, newPassword);
		});
	}

	@Test
	void updatePassword_UserHasNoPassword_SetsNewPassword() {
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
	void updatePassword_UserHasPassword_ValidOldPassword_SetsNewPassword() {
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
	void updatePassword_UserHasPassword_InvalidOldPassword_ThrowsException() {
		User user = User.builder().password("oldEncoded").build();
		String oldPassword = "wrongOldPassword";
		String newPassword = "newPassword123";

		when(passwordEncoder.matches(oldPassword, "oldEncoded")).thenReturn(false);

		assertThrows(InvalidOldPasswordException.class, () -> {
			userDomainService.updatePassword(user, newPassword, oldPassword);
		});
	}

	@Test
	void updatePassword_UserHasPassword_ReusedPassword_ThrowsException() {
		User user = User.builder().password("oldEncoded").build();
		String oldPassword = "oldPassword123";
		String newPassword = "newPassword123";

		when(passwordEncoder.matches(oldPassword, "oldEncoded")).thenReturn(true);
		when(passwordEncoder.matches(newPassword, "oldEncoded")).thenReturn(true);

		assertThrows(ReusedPasswordException.class, () -> {
			userDomainService.updatePassword(user, newPassword, oldPassword);
		});
	}
}
