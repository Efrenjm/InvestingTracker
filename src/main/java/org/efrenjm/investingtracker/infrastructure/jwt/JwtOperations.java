package org.efrenjm.investingtracker.infrastructure.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.efrenjm.investingtracker.domain.ports.outbound.security.JwtPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtOperations implements JwtPort
{
	private final SecretKey key;
	private final long EXPIRATION_TIME;

	public JwtOperations(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expirationTime)
	{
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.EXPIRATION_TIME = expirationTime;
	}

	public Mono<String> generateToken(UserIdentity payload) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", payload.roles());

		return Mono.just(Jwts.builder()
				.issuer("investing-tracker")
				.subject(payload.id())
				.claims(claims)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(key)
				.compact());
	}

	public boolean isValidToken(String token) {
		try {
			boolean isTokenExpired = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload()
					.getExpiration()
					.before(new Date());
			return !isTokenExpired;
		} catch (JwtException e) {
			return false;
		}
	}

	public String extractUserId(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}

	@Override
	public Set<SystemRole> extractRoles(String token) {
		@SuppressWarnings("unchecked")
		List<String> roles = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("roles", List.class);

		return roles == null ? Set.of() :
				roles.stream()
						.map(SystemRole::valueOf)
						.collect(Collectors.toSet());
	}

	public Mono<Void> setTokenInCookie(String token, ServerHttpResponse response) {
		ResponseCookie cookie = ResponseCookie.from("jwt", token)
				.httpOnly(true)
				//				.secure(true)    // TODO: Implement HTTPS
				.path("/")
				.maxAge(8 * 60 * (long) 60)
				.build();
		response.addCookie(cookie);
		return response.setComplete();
	}
}
