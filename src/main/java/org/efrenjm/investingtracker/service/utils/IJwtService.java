package org.efrenjm.investingtracker.service.utils;

import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import reactor.core.publisher.Mono;

public interface IJwtService {
	public Mono<String> generateToken(AuthCredentials userDetails);

	boolean isTokenValid(String token);

	String extractUserId(String token);
}
