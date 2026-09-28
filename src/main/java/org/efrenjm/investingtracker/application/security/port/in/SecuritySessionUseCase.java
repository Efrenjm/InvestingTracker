package org.efrenjm.investingtracker.application.security.port.in;

import reactor.core.publisher.Mono;

/** Inbound capability used by security adapters to validate session activity. */
public interface SecuritySessionUseCase {
    Mono<Boolean> isSessionActive(String sessionId);
}
