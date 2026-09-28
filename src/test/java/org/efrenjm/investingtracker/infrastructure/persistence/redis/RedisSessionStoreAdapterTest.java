package org.efrenjm.investingtracker.infrastructure.persistence.redis;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import org.efrenjm.investingtracker.application.security.model.SessionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RedisSessionStoreAdapterTest {
    private static final String SESSION_ID = "session-123";
    private static final String SESSION_KEY = "auth-session:" + SESSION_ID;
    private static final Duration TTL = Duration.ofMinutes(15);

    @Mock private ReactiveRedisTemplate<String, SessionRecord> redisTemplate;
    @Mock private ReactiveValueOperations<String, SessionRecord> valueOperations;

    private RedisSessionStoreAdapter adapter;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        adapter = new RedisSessionStoreAdapter(redisTemplate);
    }

    @Test
    void createShouldWriteSessionWithTtlInSingleRedisOperation() {
        SessionRecord session = session();
        when(valueOperations.set(SESSION_KEY, session, TTL)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.create(session, TTL)).expectNext(true).verifyComplete();

        verify(valueOperations).set(SESSION_KEY, session, TTL);
    }

    @Test
    void isActiveShouldReturnTrueWhenSessionExists() {
        when(valueOperations.get(SESSION_KEY)).thenReturn(Mono.just(session()));

        StepVerifier.create(adapter.isActive(SESSION_ID)).expectNext(true).verifyComplete();

        verify(valueOperations).get(SESSION_KEY);
    }

    @Test
    void isActiveShouldReturnFalseWhenSessionIsAbsent() {
        when(valueOperations.get(SESSION_KEY)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.isActive(SESSION_ID)).expectNext(false).verifyComplete();
    }

    @Test
    void invalidateShouldDeleteOnlyTheAuthenticationSessionKey() {
        when(valueOperations.delete(SESSION_KEY)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.invalidate(SESSION_ID)).expectNext(true).verifyComplete();

        verify(valueOperations).delete(SESSION_KEY);
    }

    @Test
    void createShouldPropagateRedisErrors() {
        RuntimeException redisError = new RuntimeException("redis unavailable");
        when(valueOperations.set(SESSION_KEY, session(), TTL)).thenReturn(Mono.error(redisError));

        StepVerifier.create(adapter.create(session(), TTL))
                .expectError(RuntimeException.class)
                .verify();
    }

    private SessionRecord session() {
        Instant issuedAt = Instant.parse("2026-09-18T12:00:00Z");
        return new SessionRecord(SESSION_ID, "user-123", Set.of(), issuedAt, issuedAt.plus(TTL));
    }
}
