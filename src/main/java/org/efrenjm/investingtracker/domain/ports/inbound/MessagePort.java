package org.efrenjm.investingtracker.domain.ports.inbound;

import reactor.core.publisher.Mono;

public interface MessagePort {
    Mono<Void> sendVerificationMessage(String phone, String verificationToken);
}
