package org.efrenjm.investingtracker.config;

import org.efrenjm.investingtracker.repository.AuthCredentialsRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

	private final AuthCredentialsRepository authCredentialsRepository;

	public CustomUserDetailsService(AuthCredentialsRepository authCredentialsRepository) {
		this.authCredentialsRepository = authCredentialsRepository;
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return authCredentialsRepository.findByUsername(username);
	}
}
