package org.efrenjm.investingtracker.domain.service;

import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidEmailException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidOldPasswordException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidPhoneNumberException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidPasswordException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.ReusedPasswordException;
import org.efrenjm.investingtracker.domain.ports.inbound.ValidationPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.PasswordEncoderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
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
	void createUser_ValidPhone_CreatesUser() {
		String username = "+5215551234567";
		VerificationRequest mockRequest = VerificationRequest.builder()
				.codeUsage(CodeUsage.PHONE_VERIFICATION)
				.credential(username)
				.build();

		when(validationService.isValidEmail(username)).thenReturn(false);
		when(userVerificationService.createRequest(CodeUsage.PHONE_VERIFICATION, username)).thenReturn(mockRequest);

		User user = userDomainService.createUser(username);

		assertFalse(user.isActive());
		assertEquals(username, user.getUsername());
		assertTrue(user.getVerificationRequest().isPresent());
		assertEquals(CodeUsage.PHONE_VERIFICATION, user.getVerificationRequest().get().getCodeUsage());
		verify(userVerificationService).createRequest(CodeUsage.PHONE_VERIFICATION, username);
	}

	@Test
	void createUser_ShouldSetCreatedAtAndUpdatedAt() {
		String username = "user@example.com";
		VerificationRequest mockRequest = VerificationRequest.builder()
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential(username)
				.build();

		when(validationService.isValidEmail(username)).thenReturn(true);
		when(userVerificationService.createRequest(CodeUsage.EMAIL_VERIFICATION, username)).thenReturn(mockRequest);

		User user = userDomainService.createUser(username);

		assertNotNull(user.getCreatedAt());
		assertNotNull(user.getUpdatedAt());
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
	void updateCredential_WhenEmailInvalid_ThrowsException() {
		User user = User.builder().build();
		String email = "invalid-email";

		when(validationService.isValidEmail(email)).thenReturn(false);

		assertThrows(InvalidEmailException.class,
				() -> userDomainService.updateCredential(user, CodeUsage.EMAIL_VERIFICATION, email));
	}

	@Test
	void updateCredential_WhenPhoneInvalid_ThrowsException() {
		User user = User.builder().build();
		String phone = "abc123";

		when(validationService.isValidPhone(phone)).thenReturn(false);

		assertThrows(InvalidPhoneNumberException.class,
				() -> userDomainService.updateCredential(user, CodeUsage.PHONE_VERIFICATION, phone));
	}

	@Test
	void updateCredential_WhenPasswordResetAndInvalidPassword_ThrowsException() {
		User user = User.builder().password("oldEncoded").build();
		String password = "bad";

		when(passwordEncoder.encode(password)).thenReturn("encoded");
		when(validationService.isValidPassword(password)).thenReturn(false);

		assertThrows(InvalidPasswordException.class,
				() -> userDomainService.updateCredential(user, CodeUsage.PASSWORD_RESET, password));
	}

	@Test
	void updateCredential_WhenPasswordResetAndReusedPassword_ThrowsException() {
		User user = User.builder().password("encoded-pass").build();
		String password = "NewPassword123!";

		when(passwordEncoder.encode(password)).thenReturn("encoded-pass");
		when(validationService.isValidPassword(password)).thenReturn(true);

		assertThrows(ReusedPasswordException.class,
				() -> userDomainService.updateCredential(user, CodeUsage.PASSWORD_RESET, password));
	}

	@Test
	void updateCredential_WhenPasswordResetAndValid_ShouldCreateVerificationRequestWithEncodedCredential() {
		User user = User.builder().password("old-pass").build();
		String password = "NewPassword123!";
		String encoded = "encoded-new-pass";
		VerificationRequest request = VerificationRequest.builder()
				.codeUsage(CodeUsage.PASSWORD_RESET)
				.credential(encoded)
				.code("ABC123")
				.build();

		when(passwordEncoder.encode(password)).thenReturn(encoded);
		when(validationService.isValidPassword(password)).thenReturn(true);
		when(userVerificationService.createRequest(CodeUsage.PASSWORD_RESET, encoded)).thenReturn(request);

		User updatedUser = userDomainService.updateCredential(user, CodeUsage.PASSWORD_RESET, password);

		assertTrue(updatedUser.getVerificationRequest().isPresent());
		assertEquals(encoded, updatedUser.getVerificationRequest().get().getCredential());
		verify(userVerificationService).createRequest(CodeUsage.PASSWORD_RESET, encoded);
	}

	@Test
	void updateCredential_WhenEmailValid_ShouldCreateVerificationRequest() {
		User user = User.builder().build();
		String email = "new@example.com";
		VerificationRequest request = VerificationRequest.builder()
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential(email)
				.code("ABC123")
				.build();

		when(validationService.isValidEmail(email)).thenReturn(true);
		when(userVerificationService.createRequest(CodeUsage.EMAIL_VERIFICATION, email)).thenReturn(request);

		User updatedUser = userDomainService.updateCredential(user, CodeUsage.EMAIL_VERIFICATION, email);

		assertTrue(updatedUser.getVerificationRequest().isPresent());
		assertEquals(CodeUsage.EMAIL_VERIFICATION, updatedUser.getVerificationRequest().get().getCodeUsage());
		assertEquals(email, updatedUser.getVerificationRequest().get().getCredential());
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

	@Test
	void updatePassword_UserHasPassword_NullOldPassword_ThrowsException() {
		User user = User.builder().password("oldEncoded").build();

		assertThrows(InvalidOldPasswordException.class, () -> {
			userDomainService.updatePassword(user, "NewPassword123!", null);
		});
	}

	@Test
	void updatePassword_InvalidNewPassword_ThrowsException() {
		User user = User.builder().build();
		String newPassword = "short";

		when(validationService.isValidPassword(newPassword)).thenReturn(false);

		assertThrows(InvalidPasswordException.class, () -> {
			userDomainService.updatePassword(user, newPassword, null);
		});
	}
}
