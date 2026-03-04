package org.efrenjm.investingtracker.interfaces.rest.controller.authentication;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.ports.inbound.AuthPort;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.UserPasswordDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.RegisterResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.VerifyCodeRequestDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.VerifyCodeResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.exception.NoUserProvidedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

	@Mock
	private AuthPort authService;

	@Mock
	private ServerWebExchange exchange;

	@InjectMocks
	private AuthenticationController controller;

	@Test
	void login_ValidCredentials_ReturnsOk() {
		UserPasswordDTO request = new UserPasswordDTO("user@example.com", "password123");

		when(authService.login(request.getUsername(), request.getPassword(), exchange))
				.thenReturn(Mono.empty());

		Mono<ResponseEntity<Void>> result = controller.login(request, exchange);

		StepVerifier.create(result)
				.assertNext(response -> {
					assertEquals(200, response.getStatusCode().value());
				})
				.verifyComplete();
	}

	@Test
	void register_ValidData_ReturnsCreated() {
		UserPasswordDTO request = new UserPasswordDTO("user@example.com", "Password1@");

		String userId = new ObjectId().toString();

		VerificationRequest verificationRequest = VerificationRequest.builder()
				.code("ABC123")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential("user@example.com")
				.expiration(new Date(System.currentTimeMillis() + 600_000))
				.refreshPause(new Date(System.currentTimeMillis() + 60_000))
				.build();

		User user = User.builder()
				.id(userId)
				.verificationRequest(verificationRequest)
				.build();

		when(authService.register(request.getUsername(), request.getPassword()))
				.thenReturn(Mono.just(user));

		Mono<ResponseEntity<RegisterResponseDTO>> result = controller.register(request);

		StepVerifier.create(result)
				.assertNext(response -> {
					assertEquals(201, response.getStatusCode().value());
					assertEquals(URI.create("/verify-code"), response.getHeaders().getLocation());
					assertEquals(userId, response.getBody().getUserId());
				})
				.verifyComplete();
	}

	@Test
	void refreshVerificationCode_AuthenticatedUser_ReturnsOk() {
		User user = User.builder()
				.id(new ObjectId().toString())
				.build();

		when(authService.refreshVerificationCode(user))
				.thenReturn(Mono.just(user));

		Mono<ResponseEntity<String>> result = controller.refreshVerificationCode(null, user);

		StepVerifier.create(result)
				.assertNext(response -> {
					assertEquals(200, response.getStatusCode().value());
				})
				.verifyComplete();
	}

	@Test
	void refreshVerificationCode_UnauthenticatedWithUserId_ReturnsOk() {
		String userId = new ObjectId().toString();
		User user = User.builder()
				.id(userId)
				.build();

		when(authService.refreshVerificationCode(userId))
				.thenReturn(Mono.just(user));

		Mono<ResponseEntity<String>> result = controller.refreshVerificationCode(userId, null);

		StepVerifier.create(result)
				.assertNext(response -> {
					assertEquals(200, response.getStatusCode().value());
				})
				.verifyComplete();
	}

	@Test
	void refreshVerificationCode_UnauthenticatedWithoutUserId_ThrowsException() {
		Mono<ResponseEntity<String>> result = controller.refreshVerificationCode(null, null);

		StepVerifier.create(result)
				.expectError(NoUserProvidedException.class)
				.verify();
	}

	@Test
	void verifyCode_AuthenticatedUser_ReturnsOk() {
		VerifyCodeRequestDTO request = new VerifyCodeRequestDTO();
		request.setCode("123456");

		User user = User.builder()
				.id(new ObjectId().toString())
				.email("user@example.com")
				.active(true)
				.build();

		when(authService.verifyCode(user, request.getCode()))
				.thenReturn(Mono.just(user));

		Mono<ResponseEntity<VerifyCodeResponseDTO>> result = controller.verifyCode(request, user);

		StepVerifier.create(result)
				.assertNext(response -> {
					assertEquals(200, response.getStatusCode().value());
					assertNotNull(response.getBody());
					assertEquals(user.getId(), response.getBody().getUserId());
				})
				.verifyComplete();
	}

	@Test
	void verifyCode_UnauthenticatedWithUserId_ReturnsOk() {
		String userId = new ObjectId().toString();
		VerifyCodeRequestDTO request = new VerifyCodeRequestDTO();
		request.setCode("123456");
		request.setUserId(userId);

		User user = User.builder()
				.id(userId)
				.email("user@example.com")
				.active(true)
				.build();

		when(authService.verifyCode(userId, request.getCode()))
				.thenReturn(Mono.just(user));

		Mono<ResponseEntity<VerifyCodeResponseDTO>> result = controller.verifyCode(request, null);

		StepVerifier.create(result)
				.assertNext(response -> {
					assertEquals(200, response.getStatusCode().value());
					assertNotNull(response.getBody());
					assertEquals(userId, response.getBody().getUserId());
				})
				.verifyComplete();
	}

	@Test
	void verifyCode_UnauthenticatedWithoutUserId_ThrowsException() {
		VerifyCodeRequestDTO request = new VerifyCodeRequestDTO();
		request.setCode("123456");

		Mono<ResponseEntity<VerifyCodeResponseDTO>> result = controller.verifyCode(request, null);

		StepVerifier.create(result)
				.expectError(NoUserProvidedException.class)
				.verify();
	}
}
