package org.efrenjm.investingtracker.domain.ports.outbound.utils;

import reactor.core.publisher.Mono;

public interface SmsPort {
    Mono<Void> sendSms(String to, String body);

    Mono<Void> sendWhatsApp(String to, String body);
}
