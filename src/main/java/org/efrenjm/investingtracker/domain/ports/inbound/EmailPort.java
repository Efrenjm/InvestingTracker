package org.efrenjm.investingtracker.domain.ports.inbound;

import reactor.core.publisher.Mono;

public interface EmailPort {
    Mono<Void> sendVerificationEmail(String email, String verificationToken);
}
