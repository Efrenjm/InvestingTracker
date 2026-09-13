package org.efrenjm.investingtracker.service.utils;

import org.efrenjm.investingtracker.model.user.User;
import reactor.core.publisher.Mono;

public interface IJwtService {
	Mono<String> generateToken(User userDetails);

	boolean isTokenValid(String token);

	String extractUserId(String token);
}
