package org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("wallet")
public class WalletManagementController {
	@GetMapping
	public Mono<List<WalletSummary>> getAllWallets() {
		return null;
	}

	@GetMapping("/{walletId}")
	public Mono<Wallet> getWallet(@PathVariable String walletId) {
		return null;
	}

	@PostMapping
	public Mono<Wallet> createWallet(Wallet wallet) {
		return null;
	}

	@PutMapping("/{walletId}")
	public Mono<Wallet> updateWallet(@PathVariable String walletId, Wallet wallet) {
		return null;
	}

	@DeleteMapping("/{walletId}")
	public Mono<Void> deleteWallet(@PathVariable String walletId) {
		return null;
	}
}
