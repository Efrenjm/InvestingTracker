package org.efrenjm.investingtracker.service.authentication;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.controller.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.model.user.User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface IAuthenticationService {
	Mono<Void> login(String email, String phone, String password, ServerWebExchange exchange);

	Mono<User> register(RegisterRequestDTO user);

	Mono<User> verifyToken(ObjectId userId, String token);

	Mono<Boolean> generateNewVerificationToken(ObjectId userId);
}
