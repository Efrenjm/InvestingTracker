package org.efrenjm.investingtracker.application.security.port.out;

import org.efrenjm.investingtracker.application.security.model.SessionRecord;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Application-owned contract for storing and revoking authenticated sessions.
 */
public interface SessionStorePort
{
	Mono<Boolean> create(SessionRecord session, Duration ttl);

	Mono<Boolean> isActive(String sessionId);

	Mono<Boolean> invalidate(String sessionId);
}
