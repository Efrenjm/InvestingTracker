package org.efrenjm.investingtracker.interfaces.rest.account_management;

import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.infrastructure.security.SecurityUser;
import org.efrenjm.investingtracker.interfaces.annotations.AuthenticatedUser;
import org.efrenjm.investingtracker.application.dto.controller.account_management.CreateAccountRequestDTO;
import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.application.service.account_management.AccountService;
import org.efrenjm.investingtracker.application.service.transaction_management.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountManagementController {
	private final AccountService accountService;
	private final TransactionService transactionService;

	@GetMapping
	public Mono<ResponseEntity<List<AccountSummary>>> getAllAccounts(@RequestParam(required = false) String walletId,
	                                                                 @AuthenticatedUser SecurityUser securityUser) {
		User user = securityUser.getDomainUser();
		Mono<List<AccountSummary>> accounts;
		if (walletId == null) {
			accounts = accountService.getAllUserAccounts(user);
		} else if (user.getWallets().contains(walletId)) {
			accounts = accountService.getAllWalletAccounts(walletId);
		} else {
			return Mono.error(new IllegalArgumentException("Wallet not found"));
		}

		return accounts.map(ResponseEntity::ok);
	}

	@GetMapping("/{accountId}")
	public Mono<ResponseEntity<Void>> getAccount(@PathVariable String accountId) {
		return null;
	}

	@PostMapping
	public Mono<ResponseEntity<Void>> createAccount(@RequestBody CreateAccountRequestDTO accountToCreate) {
		return null;
	}

	@PutMapping("/{accountId}")
	public Mono<ResponseEntity<Void>> updateAccount(@PathVariable String accountId) {
		return null;
	}

	@DeleteMapping("/{accountId}")
	public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable String accountId) {
		return null;
	}

	// @GetMapping("/{accountId}/transactions")
	// public TransactionSummary getAccountTransaction(
	// @PathVariable ObjectId accountId,
	// @RequestParam(required = false) Date startDate,
	// @RequestParam(required = false) Date endDate,
	// @RequestParam(required = false) int page,
	// @RequestParam(required = false) int size,
	// @AuthenticatedUser User user
	// ) {
	// return transactionManagementService.getAccountTransactions(accountId,
	// startDate, endDate, page, size, user)
	// .collectList()
	// .map(ResponseEntity::ok)
	// .block();
	// }
}
