package org.efrenjm.investingtracker.application.service.user_service;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.ProfileUpdateCommand;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
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

@Service
@RequiredArgsConstructor
public class UserService implements UserPort
{
	private final UserRepositoryPort userRepository;
	private final WalletRepositoryPort walletRepository;

	@Override
	public Mono<User> updateProfile(UserIdentity user, ProfileUpdateCommand command) {
		return userRepository.findById(user.id())
				.switchIfEmpty(Mono.error(new UserNotFoundException(user.id())))
				.flatMap(u -> {
					ProfileUpdateCommand effectiveCommand = command.withDefaults(
							u.getUsername(),
							u.getFirstName(),
							u.getMiddleName(),
							u.getLastName(),
							u.getProfilePicture(),
							u.getPreferences()
					);

					u.setUsername(effectiveCommand.username());
					u.setFirstName(effectiveCommand.firstName());
					u.setMiddleName(effectiveCommand.middleName());
					u.setLastName(effectiveCommand.lastName());
					u.setProfilePicture(effectiveCommand.profilePicture());
					u.setPreferences(effectiveCommand.userPreferences());

					return userRepository.save(u);
				});
	}

	@Override
	public Mono<Void> deleteUser(UserIdentity user) {
		return userRepository.delete(user.id());
	}

	@Override
	public Flux<PublicProfile> getFriends(UserIdentity user)
	{
		return userRepository.fetchFriends(user.id());
	}

	@Override
	public Mono<User> addFriend(UserIdentity user, String friendId)
	{
		return Mono.zip(
				userRepository.findById(user.id()).switchIfEmpty(Mono.error(new UserNotFoundException(user.id()))),
				userRepository.findById(friendId).switchIfEmpty(Mono.error(new UserNotFoundException(friendId)))
		).flatMap(tuple -> {
			User u = tuple.getT1();
			User friend = tuple.getT2();
			u.inviteFriend(friend);
			return Mono.zip(
					userRepository.save(u),
					userRepository.save(friend)
			).map(t -> t.getT1());
		});
	}

	@Override
	public Mono<Void> removeFriend(UserIdentity user, String friendToRemoveId)
	{
		return Mono.zip(
				userRepository.findById(user.id()).switchIfEmpty(Mono.error(new UserNotFoundException(user.id()))),
				userRepository.findById(friendToRemoveId).switchIfEmpty(Mono.error(new UserNotFoundException(friendToRemoveId)))
		).flatMap(tuple -> {
			User u = tuple.getT1();
			User friend = tuple.getT2();
			u.removeFriend(friend);
			return Mono.zip(
					userRepository.save(u),
					userRepository.save(friend)
			).then();
		});
	}

	@Override
	public Flux<WalletSummary> getWallets(UserIdentity user) {
		return userRepository.fetchWallets(user.id());
	}

	@Override
	public Mono<Void> joinWallet(UserIdentity userIdentity, String walletId) {
		return Mono.zip(
				userRepository.findById(userIdentity.id()).switchIfEmpty(Mono.error(new UserNotFoundException(userIdentity.id()))),
				walletRepository.findById(walletId).switchIfEmpty(Mono.error(new WalletNotFoundException(walletId)))
		).flatMap(tuple -> {
			User user = tuple.getT1();
			org.efrenjm.investingtracker.domain.model.wallet.Wallet wallet = tuple.getT2();
			user.linkWallet(wallet, "Member");
			return Mono.zip(
					walletRepository.save(wallet),
					userRepository.save(user)
			).then();
		});
	}

	@Override
	public Mono<Void> quitWallet(UserIdentity userIdentity, String walletToQuitId) {
		return Mono.zip(
				userRepository.findById(userIdentity.id()).switchIfEmpty(Mono.error(new UserNotFoundException(userIdentity.id()))),
				walletRepository.findById(walletToQuitId).switchIfEmpty(Mono.error(new WalletNotFoundException(walletToQuitId)))
		).flatMap(tuple -> {
			User user = tuple.getT1();
			org.efrenjm.investingtracker.domain.model.wallet.Wallet wallet = tuple.getT2();
			user.unlinkWallet(wallet);
			return Mono.zip(
					walletRepository.save(wallet),
					userRepository.save(user)
					).then();
		});
	}
}
