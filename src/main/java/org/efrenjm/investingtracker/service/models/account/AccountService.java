package org.efrenjm.investingtracker.service.models.account;

import org.efrenjm.investingtracker.model.Account.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    Flux<Account> getAllAccounts();

    Mono<Account> getAccountById(String id);

    Mono<Account> createAccount(Account account);

    Mono<Account> updateAccount(String id, Account account);

    Mono<Void> deleteAccount(String id);
}
