package org.efrenjm.investingtracker.domain.ports.inbound;

import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface AuthPort {
    Mono<User> login(String username, String password, ServerWebExchange exchange);

    Mono<Void> logout(UserIdentity user, ServerWebExchange exchange);

    Mono<User> register(String username, String password);

    Mono<User> refreshVerificationCode(String userId);

    Mono<User> refreshVerificationCode(User user);

    Mono<User> verifyCode(String userId, String code);

    Mono<User> verifyCode(User user, String code);

    Mono<User> updateCredential(User user, CodeUsage codeUsage, String credential);

    Mono<User> forgotPassword(String username, String newPassword);

    Mono<User> updatePassword(User user, String newPassword, String oldPassword);
}
