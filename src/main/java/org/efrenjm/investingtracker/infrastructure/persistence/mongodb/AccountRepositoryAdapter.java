package org.efrenjm.investingtracker.infrastructure.persistence.mongodb;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.account.Account;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.AccountRepositoryPort;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.AccountEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {
	private final AccountMongoRepository accountMongoRepository;

	@Override
	public Mono<Account> save(Account account) {
		return accountMongoRepository.save(AccountEntity.fromDomain(account))
				.map(AccountEntity::toDomain);
	}

	@Override
	public Mono<Account> findById(String accountId) {
		return accountMongoRepository.findById(new ObjectId(accountId))
				.map(AccountEntity::toDomain);
	}

	@Override
	public List<Account> findAll() {
		return List.of();
	}
}
