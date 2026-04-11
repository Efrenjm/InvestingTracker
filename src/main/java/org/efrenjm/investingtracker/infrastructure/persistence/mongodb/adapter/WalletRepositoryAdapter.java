package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.adapter;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.wallet.WalletEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.repository.AccountMongoRepository;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.repository.WalletMongoRepository;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.AccountSummaryProjection;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class WalletRepositoryAdapter implements WalletRepositoryPort {
	private final WalletMongoRepository walletMongoRepository;
	private final AccountMongoRepository accountMongoRepository;

	@Override
	public Mono<Wallet> save(Wallet wallet) {
		return walletMongoRepository.save(WalletEntity.fromDomain(wallet))
				.map(WalletEntity::toDomain);
	}

	@Override
	public Mono<Wallet> findById(String walletId) {
		return walletMongoRepository.findById(new ObjectId(walletId))
				.map(WalletEntity::toDomain);
	}

	@Override
	public Flux<AccountSummary> fetchAccounts(String walletId) {
		return accountMongoRepository.findWalletAccounts(new ObjectId(walletId))
				.map(AccountSummaryProjection::toDomain);
	}

	@Override
	public Mono<Void> delete(String walletId) {
		return walletMongoRepository.deleteById(new ObjectId(walletId));
	}

	@Override
	public Flux<Wallet> findByVisibility(org.efrenjm.investingtracker.domain.model.wallet.Visibility visibility) {
		return walletMongoRepository.findByVisibility(visibility)
				.map(WalletEntity::toDomain);
	}
}
