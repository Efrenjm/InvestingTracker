package org.efrenjm.investingtracker.application.service.security;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.UserNotFoundException;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.inbound.SecurityPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.JwtPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.PasswordEncoderPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.SessionPort;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SecurityService implements SecurityPort
{
	private final UserRepositoryPort userRepository;
	private final SessionPort sessionOperations;
	private final JwtPort jwtOperations;
	private final PasswordEncoderPort passwordEncoder;

	@Override
	public Mono<String> generateToken(User user)
	{
		return jwtOperations.generateToken(UserIdentity.from(user));
	}

	@Override
	public Mono<Void> setTokenInCookie(String token, ServerHttpResponse response)
	{
		return jwtOperations.setTokenInCookie(token, response);
	}

	@Override
	public boolean isValidToken(String token)
	{
		return jwtOperations.isValidToken(token);
	}

	@Override
	public String extractUserId(String token)
	{
		return jwtOperations.extractUserId(token);
	}

	@Override
	public Set<SystemRole> extractRoles(String token)
	{
		return jwtOperations.extractRoles(token);
	}

	@Override
	public Mono<User> loadUserByUsername(String username)
	{
		return userRepository
				.findByAnyCredential(username)
				.switchIfEmpty(Mono.error(new UserNotFoundException(username)));
	}

	@Override
	public Mono<User> loadUserByUserId(String userId)
	{
		return userRepository
				.findById(userId)
				.switchIfEmpty(Mono.error(new UserNotFoundException(userId)));
	}

	@Override
	public Mono<Profile> loadProfileByUserId(String userId)
	{
		return sessionOperations
				.getUserSession(userId)
				.switchIfEmpty(Mono.defer(() -> userRepository
						.findById(userId)
						.map(Profile::from)
						.flatMap(profile -> sessionOperations
								.storeUserSession(userId, profile, Duration.ofHours(1))
								.thenReturn(profile)
						)
				));
	}

	@Override
	public boolean arePasswordsEqual(String rawPassword, String encodedPassword)
	{
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	@Override
	public String encode(String text)
	{
		return passwordEncoder.encode(text);
	}
}
