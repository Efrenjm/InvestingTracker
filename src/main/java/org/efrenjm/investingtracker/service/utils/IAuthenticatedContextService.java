package org.efrenjm.investingtracker.service.utils;

import org.efrenjm.investingtracker.model.user.User;
import reactor.core.publisher.Mono;

public interface IAuthenticatedContextService {
	Mono<User> getAuthenticatedUser();
}
