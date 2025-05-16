package org.efrenjm.investingtracker.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.ports.inbound.SecurityPort;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {
	private final SecurityPort securityPort;

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return securityPort.loadUserByUsername(username);
	}

	public Mono<UserDetails> findByUserId(String userId) {
		return securityPort.loadUserByUserId(userId);
	}
}
