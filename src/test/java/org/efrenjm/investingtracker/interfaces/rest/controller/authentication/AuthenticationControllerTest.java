package org.efrenjm.investingtracker.interfaces.rest.controller.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Date;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.ports.inbound.AuthPort;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.AuthResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.RegisterRequestDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.RegisterResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.UserPasswordDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.VerificationCodeResponseDTO;
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

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock private AuthPort authService;

    @Mock private ServerWebExchange exchange;

    @InjectMocks private AuthenticationController controller;

    @Test
    void loginValidCredentialsReturnsOk() {
        UserPasswordDTO request = new UserPasswordDTO("user@example.com", "password123");
        User user =
                User.builder()
                        .username("user@example.com")
                        .email("user@example.com")
                        .active(true)
                        .build();

        when(authService.login(request.getUsername(), request.getPassword(), exchange))
                .thenReturn(Mono.just(user));

        Mono<ResponseEntity<AuthResponseDTO>> result = controller.login(request, exchange);

        StepVerifier.create(result)
                .assertNext(
                        response -> {
                            assertEquals(200, response.getStatusCode().value());
                            assertNotNull(response.getBody());
                            assertEquals("user@example.com", response.getBody().user().username());
                        })
                .verifyComplete();
    }

    @Test
    void registerValidDataReturnsCreated() {
        RegisterRequestDTO request = new RegisterRequestDTO("user@example.com");
        request.setPassword("Password1@");
        request.setConfirmPassword("Password1@");

        String userId = new ObjectId().toString();

        VerificationRequest verificationRequest =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential("user@example.com")
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                        .build();

        User user = User.builder().id(userId).verificationRequest(verificationRequest).build();

        when(authService.register(request.getUsername(), request.getPassword()))
                .thenReturn(Mono.just(user));

        Mono<ResponseEntity<RegisterResponseDTO>> result = controller.register(request);

        StepVerifier.create(result)
                .assertNext(
                        response -> {
                            assertEquals(201, response.getStatusCode().value());
                            assertEquals(
                                    URI.create("/verify-code"),
                                    response.getHeaders().getLocation());
                            assertEquals(userId, response.getBody().userId());
                            assertEquals(
                                    "You’re almost there! Check your inbox for the next steps.",
                                    response.getBody().message());
                        })
                .verifyComplete();
    }

    @Test
    void refreshVerificationCodeAuthenticatedUserReturnsOk() {
        User user = User.builder().id(new ObjectId().toString()).build();

        when(authService.refreshVerificationCode(user)).thenReturn(Mono.just(user));

        Mono<ResponseEntity<VerificationCodeResponseDTO>> result =
                controller.refreshVerificationCode(null, user);

        StepVerifier.create(result)
                .assertNext(
                        response -> {
                            assertEquals(200, response.getStatusCode().value());
                            assertEquals("VERIFICATION_CODE_SENT", response.getBody().code());
                        })
                .verifyComplete();
    }

    @Test
    void refreshVerificationCodeUnauthenticatedWithUserIdReturnsOk() {
        String userId = new ObjectId().toString();
        User user = User.builder().id(userId).build();

        when(authService.refreshVerificationCode(userId)).thenReturn(Mono.just(user));

        Mono<ResponseEntity<VerificationCodeResponseDTO>> result =
                controller.refreshVerificationCode(userId, null);

        StepVerifier.create(result)
                .assertNext(
                        response -> {
                            assertEquals(200, response.getStatusCode().value());
                            assertEquals("VERIFICATION_CODE_SENT", response.getBody().code());
                        })
                .verifyComplete();
    }

    @Test
    void refreshVerificationCodeUnauthenticatedWithoutUserIdThrowsException() {
        Mono<ResponseEntity<VerificationCodeResponseDTO>> result =
                controller.refreshVerificationCode(null, null);

        StepVerifier.create(result).expectError(NoUserProvidedException.class).verify();
    }

    @Test
    void verifyCodeAuthenticatedUserReturnsOk() {
        VerifyCodeRequestDTO request = new VerifyCodeRequestDTO();
        request.setCode("123456");

        User user =
                User.builder()
                        .id(new ObjectId().toString())
                        .email("user@example.com")
                        .active(true)
                        .build();

        when(authService.verifyCode(user, request.getCode())).thenReturn(Mono.just(user));

        Mono<ResponseEntity<VerifyCodeResponseDTO>> result = controller.verifyCode(request, user);

        StepVerifier.create(result)
                .assertNext(
                        response -> {
                            assertEquals(201, response.getStatusCode().value());
                            assertEquals(
                                    URI.create("/password"), response.getHeaders().getLocation());
                            assertNotNull(response.getBody());
                            assertEquals(user.getId(), response.getBody().userId());
                        })
                .verifyComplete();
    }

    @Test
    void verifyCodeUnauthenticatedWithUserIdReturnsOk() {
        String userId = new ObjectId().toString();
        VerifyCodeRequestDTO request = new VerifyCodeRequestDTO();
        request.setCode("123456");
        request.setUserId(userId);

        User user = User.builder().id(userId).email("user@example.com").active(true).build();

        when(authService.verifyCode(userId, request.getCode())).thenReturn(Mono.just(user));

        Mono<ResponseEntity<VerifyCodeResponseDTO>> result = controller.verifyCode(request, null);

        StepVerifier.create(result)
                .assertNext(
                        response -> {
                            assertEquals(201, response.getStatusCode().value());
                            assertEquals(
                                    URI.create("/password"), response.getHeaders().getLocation());
                            assertNotNull(response.getBody());
                            assertEquals(userId, response.getBody().userId());
                        })
                .verifyComplete();
    }

    @Test
    void verifyCodeUnauthenticatedWithoutUserIdThrowsException() {
        VerifyCodeRequestDTO request = new VerifyCodeRequestDTO();
        request.setCode("123456");

        Mono<ResponseEntity<VerifyCodeResponseDTO>> result = controller.verifyCode(request, null);

        StepVerifier.create(result).expectError(NoUserProvidedException.class).verify();
    }
}
