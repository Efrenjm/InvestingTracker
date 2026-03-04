package org.efrenjm.investingtracker.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.ports.outbound.security.SessionPort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUserSessionAdapter implements SessionPort
{
	private static final String SESSION_KEY_PREFIX = "session:";

	private final ReactiveRedisTemplate<String, UserSession> redisTemplate;

	public Mono<Profile> getUserSession(String userId)
	{
		return redisTemplate
				.opsForValue()
				.get(SESSION_KEY_PREFIX + userId)
				.cast(UserSession.class)
				.map(UserSession::toProfile);
	}

	public Mono<Boolean> storeUserSession(String userId, Profile profile, Duration expiration)
	{
		UserSession userSession = UserSession.fromProfile(profile);
		return redisTemplate
				.opsForValue()
				.set(SESSION_KEY_PREFIX + userId, userSession)
				.then(redisTemplate.expire(SESSION_KEY_PREFIX + userId, expiration));
	}

	public Mono<Boolean> invalidateSession(String userId)
	{
		return redisTemplate
				.opsForValue()
				.delete(SESSION_KEY_PREFIX + userId);
	}
}
