package org.efrenjm.investingtracker.application.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Set;
import org.efrenjm.investingtracker.application.security.port.out.ProfileCachePort;
import org.efrenjm.investingtracker.application.security.port.out.SessionStorePort;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.UserNotFoundException;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.JwtPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private ProfileCachePort profileCache;
    @Mock private SessionStorePort sessionStore;
    @Mock private JwtPort jwtOperations;

    @InjectMocks private SecurityService securityService;

    @Test
    void isValidTokenShouldDelegateToJwtPort() {
        when(jwtOperations.isValidToken("token")).thenReturn(true);
        assertTrue(securityService.isValidToken("token"));
    }

    @Test
    void extractUserIdShouldDelegateToJwtPort() {
        when(jwtOperations.extractUserId("token")).thenReturn("u-1");
        assertEquals("u-1", securityService.extractUserId("token"));
    }

    @Test
    void extractSessionIdShouldDelegateToJwtPort() {
        when(jwtOperations.extractSessionId("token")).thenReturn("session-1");
        assertEquals("session-1", securityService.extractSessionId("token"));
    }

    @Test
    void extractRolesShouldDelegateToJwtPort() {
        Set<SystemRole> roles = Set.of(SystemRole.STANDARD);
        when(jwtOperations.extractRoles("token")).thenReturn(roles);
        assertEquals(roles, securityService.extractRoles("token"));
    }

    @Test
    void loadUserByUsernameWhenNotFoundShouldEmitUserNotFoundException() {
        when(userRepository.findByAnyCredential("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(securityService.loadUserByUsername("unknown"))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void loadUserByUserIdWhenNotFoundShouldEmitUserNotFoundException() {
        when(userRepository.findById("missing-id")).thenReturn(Mono.empty());

        StepVerifier.create(securityService.loadUserByUserId("missing-id"))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void loadProfileByUserIdWhenSessionExistsShouldReturnCachedProfile() {
        Profile cached =
                new Profile("u-1", "user", "u@example.com", null, null, null, null, null, Set.of());
        when(profileCache.getUserProfile("u-1")).thenReturn(Mono.just(cached));

        StepVerifier.create(securityService.loadProfileByUserId("u-1"))
                .expectNext(cached)
                .verifyComplete();

        verify(profileCache).getUserProfile("u-1");
    }

    @Test
    void loadProfileByUserIdWhenSessionMissingShouldLoadAndStoreInSession() {
        User user = User.builder().id("u-1").username("user").email("u@example.com").build();
        when(profileCache.getUserProfile("u-1")).thenReturn(Mono.empty());
        when(userRepository.findById("u-1")).thenReturn(Mono.just(user));
        when(profileCache.storeUserProfile(eq("u-1"), any(Profile.class), any(Duration.class)))
                .thenReturn(Mono.just(true));

        StepVerifier.create(securityService.loadProfileByUserId("u-1"))
                .assertNext(
                        profile -> {
                            assertEquals("u-1", profile.id());
                            assertEquals("user", profile.username());
                            assertEquals("u@example.com", profile.email());
                        })
                .verifyComplete();

        verify(profileCache).storeUserProfile(eq("u-1"), any(Profile.class), any(Duration.class));
    }
}
