package org.efrenjm.investingtracker.service.authentication;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface IAuthenticationService {
	Mono<Void> login(String email, String phone, String password, ServerWebExchange exchange);

	Mono<AuthCredentials> register(RegisterRequestDTO user);

	Mono<Profile> verifyToken(ObjectId userId, String token);

	Mono<Boolean> generateNewVerificationToken(ObjectId userId);
}
