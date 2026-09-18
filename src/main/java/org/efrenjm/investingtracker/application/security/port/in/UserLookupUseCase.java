package org.efrenjm.investingtracker.application.security.port.in;

import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.model.user.User;
import reactor.core.publisher.Mono;

/**
 * User and profile lookup operations required by authenticated request adapters.
 */
public interface UserLookupUseCase
{
	Mono<User> loadUserByUsername(String username);

	Mono<User> loadUserByUserId(String userId);

	Mono<Profile> loadProfileByUserId(String userId);
}
