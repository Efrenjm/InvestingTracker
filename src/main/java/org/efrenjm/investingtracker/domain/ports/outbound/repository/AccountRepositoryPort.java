package org.efrenjm.investingtracker.domain.ports.outbound.repository;

import org.efrenjm.investingtracker.domain.model.account.Account;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountRepositoryPort {
	Mono<Account> save(Account account);
	Mono<Account> findById(String accountId);
	List<Account> findAll();
}
