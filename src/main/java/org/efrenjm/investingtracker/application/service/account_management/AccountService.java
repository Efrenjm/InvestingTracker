package org.efrenjm.investingtracker.application.service.account_management;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.inbound.AccountPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.AccountRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountPort
{
	private final UserRepositoryPort userRepository;
	private final WalletRepositoryPort walletRepository;
	private final AccountRepositoryPort accountRepository;

	public Mono<List<AccountSummary>> getAllUserAccounts(User user) {
		return userRepository.fetchAccounts(user.getId()).collectList();
	}

	public Mono<List<AccountSummary>> getAllWalletAccounts(String walletId) {
		return walletRepository.fetchAccounts(walletId).collectList()
				.switchIfEmpty(Mono.error(new IllegalArgumentException("Wallet not found")));
	}

//	public Mono<Account> getAccountDetails(String accountId, UserEntity userEntity) {
//		return accountRepository.findById(accountId).single()
//				.switchIfEmpty(Mono.error(new IllegalArgumentException("Account not found")));
//	}

//	public Mono<Account> createAccount(Account account, ObjectId walletId, UserEntity userEntity) {
//		if (!userEntity.getWallets().contains(walletId)) {
//			return Mono.error(new IllegalArgumentException("User does not belong to the wallet"));
//		}
//		return null;
////		return accountRepository.createAccount(account, walletId)
////				.switchIfEmpty(Mono.error(new IllegalArgumentException("Failed to create account")));
//	}

	// public Mono<Account> updateAccount(Account account) {
	// return accountService.updateAccount(account, walletId)
	// .switchIfEmpty(Mono.error(new IllegalArgumentException("Failed to update
	// account")));
	// }
}
