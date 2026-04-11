package org.efrenjm.investingtracker.domain.ports.inbound;

import org.efrenjm.investingtracker.domain.dto.ProfileUpdateCommand;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserPort
{
	Mono<User> updateProfile(UserIdentity user, ProfileUpdateCommand command);

	Mono<Void> deleteUser(UserIdentity user);

	Flux<PublicProfile> getFriends(UserIdentity user);

	Mono<User> addFriend(UserIdentity user, String friendId);

	Mono<Void> removeFriend(UserIdentity user, String friendToRemoveId);

	Flux<WalletSummary> getWallets(UserIdentity user);

	Mono<Void> joinWallet(UserIdentity user, String walletId);

	Mono<Void> quitWallet(UserIdentity user, String walletId);
}
