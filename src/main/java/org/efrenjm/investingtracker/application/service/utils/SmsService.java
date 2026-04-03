package org.efrenjm.investingtracker.application.service.utils;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.ports.inbound.MessagePort;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.SmsPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SmsService implements MessagePort {
	private final SmsPort smsPort;

	@Override
	public Mono<Void> sendVerificationMessage(String phone, String verificationToken) {
		String body = "Your verification code is: " + verificationToken;
		return smsPort.sendWhatsApp(phone, body);
	}
}
