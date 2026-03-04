package org.efrenjm.investingtracker.application.service.user_service;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.ProfileUpdateCommand;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.UserNotFoundException;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.WalletNotFoundException;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.inbound.UserPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
public class UserService implements UserPort
{
	private final UserRepositoryPort userRepository;
	private final WalletRepositoryPort walletRepository;

	public Mono<User> updateProfile(User user, ProfileUpdateCommand command) {
		// Apply command with fallback to current values for null fields
		ProfileUpdateCommand effectiveCommand = command.withDefaults(
				user.getUsername(),
				user.getFirstName(),
				user.getMiddleName(),
				user.getLastName(),
				user.getProfilePicture(),
				user.getPreferences()
		);

		user.setUsername(effectiveCommand.username());
		user.setFirstName(effectiveCommand.firstName());
		user.setMiddleName(effectiveCommand.middleName());
		user.setLastName(effectiveCommand.lastName());
		user.setProfilePicture(effectiveCommand.profilePicture());
		user.setPreferences(effectiveCommand.userPreferences());

		return userRepository.save(user);
	}

	// TODO: Implement wallet cleanup (unlink from shared wallets, delete personal wallets and their accounts)
	public Mono<Void> deleteUser(User user) {
		return userRepository.delete(user.getId());
	}

	public Flux<PublicProfile> getFriends(User user)
	{
		return userRepository.fetchFriends(user.getId());
	}

	public Mono<User> addFriend(User user, String friendId)
	{
		return userRepository.findById(friendId)
				.switchIfEmpty(Mono.error(new UserNotFoundException(friendId)))
				.flatMap(friend -> {
					user.inviteFriend(friend);
					return Mono.zip(
							userRepository.save(user),
							userRepository.save(friend)
					).map(Tuple2::getT1);
				});
	}

	public Mono<User> removeFriend(User user, String friendToRemoveId)
	{
		return userRepository.findById(friendToRemoveId)
				.switchIfEmpty(Mono.error(new UserNotFoundException(friendToRemoveId)))
				.flatMap(friend -> {
					user.removeFriend(friend);
					return Mono.zip(
							userRepository.save(user),
							userRepository.save(friend)
					).map(Tuple2::getT1);
				});
	}

	public Flux<WalletSummary> getWallets(User user) {
		return userRepository.fetchWallets(user.getId());
	}

	public Mono<User> joinWallet(User user, String walletId) {
		return walletRepository.findById(walletId)
				.switchIfEmpty(Mono.error(new WalletNotFoundException(walletId)))
				.flatMap(wallet -> {
					user.linkWallet(wallet, "Member");
					return Mono.zip(
							walletRepository.save(wallet),
							userRepository.save(user)
					).map(Tuple2::getT2);
				});
	}

	public Mono<User> quitWallet(User user, String walletToQuitId) {
		return walletRepository.findById(walletToQuitId)
				.switchIfEmpty(Mono.error(new WalletNotFoundException(walletToQuitId)))
				.flatMap(wallet -> {
					user.unlinkWallet(wallet);
					return Mono.zip(
							walletRepository.save(wallet),
							userRepository.save(user)
					).map(Tuple2::getT2);
				});
	}
}
