package org.efrenjm.investingtracker.application.service.security;

import org.efrenjm.investingtracker.application.service.user_service.exceptions.UserNotFoundException;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.JwtPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.PasswordEncoderPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.SessionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

	@Mock
	private UserRepositoryPort userRepository;
	@Mock
	private SessionPort sessionOperations;
	@Mock
	private JwtPort jwtOperations;
	@Mock
	private PasswordEncoderPort passwordEncoder;
	@Mock
	private ServerHttpResponse response;

	@InjectMocks
	private SecurityService securityService;

	@Test
	void generateToken_ShouldDelegateToJwtPort() {
		User user = User.builder().id("u-1").roles(Set.of(SystemRole.STANDARD)).build();
		when(jwtOperations.generateToken(any(UserIdentity.class))).thenReturn(Mono.just("token"));

		StepVerifier.create(securityService.generateToken(user))
				.expectNext("token")
				.verifyComplete();

		verify(jwtOperations).generateToken(any(UserIdentity.class));
	}

	@Test
	void setTokenInCookie_ShouldDelegateToJwtPort() {
		when(jwtOperations.setTokenInCookie("token", response)).thenReturn(Mono.empty());

		StepVerifier.create(securityService.setTokenInCookie("token", response))
				.verifyComplete();
	}

	@Test
	void clearTokenCookie_ShouldDelegateToJwtPort() {
		when(jwtOperations.clearTokenCookie(response)).thenReturn(Mono.empty());

		StepVerifier.create(securityService.clearTokenCookie(response))
				.verifyComplete();
	}

	@Test
	void isValidToken_ShouldDelegateToJwtPort() {
		when(jwtOperations.isValidToken("token")).thenReturn(true);
		assertTrue(securityService.isValidToken("token"));
	}

	@Test
	void extractUserId_ShouldDelegateToJwtPort() {
		when(jwtOperations.extractUserId("token")).thenReturn("u-1");
		assertEquals("u-1", securityService.extractUserId("token"));
	}

	@Test
	void extractRoles_ShouldDelegateToJwtPort() {
		Set<SystemRole> roles = Set.of(SystemRole.STANDARD);
		when(jwtOperations.extractRoles("token")).thenReturn(roles);
		assertEquals(roles, securityService.extractRoles("token"));
	}

	@Test
	void loadUserByUsername_WhenNotFound_ShouldEmitUserNotFoundException() {
		when(userRepository.findByAnyCredential("unknown")).thenReturn(Mono.empty());

		StepVerifier.create(securityService.loadUserByUsername("unknown"))
				.expectError(UserNotFoundException.class)
				.verify();
	}

	@Test
	void loadUserByUserId_WhenNotFound_ShouldEmitUserNotFoundException() {
		when(userRepository.findById("missing-id")).thenReturn(Mono.empty());

		StepVerifier.create(securityService.loadUserByUserId("missing-id"))
				.expectError(UserNotFoundException.class)
				.verify();
	}

	@Test
	void loadProfileByUserId_WhenSessionExists_ShouldReturnCachedProfile() {
		Profile cached = new Profile("u-1", "user", "u@example.com", null, null, null, null, null, Set.of());
		when(sessionOperations.getUserSession("u-1")).thenReturn(Mono.just(cached));

		StepVerifier.create(securityService.loadProfileByUserId("u-1"))
				.expectNext(cached)
				.verifyComplete();
	}

	@Test
	void loadProfileByUserId_WhenSessionMissing_ShouldLoadAndStoreInSession() {
		User user = User.builder().id("u-1").username("user").email("u@example.com").build();
		when(sessionOperations.getUserSession("u-1")).thenReturn(Mono.empty());
		when(userRepository.findById("u-1")).thenReturn(Mono.just(user));
		when(sessionOperations.storeUserSession(eq("u-1"), any(Profile.class), any(Duration.class))).thenReturn(Mono.just(true));

		StepVerifier.create(securityService.loadProfileByUserId("u-1"))
				.assertNext(profile -> {
					assertEquals("u-1", profile.id());
					assertEquals("user", profile.username());
					assertEquals("u@example.com", profile.email());
				})
				.verifyComplete();

		verify(sessionOperations).storeUserSession(eq("u-1"), any(Profile.class), any(Duration.class));
	}

	@Test
	void arePasswordsEqual_ShouldDelegateToPasswordEncoder() {
		when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);
		assertTrue(securityService.arePasswordsEqual("raw", "encoded"));
	}

	@Test
	void encode_ShouldDelegateToPasswordEncoder() {
		when(passwordEncoder.encode("raw")).thenReturn("encoded");
		assertEquals("encoded", securityService.encode("raw"));
	}
}
