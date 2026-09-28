package org.efrenjm.investingtracker.domain.ports.inbound;

import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletPort {
    Mono<Wallet> createWallet(
            UserIdentity user, String name, String description, Visibility visibility);

    Mono<Wallet> updateWallet(
            UserIdentity user,
            String walletId,
            String name,
            String description,
            Visibility visibility);

    Mono<Void> deleteWallet(UserIdentity user, String walletId);

    Mono<Wallet> getWalletById(UserIdentity user, String walletId);

    Flux<Wallet> getPublicWallets();

    Mono<Void> addMember(UserIdentity user, String walletId, String memberId, String roleName);

    Mono<Void> removeMember(UserIdentity user, String walletId, String memberId);
}
