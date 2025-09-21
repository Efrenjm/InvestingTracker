package org.efrenjm.investingtracker.domain.ports.inbound;

import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.efrenjm.investingtracker.infrastructure.persistence.redis.UserSession;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface SecurityPort {
	boolean isValidToken(String token);

	boolean arePasswordsEqual(String rawPassword, String encodedPassword);

	String encode(String text);

	String extractUserId(String token);

	Set<SystemRole> extractRoles(String token);

	Mono<User> loadUserByUsername(String username);

	Mono<User> loadUserByUserId(String userId);

	Mono<Profile> loadProfileByUserId(String userId);

	Mono<String> generateToken(User user);

	Mono<Void> setTokenInCookie(String token, ServerHttpResponse response);
}
