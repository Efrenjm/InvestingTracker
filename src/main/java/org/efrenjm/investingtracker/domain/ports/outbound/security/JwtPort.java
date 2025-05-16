package org.efrenjm.investingtracker.domain.ports.outbound.security;

import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

public interface JwtPort {
	Mono<String> generateToken(String userId);
	boolean isValidToken(String token);
	String extractUserId(String token);
	Mono<Void> setTokenInCookie(String token, ServerHttpResponse response);
}
