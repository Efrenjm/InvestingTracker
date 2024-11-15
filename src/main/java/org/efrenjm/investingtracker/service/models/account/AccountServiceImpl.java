package org.efrenjm.investingtracker.service.models.account;

import org.efrenjm.investingtracker.model.Account.Account;
import org.efrenjm.investingtracker.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Flux<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Mono<Account> getAccountById(String id) {
        return accountRepository.findById(id);
    }

    @Override
    public Mono<Account> createAccount(Account account) {
        return accountRepository.insert(account);
    }

    @Override
    public Mono<Account> updateAccount(String id, Account account) {
        account.setId(id);
        return accountRepository.save(account);
    }

    @Override
    public Mono<Void> deleteAccount(String id) {
        return accountRepository.deleteById(id);
    }
}
