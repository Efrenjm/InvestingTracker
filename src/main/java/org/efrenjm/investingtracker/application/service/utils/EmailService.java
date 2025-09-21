package org.efrenjm.investingtracker.application.service.utils;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.ports.inbound.EmailPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailPort
{
	private final org.efrenjm.investingtracker.domain.ports.outbound.utils.EmailPort emailPort;

	public Mono<Void> sendVerificationEmail(String email, String verificationToken) {
		String subject = "Please verify your email address";
		String body = "Use the following code to verify your email: " + verificationToken;
		return emailPort.sendEmail(email, subject, body);
	}
}
