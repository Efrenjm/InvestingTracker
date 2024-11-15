package org.efrenjm.investingtracker.service.models.account;

import org.efrenjm.investingtracker.model.Account.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    public Flux<Account> getAllAccounts();
    public Mono<Account> getAccountById(String id);
}
