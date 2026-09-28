package org.efrenjm.investingtracker.domain.ports.outbound.security;

import java.util.Set;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

public interface JwtPort {
    Mono<String> generateToken(UserIdentity payload);

    boolean isValidToken(String token);

    String extractUserId(String token);

    String extractSessionId(String token);

    Set<SystemRole> extractRoles(String token);

    Mono<Void> setTokenInCookie(String token, ServerHttpResponse response);

    Mono<Void> clearTokenCookie(ServerHttpResponse response);
}
