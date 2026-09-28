package org.efrenjm.investingtracker.domain.ports.outbound.repository;

import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletRepositoryPort {
    Mono<Wallet> save(Wallet wallet);

    Mono<Wallet> findById(String walletId);

    Flux<AccountSummary> fetchAccounts(String walletId);

    Mono<Void> delete(String walletId);

    Flux<Wallet> findByVisibility(Visibility visibility);
}
