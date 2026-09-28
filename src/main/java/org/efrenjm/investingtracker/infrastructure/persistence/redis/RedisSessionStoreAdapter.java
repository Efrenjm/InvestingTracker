package org.efrenjm.investingtracker.infrastructure.persistence.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.security.model.SessionRecord;
import org.efrenjm.investingtracker.application.security.port.out.SessionStorePort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Redis adapter for revocable authentication sessions.
 *
 * <p>The token itself is deliberately not stored. Redis only tracks the application-owned session
 * record, keyed by the token's unique identifier.
 */
@Service
@RequiredArgsConstructor
public class RedisSessionStoreAdapter implements SessionStorePort {
    private static final String SESSION_KEY_PREFIX = "auth-session:";

    private final ReactiveRedisTemplate<String, SessionRecord> redisTemplate;

    @Override
    public Mono<Boolean> create(SessionRecord session, Duration ttl) {
        return redisTemplate.opsForValue().set(sessionKey(session.sessionId()), session, ttl);
    }

    @Override
    public Mono<Boolean> isActive(String sessionId) {
        return redisTemplate.opsForValue().get(sessionKey(sessionId)).hasElement();
    }

    @Override
    public Mono<Boolean> invalidate(String sessionId) {
        return redisTemplate.opsForValue().delete(sessionKey(sessionId));
    }

    private String sessionKey(String sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }
}
