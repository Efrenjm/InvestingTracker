package org.efrenjm.investingtracker.infrastructure.utils;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.EmailPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class EmailOperations implements EmailPort {
	private final JavaMailSender mailSender;

	@Override
	public Mono<Void> sendEmail(String to, String subject, String body) {
		return Mono.fromCallable(() -> {
					SimpleMailMessage message = new SimpleMailMessage();
					message.setTo(to);
					message.setSubject(subject);
					message.setText(body);
					return message;
				}).subscribeOn(Schedulers.boundedElastic())
				.doOnNext(mailSender::send)
				.then();
	}
}
