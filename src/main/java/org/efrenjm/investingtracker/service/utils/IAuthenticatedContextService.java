package org.efrenjm.investingtracker.service.utils;

import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.model.profile.Profile;
import reactor.core.publisher.Mono;

public interface IAuthenticatedContextService {
	Mono<AuthCredentials> getAuthenticatedUserCredentials();

	Mono<Profile> getAuthenticatedProfile();
}
