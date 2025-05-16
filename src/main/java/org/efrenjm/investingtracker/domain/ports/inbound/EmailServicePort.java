package org.efrenjm.investingtracker.domain.ports.inbound;

import reactor.core.publisher.Mono;

public interface EmailServicePort {
	Mono<Void> sendVerificationEmail(String email, String verificationToken);
}
