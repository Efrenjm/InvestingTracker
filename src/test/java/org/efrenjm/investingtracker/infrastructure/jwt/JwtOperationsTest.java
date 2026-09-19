package org.efrenjm.investingtracker.infrastructure.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtOperationsTest {
	private static final String SECRET = "0123456789012345678901234567890123456789012345678901234567890123";
	private static final long EXPIRATION_MILLIS = 60_000L;

	@Test
	void generateToken_ContainsUniqueNonEmptySessionIdentifier() {
		JwtOperations operations = new JwtOperations(SECRET, EXPIRATION_MILLIS);
		UserIdentity identity = new UserIdentity("user-1", Set.of(SystemRole.STANDARD));

		String firstToken = operations.generateToken(identity).block();
		String secondToken = operations.generateToken(identity).block();
		SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

		String firstSessionId = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(firstToken)
				.getPayload()
				.getId();
		String secondSessionId = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(secondToken)
				.getPayload()
				.getId();

		assertNotNull(firstToken);
		assertNotNull(secondToken);
		assertFalse(firstSessionId == null || firstSessionId.isBlank());
		assertFalse(secondSessionId == null || secondSessionId.isBlank());
		assertNotEquals(firstSessionId, secondSessionId);
		assertEquals(firstSessionId, operations.extractSessionId(firstToken));
		assertTrue(operations.isValidToken(firstToken));
	}

	@Test
	void tokenWithoutSessionIdentifier_IsInvalid() {
		JwtOperations operations = new JwtOperations(SECRET, EXPIRATION_MILLIS);
		String token = Jwts.builder()
				.subject("user-1")
				.issuedAt(new java.util.Date())
				.expiration(new java.util.Date(System.currentTimeMillis() + EXPIRATION_MILLIS))
				.signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
				.compact();

		assertFalse(operations.isValidToken(token));
		assertThrows(RuntimeException.class, () -> operations.extractSessionId(token));
	}

	@Test
	void tokenWithMalformedSessionIdentifier_IsInvalid() {
		JwtOperations operations = new JwtOperations(SECRET, EXPIRATION_MILLIS);
		String token = Jwts.builder()
				.id("not-a-uuid")
				.subject("user-1")
				.issuedAt(new java.util.Date())
				.expiration(new java.util.Date(System.currentTimeMillis() + EXPIRATION_MILLIS))
				.signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
				.compact();

		assertFalse(operations.isValidToken(token));
		assertThrows(RuntimeException.class, () -> operations.extractSessionId(token));
	}
}
