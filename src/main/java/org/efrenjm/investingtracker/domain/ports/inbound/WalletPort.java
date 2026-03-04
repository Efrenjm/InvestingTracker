package org.efrenjm.investingtracker.domain.ports.inbound;

import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import reactor.core.publisher.Mono;

public interface WalletPort
{
	Mono<Wallet> createWallet(Wallet wallet);
	Mono<Wallet> getWalletById(String walletId);
}
