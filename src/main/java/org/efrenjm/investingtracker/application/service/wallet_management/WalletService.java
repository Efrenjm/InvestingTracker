package org.efrenjm.investingtracker.application.service.wallet_management;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.UserNotFoundException;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.WalletNotFoundException;
import org.efrenjm.investingtracker.application.service.wallet_management.exceptions.UnauthorizedActionException;
import org.efrenjm.investingtracker.application.service.wallet_management.exceptions.UnauthorizedWalletAccessException;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.inbound.WalletPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.efrenjm.investingtracker.domain.service.WalletDomainService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WalletService implements WalletPort
{
	private final WalletRepositoryPort walletRepository;
	private final UserRepositoryPort userRepository;
	private final WalletDomainService walletDomainService;

	@Override
	public Mono<Wallet> createWallet(UserIdentity user, String name, String description, Visibility visibility)
	{
		Wallet newWallet = walletDomainService.createWallet(user.id(), name, description, visibility);
		return walletRepository.save(newWallet)
				.flatMap(savedWallet -> userRepository.findById(user.id())
						.switchIfEmpty(Mono.error(new UserNotFoundException(user.id())))
						.flatMap(u -> {
							u.linkWallet(savedWallet, "Owner");
							return userRepository.save(u)
									.thenReturn(savedWallet);
						})
				);
	}

	@Override
	public Mono<Wallet> updateWallet(UserIdentity user, String walletId, String name, String description, Visibility visibility)
	{
		return walletRepository.findById(walletId)
				.switchIfEmpty(Mono.error(new WalletNotFoundException(walletId)))
				.flatMap(wallet -> {
					String role = wallet.findRoleOfUser(user.id()).orElse(null);
					if (role == null || (!role.equals("Owner") && !role.equals("Manager")))
					{
						return Mono.error(new UnauthorizedActionException("update", walletId));
					}
					wallet.setName(name);
					wallet.setDescription(description);
					wallet.setVisibility(visibility);
					return walletRepository.save(wallet);
				});
	}

	@Override
	public Mono<Void> deleteWallet(UserIdentity user, String walletId)
	{
		return walletRepository.findById(walletId)
				.switchIfEmpty(Mono.error(new WalletNotFoundException(walletId)))
				.flatMap(wallet -> {
					String role = wallet.findRoleOfUser(user.id()).orElse(null);
					if (role == null || !role.equals("Owner"))
					{
						return Mono.error(new UnauthorizedActionException("delete", walletId));
					}
					// TODO: Logic to unlink from all members before deleting
					return walletRepository.delete(walletId);
				});
	}

	@Override
	public Mono<Wallet> getWalletById(UserIdentity user, String walletId)
	{
		return walletRepository.findById(walletId)
				.switchIfEmpty(Mono.error(new WalletNotFoundException(walletId)))
				.flatMap(wallet -> {
					if (wallet.getVisibility() == Visibility.PUBLIC)
					{
						return Mono.just(wallet);
					}
					if (wallet.findRoleOfUser(user.id()).isPresent())
					{
						return Mono.just(wallet);
					}
					return Mono.error(new UnauthorizedWalletAccessException(walletId));
				});
	}

	@Override
	public Flux<Wallet> getPublicWallets()
	{
		return walletRepository.findByVisibility(Visibility.PUBLIC);
	}

	@Override
	public Mono<Void> addMember(UserIdentity user, String walletId, String memberId, String roleName)
	{
		return walletRepository.findById(walletId)
				.switchIfEmpty(Mono.error(new WalletNotFoundException(walletId)))
				.flatMap(wallet -> {
					String userRole = wallet.findRoleOfUser(user.id()).orElse(null);
					if (userRole == null || (!userRole.equals("Owner") && !userRole.equals("Manager")))
					{
						return Mono.error(new UnauthorizedActionException("add member", walletId));
					}

					return userRepository.findById(memberId)
							.switchIfEmpty(Mono.error(new UserNotFoundException(memberId)))
							.flatMap(member -> {
								member.linkWallet(wallet, roleName);
								return Mono.zip(
										walletRepository.save(wallet),
										userRepository.save(member)
								).then();
							});
				});
	}

	@Override
	public Mono<Void> removeMember(UserIdentity user, String walletId, String memberId)
	{
		return walletRepository.findById(walletId)
				.switchIfEmpty(Mono.error(new WalletNotFoundException(walletId)))
				.flatMap(wallet -> {
					String userRole = wallet.findRoleOfUser(user.id()).orElse(null);
					if (userRole == null || (!userRole.equals("Owner") && !userRole.equals("Manager")))
					{
						return Mono.error(new UnauthorizedActionException("remove member", walletId));
					}

					return userRepository.findById(memberId)
							.switchIfEmpty(Mono.error(new UserNotFoundException(memberId)))
							.flatMap(member -> {
								member.unlinkWallet(wallet);
								return Mono.zip(
										walletRepository.save(wallet),
										userRepository.save(member)
								).then();
							});
				});
	}
}
