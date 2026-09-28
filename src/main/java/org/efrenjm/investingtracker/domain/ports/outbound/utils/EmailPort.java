package org.efrenjm.investingtracker.domain.ports.outbound.utils;

import reactor.core.publisher.Mono;

public interface EmailPort {
    Mono<Void> sendEmail(String to, String subject, String body);
}
