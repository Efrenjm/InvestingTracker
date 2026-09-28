package org.efrenjm.investingtracker.application.service.account_management;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.inbound.AccountPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.AccountRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountPort {
    private final UserRepositoryPort userRepository;
    private final WalletRepositoryPort walletRepository;
    private final AccountRepositoryPort accountRepository;

    public Mono<List<AccountSummary>> getAllUserAccounts(User user) {
        return userRepository.fetchAccounts(user.getId()).collectList();
    }

    public Mono<List<AccountSummary>> getAllWalletAccounts(String walletId) {
        return walletRepository
                .fetchAccounts(walletId)
                .collectList()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Wallet not found")));
    }

    // TODO: Implement getAccountDetails - fetch account by ID and verify user access
    // TODO: Implement createAccount - validate user belongs to wallet, create via domain service,
    // link to wallet
    // TODO: Implement updateAccount - validate permissions, update via domain service
}
