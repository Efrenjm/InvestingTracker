package org.efrenjm.investingtracker.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.security.port.out.ProfileCachePort;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Redis adapter for the profile cache. This data is independent from
 * revocable authentication sessions and uses a separate key namespace.
 */
@Service
@RequiredArgsConstructor
public class RedisProfileCacheAdapter implements ProfileCachePort
{
	private static final String PROFILE_CACHE_KEY_PREFIX = "session:";

	private final ReactiveRedisTemplate<String, UserSession> redisTemplate;

	@Override
	public Mono<Profile> getUserProfile(String userId)
	{
		return redisTemplate.opsForValue()
				.get(profileKey(userId))
				.map(UserSession::toProfile);
	}

	@Override
	public Mono<Boolean> storeUserProfile(String userId, Profile profile, Duration duration)
	{
		return redisTemplate.opsForValue()
				.set(profileKey(userId), UserSession.fromProfile(profile), duration);
	}

	@Override
	public Mono<Boolean> invalidateUserProfile(String userId)
	{
		return redisTemplate.opsForValue().delete(profileKey(userId));
	}

	private String profileKey(String userId)
	{
		return PROFILE_CACHE_KEY_PREFIX + userId;
	}
}
