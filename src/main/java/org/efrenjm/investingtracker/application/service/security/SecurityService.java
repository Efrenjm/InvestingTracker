package org.efrenjm.investingtracker.application.service.security;

import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.security.port.in.JwtAuthenticationUseCase;
import org.efrenjm.investingtracker.application.security.port.in.SecuritySessionUseCase;
import org.efrenjm.investingtracker.application.security.port.in.UserLookupUseCase;
import org.efrenjm.investingtracker.application.security.port.out.ProfileCachePort;
import org.efrenjm.investingtracker.application.security.port.out.SessionStorePort;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.UserNotFoundException;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.JwtPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SecurityService
        implements JwtAuthenticationUseCase, SecuritySessionUseCase, UserLookupUseCase {
    private final UserRepositoryPort userRepository;
    private final ProfileCachePort profileCache;
    private final SessionStorePort sessionStore;
    private final JwtPort jwtOperations;

    @Override
    public boolean isValidToken(String token) {
        return jwtOperations.isValidToken(token);
    }

    @Override
    public String extractUserId(String token) {
        return jwtOperations.extractUserId(token);
    }

    @Override
    public String extractSessionId(String token) {
        return jwtOperations.extractSessionId(token);
    }

    @Override
    public Set<SystemRole> extractRoles(String token) {
        return jwtOperations.extractRoles(token);
    }

    @Override
    public Mono<User> loadUserByUsername(String username) {
        return userRepository
                .findByAnyCredential(username)
                .switchIfEmpty(Mono.error(new UserNotFoundException(username)));
    }

    @Override
    public Mono<User> loadUserByUserId(String userId) {
        return userRepository
                .findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)));
    }

    @Override
    public Mono<Profile> loadProfileByUserId(String userId) {
        return profileCache
                .getUserProfile(userId)
                .switchIfEmpty(
                        Mono.defer(
                                () ->
                                        userRepository
                                                .findById(userId)
                                                .map(Profile::from)
                                                .flatMap(
                                                        profile ->
                                                                profileCache
                                                                        .storeUserProfile(
                                                                                userId,
                                                                                profile,
                                                                                Duration.ofHours(1))
                                                                        .thenReturn(profile))));
    }

    @Override
    public Mono<Boolean> isSessionActive(String sessionId) {
        return sessionStore.isActive(sessionId);
    }
}
