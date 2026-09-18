package org.efrenjm.investingtracker.application.security.port.out;

import org.efrenjm.investingtracker.domain.dto.Profile;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Application-owned contract for the user profile cache.
 */
public interface ProfileCachePort
{
	Mono<Profile> getUserProfile(String userId);

	Mono<Boolean> storeUserProfile(String userId, Profile profile, Duration duration);

	Mono<Boolean> invalidateUserProfile(String userId);
}
