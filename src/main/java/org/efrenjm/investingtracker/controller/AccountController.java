package org.efrenjm.investingtracker.controller;

import org.efrenjm.investingtracker.model.Account.Account;
import org.efrenjm.investingtracker.service.models.account.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountServiceImpl accountService;

    @GetMapping
    public Flux<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Mono<Account> getAccountById(@PathVariable String id) {
        return accountService.getAccountById(id);
    }

    @GetMapping("/test")
    public Mono<Account> testConnection() {
        return accountService.getAllAccounts().next()
                .doOnSuccess(account -> System.out.println("Succesfully retrieved account: " + account))
                .doOnError(error -> System.out.println("Error retrieving account: " + error));
    }
}
