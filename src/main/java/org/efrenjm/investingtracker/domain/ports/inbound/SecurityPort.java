package org.efrenjm.investingtracker.domain.ports.inbound;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface SecurityPort {
	boolean isValidToken(String token);

	boolean arePasswordsEqual(String rawPassword, String encodedPassword);

	String encode(String text);

	String extractUserId(String token);

	Mono<UserDetails> loadUserByUsername(String username);

	Mono<UserDetails> loadUserByUserId(String userId);

	Mono<String> generateToken(String userId);

	Mono<Void> setTokenInCookie(String token, ServerHttpResponse response);
}
