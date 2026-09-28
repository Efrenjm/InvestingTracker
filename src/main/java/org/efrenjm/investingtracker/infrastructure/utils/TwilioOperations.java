package org.efrenjm.investingtracker.infrastructure.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.SmsPort;
import org.efrenjm.investingtracker.infrastructure.config.TwilioConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TwilioOperations implements SmsPort {

    private final TwilioConfig twilioConfig;

    @PostConstruct
    public void init() {
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

    @Override
    public Mono<Void> sendSms(String to, String body) {
        return Mono.fromCompletionStage(
                        Message.creator(
                                        new PhoneNumber(to),
                                        new PhoneNumber(twilioConfig.getFromNumber()),
                                        body)
                                .createAsync())
                .then();
    }

    @Override
    public Mono<Void> sendWhatsApp(String to, String body) {
        return Mono.fromCompletionStage(
                        Message.creator(
                                        new PhoneNumber("whatsapp:" + to),
                                        new PhoneNumber(
                                                "whatsapp:" + twilioConfig.getFromWhatsapp()),
                                        body)
                                .createAsync())
                .then();
    }
}
