package org.efrenjm.investingtracker.config;

import org.efrenjm.investingtracker.repository.AuthRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

	private final AuthRepository authRepository;

	public CustomUserDetailsService(AuthRepository authRepository) {
		this.authRepository = authRepository;
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return authRepository.findByUsername(username);
	}
}
