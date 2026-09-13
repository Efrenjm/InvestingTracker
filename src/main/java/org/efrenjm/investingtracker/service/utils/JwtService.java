package org.efrenjm.investingtracker.service.utils;

import io.jsonwebtoken.*;
import org.efrenjm.investingtracker.model.user.User;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService implements IJwtService {
//	@Value("${jwt.secret}")
	private static final SecretKey key = Jwts.SIG.HS256.key().build();
//	@Value("{jwt.expiration}")
	private static final long EXPIRATION_TIME = 864_000_000; // 10 days

	public Mono<String> generateToken(User userDetails) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
		return Mono.just(Jwts.builder()
				.issuer("investing-tracker")
				.subject(userDetails.getId().toString())
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(key)
				.compact());
	}

	public boolean isTokenValid(String token) {
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

	public Mono<Void> setTokenInCookie(String token, ServerHttpResponse response) {
		ResponseCookie cookie = ResponseCookie.from("jwt", token)
				.httpOnly(true)
				//				.secure(true)    // TODO: Implement HTTPS
				.path("/")
				.maxAge(8 * 60 * 60)
				.build();
		response.addCookie(cookie);
		return response.setComplete();
	}
}
