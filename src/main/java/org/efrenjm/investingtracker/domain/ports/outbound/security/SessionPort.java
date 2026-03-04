package org.efrenjm.investingtracker.domain.ports.outbound.security;

import org.efrenjm.investingtracker.domain.dto.Profile;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface SessionPort
{
	Mono<Profile> getUserSession(String userId);

	Mono<Boolean> storeUserSession(String userId, Profile profile, Duration duration);

	Mono<Boolean> invalidateSession(String userId);
}
