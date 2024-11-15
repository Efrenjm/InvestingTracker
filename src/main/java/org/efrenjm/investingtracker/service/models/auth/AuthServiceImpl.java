package org.efrenjm.investingtracker.service.models.auth;

import org.efrenjm.investingtracker.model.Auth.Auth;
import org.efrenjm.investingtracker.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthRepository authRepository;

    @Override
    public Mono<Boolean> accountExists(String id) {
        return authRepository.existsById(id);
    }
}
