package org.efrenjm.investingtracker.domain.ports.outbound.security;

import org.efrenjm.investingtracker.infrastructure.persistence.redis.UserSession;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface SessionPort
{
	Mono<UserSession> getUserSession(String userId);

	Mono<Boolean> storeUserSession(String userId, UserSession userSession, Duration duration);

	Mono<Boolean> invalidateSession(String userId);
}
