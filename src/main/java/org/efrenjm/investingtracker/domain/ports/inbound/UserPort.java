package org.efrenjm.investingtracker.domain.ports.inbound;

import org.efrenjm.investingtracker.application.dto.controller.user_management.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserPort
{
	Mono<User> updateProfile(User user, ProfileUpdateRequestDTO updateRequest);

	Mono<Void> deleteUser(User user);

	Flux<PublicProfile> getFriends(User user);

	Mono<User> addFriend(User user, String friendId);

	Mono<User> removeFriend(User user, String friendToRemoveId);

	Flux<WalletSummary> getWallets(User user);

	Mono<User> joinWallet(User user, String walletId);

	Mono<User> quitWallet(User user, String walletId);
}
