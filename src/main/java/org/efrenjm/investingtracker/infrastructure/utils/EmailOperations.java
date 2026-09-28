package org.efrenjm.investingtracker.infrastructure.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.EmailPort;
import org.efrenjm.investingtracker.infrastructure.logging.AppLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailOperations implements EmailPort {
    private final JavaMailSender mailSender;

    @Value(
            "${spring.mail.username:investing-tracker@sandbox6f1a2edd320341c7acb394d2ed82bcc9.mailgun.org}")
    private String fromAddress;

    @Override
    public Mono<Void> sendEmail(String to, String subject, String body) {
        return Mono.fromCallable(
                        () -> {
                            SimpleMailMessage message = new SimpleMailMessage();
                            message.setFrom(fromAddress);
                            message.setTo(to);
                            message.setSubject(subject);
                            message.setText(body);
                            return message;
                        })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(mailSender::send)
                .doOnSuccess(
                        v ->
                                AppLogger.success(
                                        log,
                                        "EMAIL-001",
                                        "sendEmail",
                                        "Email sent to " + to + " from " + fromAddress))
                .doOnError(
                        e ->
                                AppLogger.fail(
                                        log,
                                        "EMAIL-002",
                                        "sendEmail",
                                        "Failed to send email to " + to + " from " + fromAddress,
                                        e))
                .then();
    }
}
