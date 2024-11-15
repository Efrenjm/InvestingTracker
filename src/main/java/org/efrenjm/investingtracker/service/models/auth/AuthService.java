package org.efrenjm.investingtracker.service.models.auth;

import org.efrenjm.investingtracker.model.Auth.Auth;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<Boolean> accountExists(String id);
}
