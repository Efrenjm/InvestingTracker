package org.efrenjm.investingtracker.service.authentication;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.service.model.AuthCredentialsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService {

	private final AuthCredentialsService authCredentialsService;

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return null;
	}

	public Mono<AuthCredentials> findById(ObjectId userId) {
		return authCredentialsService.fetchUser(userId);
	}
}
