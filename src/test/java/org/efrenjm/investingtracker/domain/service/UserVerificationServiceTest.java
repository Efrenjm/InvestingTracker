package org.efrenjm.investingtracker.domain.service;

import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeRefreshDisabledException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidCodeException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.NoVerificationInProcessException;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserVerificationServiceTest {

	private final UserVerificationService userVerificationService = new UserVerificationService();

	@Test
	void createRequest_ShouldGenerateSixCharAlphanumericCodeAndSetMetadata() {
		long before = System.currentTimeMillis();
		VerificationRequest request = userVerificationService.createRequest(CodeUsage.EMAIL_VERIFICATION, "user@example.com");
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
	void refreshRequest_WhenRequestNotRefreshable_ShouldThrowCodeRefreshDisabledException() {
		VerificationRequest request = VerificationRequest.builder()
				.code("ABC123")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential("user@example.com")
				.expiration(new Date(System.currentTimeMillis() + 600_000))
				.refreshPause(new Date(System.currentTimeMillis() + 60_000))
				.build();

		assertThrows(CodeRefreshDisabledException.class, () -> userVerificationService.refreshRequest(request));
	}

	@Test
	void refreshRequest_WhenRequestRefreshable_ShouldCreateNewRequestKeepingUsageAndCredential() {
		VerificationRequest request = VerificationRequest.builder()
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
	void validateRequest_WhenCodeDoesNotMatch_ShouldThrowInvalidCodeException() {
		VerificationRequest request = VerificationRequest.builder()
				.code("ABC123")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential("user@example.com")
				.expiration(new Date(System.currentTimeMillis() + 10_000))
				.refreshPause(new Date(System.currentTimeMillis() - 1_000))
				.build();

		assertThrows(InvalidCodeException.class, () -> userVerificationService.validateRequest(request, "ZZZ999"));
	}

	@Test
	void validateRequest_WhenCodeMatchesButExpired_ShouldThrowCodeExpiredException() {
		VerificationRequest request = VerificationRequest.builder()
				.code("ABC123")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential("user@example.com")
				.expiration(new Date(System.currentTimeMillis() - 1_000))
				.refreshPause(new Date(System.currentTimeMillis() - 1_000))
				.build();

		assertThrows(CodeExpiredException.class, () -> userVerificationService.validateRequest(request, "ABC123"));
	}

	@Test
	void validateRequest_WhenCodeMatchesAndNotExpired_ShouldNotThrow() {
		VerificationRequest request = VerificationRequest.builder()
				.code("ABC123")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential("user@example.com")
				.expiration(new Date(System.currentTimeMillis() + 10_000))
				.refreshPause(new Date(System.currentTimeMillis() - 1_000))
				.build();

		assertDoesNotThrow(() -> userVerificationService.validateRequest(request, "ABC123"));
	}

	@Test
	void completeRequest_WhenNoVerificationInProcess_ShouldThrowNoVerificationInProcessException() {
		User user = User.builder().build();

		assertThrows(NoVerificationInProcessException.class, () -> userVerificationService.completeRequest(user));
	}

	@Test
	void completeRequest_WhenEmailVerification_ShouldSetEmailAndClearRequest() {
		User user = User.builder()
				.verificationRequest(VerificationRequest.builder()
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
	void completeRequest_WhenPhoneVerification_ShouldSetPhoneAndClearRequest() {
		User user = User.builder()
				.verificationRequest(VerificationRequest.builder()
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
	void completeRequest_WhenPasswordReset_ShouldSetPasswordAndClearRequest() {
		User user = User.builder()
				.verificationRequest(VerificationRequest.builder()
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
