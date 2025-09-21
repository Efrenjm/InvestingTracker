package org.efrenjm.investingtracker.application.service.user_service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.application.dto.controller.user_management.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.WalletNotFoundException;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.UserNotFoundException;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.inbound.UserPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserPort
{
	private final UserRepositoryPort userRepository;
	private final WalletRepositoryPort walletRepository;

	public Mono<User> updateProfile(User user, ProfileUpdateRequestDTO updateRequest) {
		user.setUsername(updateRequest.getUsername());
		user.setFirstName(updateRequest.getFirstName());
		user.setMiddleName(updateRequest.getMiddleName());
		user.setLastName(updateRequest.getLastName());
		user.setProfilePicture(updateRequest.getProfilePicture());

		return userRepository.save(user);
	}

	/* TODO: Delete personal wallet */
	public Mono<Void> deleteUser(User user) {
//		Set<String> wallets = user.getWallets();
//		Set<String> friends = user.getFriends();

		// friends.forEach(friend -> friend.getFriends().remove(user));
		//
		// wallets.forEach(wallet -> {
		// if (wallet.getMembers().size() == 1) {
		// walletRepository.delete(wallet).subscribe(); /* TODO: Delete accounts */
		// } else {
		// wallet.getMembers()
		// .removeIf(member -> member.getUser().getId().equals(userId));
		// }
		// });

		// userService.fetchWallet(userId)
		// .flatMap(wallet -> wallet.getMembers().stream()
		// .filter(member -> member.getUser().getId().equals(userId))
		// .findFirst()
		// .map(member -> walletRepository.save(wallet))
		// .orElse(Mono.empty()))
		// .subscribe();
		return userRepository.delete(user.getId());
	}

	public Flux<PublicProfile> getFriends(User user)
	{
		return userRepository.fetchFriends(user.getId());
	}

	public Mono<User> addFriend(User user, String friendId)
	{
		return Mono.empty();
//		return userRepository.findById(friendId)
//				/* TODO: Add logic to invite friends */
//				.switchIfEmpty(Mono.error(new UserNotFoundException(new ObjectId(friendId))))
//				.flatMap(friendProfile -> {
//					user.getFriends().add(friendProfile.getId());
//					return userRepository.save(user);
//				});
	}

	// TODO: Implement logic from the domain
	public Mono<User> removeFriend(User user, String friendToRemoveId)
	{
//		user.removeFriend(friendToRemoveId);

//		Set<String> updatedFriendsList = user.getFriends().stream()
//				.filter(friendId -> !friendId.equals(friendToRemoveId))
//				.toList();
//
//		user.setFriends(updatedFriendsList);
//
//		return userRepository.save(user);
		return Mono.empty();
	}

	public Flux<WalletSummary> getWallets(User user) {
		return userRepository.fetchWallets(user.getId());
	}

	// TODO: Implement logic from the domain
	public Mono<User> joinWallet(User user, String walletId) {
		return walletRepository.findById(walletId)
				.switchIfEmpty(Mono.error(new WalletNotFoundException(walletId)))
				.flatMap(wallet -> {
//					user.getWallets().add(wallet.getId());
					return userRepository.save(user);
				});
	}

	// TODO: Implement logic from the domain
	public Mono<User> quitWallet(User user, String walletToQuitId) {
//		List<String> updatedWalletList = user.getWallets().stream()
//				.filter(walletId -> !walletId.equals(walletToQuitId))
//				.toList();
//
//		user.setWallets(updatedWalletList);
//
//		return userRepository.save(user);
		return Mono.empty();
	}
}
