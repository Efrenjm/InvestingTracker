//package org.efrenjm.investingtracker.interfaces.rest.authentication;
//
//import org.bson.types.ObjectId;
//import org.efrenjm.investingtracker.domain.exception.BadRequestException;
//import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
//import org.efrenjm.investingtracker.domain.model.user.User;
//import org.efrenjm.investingtracker.domain.ports.inbound.AuthServicePort;
//import org.efrenjm.investingtracker.infrastructure.security.SecurityUser;
//import org.efrenjm.investingtracker.interfaces.rest.authentication.dto.*;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.net.URI;
//
//import static org.mockito.Mockito.when;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthenticationControllerTest {
//
//	@Mock
//	private AuthServicePort authService;
//
//	@Mock
//	private ServerWebExchange exchange;
//
//	@InjectMocks
//	private AuthenticationController controller;
//
//	@Test
//	void login_ValidCredentials_ReturnsOk() {
//		// Arrange
//		UserPasswordDTO request = new UserPasswordDTO();
//		request.setUsername("user@example.com");
//		request.setPassword("password123");
//
//		when(authService.login(request.getUsername(), request.getPassword(), exchange))
//				.thenReturn(Mono.empty());
//
//		// Act
//		Mono<ResponseEntity<Void>> result = controller.login(request, exchange);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(200, response.getStatusCodeValue());
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void register_ValidData_ReturnsCreated() {
//		// Arrange
//		RegisterRequestDTO request = new RegisterRequestDTO();
//		request.setUsername("user@example.com");
//		request.setPassword("Password1@");
//
//		String userId = new ObjectId().toString();
//		User user = User.builder()
//				.id(userId)
//				.build();
//
//		when(authService.register(request.getUsername(), request.getPassword()))
//				.thenReturn(Mono.just(user));
//
//		// Act
//		Mono<ResponseEntity<RegisterResponseDTO>> result = controller.register(request);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(201, response.getStatusCodeValue());
//					assertEquals(URI.create("/verify-code"), response.getHeaders().getLocation());
//					assertEquals(userId, response.getBody().getUserId());
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void refreshVerificationCode_AuthenticatedUser_ReturnsOk() {
//		// Arrange
//		User user = User.builder()
//				.id(new ObjectId().toString())
//				.build();
//
//		SecurityUser securityUser = new SecurityUser(user);
//
//		when(authService.refreshVerificationCode(securityUser.getDomainUser()))
//				.thenReturn(Mono.just(user));
//
//		// Act
//		Mono<ResponseEntity<String>> result = controller.refreshVerificationCode(null, securityUser);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(200, response.getStatusCodeValue());
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void refreshVerificationCode_UnauthenticatedWithUserId_ReturnsOk() {
//		// Arrange
//		String userId = new ObjectId().toString();
//		User user = User.builder()
//				.id(userId)
//				.build();
//
//		when(authService.refreshVerificationCode(userId))
//				.thenReturn(Mono.just(user));
//
//		// Act
//		Mono<ResponseEntity<String>> result = controller.refreshVerificationCode(userId, null);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(200, response.getStatusCodeValue());
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void refreshVerificationCode_UnauthenticatedWithoutUserId_ThrowsException() {
//		// Arrange - no userId and no authenticated user
//
//		// Act
//		Mono<ResponseEntity<String>> result = controller.refreshVerificationCode(null, null);
//
//		// Assert
//		StepVerifier.create(result)
//				.expectError(BadRequestException.class)
//				.verify();
//	}
//
//	@Test
//	void verifyCode_AuthenticatedUser_ReturnsOk() {
//		// Arrange
//		VerifyCodeRequestDTO request = new VerifyCodeRequestDTO();
//		request.setCode("123456");
//
//		User user = User.builder()
//				.id(new ObjectId().toString())
//				.active(true)
//				.build();
//
//		SecurityUser securityUser = new SecurityUser(user);
//
//		when(authService.verifyCode(securityUser.getDomainUser(), request.getCode()))
//				.thenReturn(Mono.just(user));
//
//		// Act
//		Mono<ResponseEntity<VerifyCodeResponseDTO>> result = controller.verifyCode(request, securityUser);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(200, response.getStatusCode().value());
//					assertTrue(response.getBody().isActive());
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void verifyCode_UnauthenticatedWithUserId_ReturnsOk() {
//		// Arrange
//		String userId = new ObjectId().toString();
//		VerifyCodeRequestDTO request = new VerifyCodeRequestDTO();
//		request.setCode("123456");
//		request.setUserId(userId);
//
//		User user = User.builder()
//				.id(userId)
//				.active(true)
//				.build();
//
//		when(authService.verifyCode(userId, request.getCode()))
//				.thenReturn(Mono.just(user));
//
//		// Act
//		Mono<ResponseEntity<VerifyCodeResponseDTO>> result = controller.verifyCode(request, null);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(200, response.getStatusCodeValue());
//					assertTrue(response.getBody().isActive());
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void verifyCode_UnauthenticatedWithoutUserId_ThrowsException() {
//		// Arrange
//		VerifyCodeRequestDTO request = new VerifyCodeRequestDTO();
//		request.setCode("123456");
//		// No userId provided
//
//		// Act
//		Mono<ResponseEntity<VerifyCodeResponseDTO>> result = controller.verifyCode(request, null);
//
//		// Assert
//		StepVerifier.create(result)
//				.expectError(BadRequestException.class)
//				.verify();
//	}
//
//	@Test
//	void updateEmail_ValidEmail_ReturnsOk() {
//		// Arrange
//		String newEmail = "newemail@example.com";
//		User user = User.builder()
//				.id(new ObjectId().toString())
//				.email("oldemail@example.com")
//				.build();
//
//		SecurityUser securityUser = new SecurityUser(user);
//
//		when(authService.updateCredential(securityUser.getDomainUser(), CodeUsage.EMAIL_VERIFICATION, newEmail))
//				.thenReturn(Mono.just(user));
//
//		// Act
//		Mono<ResponseEntity<String>> result = controller.updateEmail(newEmail, securityUser);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(200, response.getStatusCodeValue());
//					assertEquals("Email updated successfully", response.getBody());
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void updatePhone_ValidPhone_ReturnsOk() {
//		// Arrange
//		String newPhone = "+1234567890";
//		User user = User.builder()
//				.id(new ObjectId().toString())
//				.build();
//
//		SecurityUser securityUser = new SecurityUser(user);
//
//		when(authService.updateCredential(securityUser.getDomainUser(), CodeUsage.PHONE_VERIFICATION, newPhone))
//				.thenReturn(Mono.just(user));
//
//		// Act
//		Mono<ResponseEntity<String>> result = controller.updatePhone(newPhone, securityUser);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(200, response.getStatusCodeValue());
//					assertEquals("Phone updated successfully", response.getBody());
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void updatePassword_ValidCredentials_ReturnsOk() {
//		// Arrange
//		UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO();
//		request.setNewPassword("NewPassword1@");
//		request.setOldPassword("OldPassword1@");
//
//		User user = User.builder()
//				.id(new ObjectId().toString())
//				.build();
//
//		SecurityUser securityUser = new SecurityUser(user);
//
//		when(authService.updatePassword(securityUser.getDomainUser(), request.getNewPassword(), request.getOldPassword()))
//				.thenReturn(Mono.just(user));
//
//		// Act
//		Mono<ResponseEntity<String>> result = controller.updatePassword(request, securityUser);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(200, response.getStatusCodeValue());
//					assertEquals("Password updated successfully", response.getBody());
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void forgotPassword_ValidData_ReturnsOk() {
//		// Arrange
//		ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO();
//		request.setUsername("user@example.com");
//		request.setNewPassword("NewPassword1@");
//		request.setConfirmPassword("NewPassword1@");
//
//		User user = User.builder()
//				.id(new ObjectId().toString())
//				.build();
//
//		when(authService.forgotPassword(request.getUsername(), request.getNewPassword()))
//				.thenReturn(Mono.just(user));
//
//		// Act
//		Mono<ResponseEntity<String>> result = controller.forgotPassword(request);
//
//		// Assert
//		StepVerifier.create(result)
//				.assertNext(response -> {
//					assertEquals(200, response.getStatusCodeValue());
//					assertEquals("Password updated successfully", response.getBody());
//				})
//				.verifyComplete();
//	}
//}