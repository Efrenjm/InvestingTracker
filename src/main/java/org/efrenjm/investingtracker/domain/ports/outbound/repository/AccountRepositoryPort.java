package org.efrenjm.investingtracker.domain.ports.outbound.repository;

import org.efrenjm.investingtracker.domain.model.account.BaseAccount;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountRepositoryPort {
	Mono<BaseAccount> save(BaseAccount account);
	Mono<BaseAccount> findById(String accountId);
	List<BaseAccount> findAll();
}
