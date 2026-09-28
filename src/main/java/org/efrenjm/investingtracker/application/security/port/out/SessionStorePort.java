package org.efrenjm.investingtracker.application.security.port.out;

import java.time.Duration;
import org.efrenjm.investingtracker.application.security.model.SessionRecord;
import reactor.core.publisher.Mono;

/** Application-owned contract for storing and revoking authenticated sessions. */
public interface SessionStorePort {
    Mono<Boolean> create(SessionRecord session, Duration ttl);

    Mono<Boolean> isActive(String sessionId);

    Mono<Boolean> invalidate(String sessionId);
}
