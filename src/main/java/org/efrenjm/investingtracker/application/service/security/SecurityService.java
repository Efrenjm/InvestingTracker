package org.efrenjm.investingtracker.application.service.security;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.UserNotFoundException;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.inbound.SecurityPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.PasswordEncoderPort;
import org.efrenjm.investingtracker.infrastructure.jwt.JwtOperations;
import org.efrenjm.investingtracker.infrastructure.security.SecurityUser;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SecurityService implements SecurityPort {
	private final UserRepositoryPort userRepository;
	private final JwtOperations jwtOperations;
	private final PasswordEncoderPort passwordEncoder;

	@Override
	public Mono<String> generateToken(String userId) {
		return jwtOperations.generateToken(userId);
	}

	@Override
	public Mono<Void> setTokenInCookie(String token, ServerHttpResponse response) {
		return jwtOperations.setTokenInCookie(token, response);
	}

	@Override
	public boolean isValidToken(String token) {
		return jwtOperations.isValidToken(token);
	}

	@Override
	public String extractUserId(String token) {
		return jwtOperations.extractUserId(token);
	}

	@Override
	public Mono<UserDetails> loadUserByUsername(String username) {
		return userRepository.findByAnyCredential(username)
				.switchIfEmpty(Mono.error(new UserNotFoundException(username)))
				.map(SecurityUser::new);
	}

	@Override
	public Mono<UserDetails> loadUserByUserId(String userId) {
		return userRepository.findById(userId)
				.switchIfEmpty(Mono.error(new UserNotFoundException(new ObjectId(userId))))
				.map(SecurityUser::new);
	}

	@Override
	public boolean arePasswordsEqual(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	@Override
	public String encode(String text) {
		return passwordEncoder.encode(text);
	}
}
