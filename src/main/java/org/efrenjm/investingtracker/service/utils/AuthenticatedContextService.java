package org.efrenjm.investingtracker.service.utils;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.service.model.AuthCredentialsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticatedContextService implements IAuthenticatedContextService {
	private final AuthCredentialsService authCredentialsService;

	public Mono<AuthCredentials> getAuthenticatedUserCredentials() {
		return ReactiveSecurityContextHolder.getContext()
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getPrincipal)
				.cast(AuthCredentials.class);
	}

	public Mono<Profile> getAuthenticatedProfile() {
		return ReactiveSecurityContextHolder.getContext()
				.map(auth -> auth.getAuthentication().getPrincipal())
				.cast(AuthCredentials.class)
				.flatMap(user -> authCredentialsService.fetchUserProfile(user.getId()));
	}
}
