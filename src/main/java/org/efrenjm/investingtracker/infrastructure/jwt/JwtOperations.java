package org.efrenjm.investingtracker.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.efrenjm.investingtracker.domain.ports.outbound.security.JwtPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class JwtOperations implements JwtPort {
    private static final String JWT_COOKIE_NAME = "jwt";
    private static final String JWT_COOKIE_PATH = "/";

    private final SecretKey key;
    private final long EXPIRATION_TIME;

    public JwtOperations(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationTime) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.EXPIRATION_TIME = expirationTime;
    }

    public Mono<String> generateToken(UserIdentity payload) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        String sessionId = UUID.randomUUID().toString();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", payload.roles());

        return Mono.just(
                Jwts.builder()
                        .id(sessionId)
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
            Claims claims = parseClaims(token);
            Date expiration = claims.getExpiration();
            return expiration != null
                    && expiration.after(new Date())
                    && isValidSessionId(claims.getId());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public String extractSessionId(String token) {
        String sessionId = parseClaims(token).getId();
        if (!isValidSessionId(sessionId)) {
            throw new MalformedJwtException("JWT is missing a valid session identifier");
        }
        return sessionId;
    }

    @Override
    public Set<SystemRole> extractRoles(String token) {
        @SuppressWarnings("unchecked")
        List<String> roles = parseClaims(token).get("roles", List.class);

        return roles == null
                ? Set.of()
                : roles.stream().map(SystemRole::valueOf).collect(Collectors.toSet());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    private boolean isValidSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }

        try {
            UUID.fromString(sessionId);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public Mono<Void> setTokenInCookie(String token, ServerHttpResponse response) {
        ResponseCookie cookie =
                ResponseCookie.from(JWT_COOKIE_NAME, token)
                        .httpOnly(true)
                        .path(JWT_COOKIE_PATH)
                        .sameSite("Lax")
                        .maxAge(8 * 60 * (long) 60)
                        .build();
        response.addCookie(cookie);
        return Mono.empty();
    }

    @Override
    public Mono<Void> clearTokenCookie(ServerHttpResponse response) {
        ResponseCookie cookie =
                ResponseCookie.from(JWT_COOKIE_NAME, "")
                        .httpOnly(true)
                        .path(JWT_COOKIE_PATH)
                        .sameSite("Lax")
                        .maxAge(0)
                        .build();
        response.addCookie(cookie);
        return Mono.empty();
    }
}
