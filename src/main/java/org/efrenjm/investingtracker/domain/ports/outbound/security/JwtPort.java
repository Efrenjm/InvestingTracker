package org.efrenjm.investingtracker.domain.ports.outbound.security;

import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface JwtPort
{
	Mono<String> generateToken(UserIdentity payload);

	boolean isValidToken(String token);

	String extractUserId(String token);

	Set<SystemRole> extractRoles(String token);

	Mono<Void> setTokenInCookie(String token, ServerHttpResponse response);
}
