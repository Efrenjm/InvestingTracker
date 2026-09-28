package org.efrenjm.investingtracker.application.security.port.out;

import java.time.Duration;
import org.efrenjm.investingtracker.domain.dto.Profile;
import reactor.core.publisher.Mono;

/** Application-owned contract for the user profile cache. */
public interface ProfileCachePort {
    Mono<Profile> getUserProfile(String userId);

    Mono<Boolean> storeUserProfile(String userId, Profile profile, Duration duration);

    Mono<Boolean> invalidateUserProfile(String userId);
}
