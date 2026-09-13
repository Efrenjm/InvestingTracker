package org.efrenjm.investingtracker.service.authentication.code_verification;

import org.efrenjm.investingtracker.model.user.CodeUsage;
import org.efrenjm.investingtracker.model.user.User;
import reactor.core.publisher.Mono;

public interface ICodeVerificationservice {
	Mono<Boolean> validate(User user, String token, CodeUsage codeUsage);

	Mono<User> createRequest(User user, CodeUsage codeUsage);
}
