package org.efrenjm.investingtracker.service.models.auth;

import org.efrenjm.investingtracker.model.Account.Account;
import org.efrenjm.investingtracker.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AccountRepository accountRepository;

    public Flux<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Mono<Account> getAccountById(String id) {
        return accountRepository.findById(id);
    }
}
