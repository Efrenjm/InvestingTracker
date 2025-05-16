package org.efrenjm.investingtracker.interfaces.rest.user_management;

import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.infrastructure.security.SecurityUser;
import org.efrenjm.investingtracker.interfaces.annotations.AuthenticatedUser;
import org.efrenjm.investingtracker.application.dto.controller.user_management.Profile;
import org.efrenjm.investingtracker.application.dto.controller.user_management.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.efrenjm.investingtracker.application.service.user_service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserManagementController {
	private final UserService userService;

	@GetMapping
	public Mono<ResponseEntity<Profile>> fetchProfile(@AuthenticatedUser SecurityUser securityUser) {
		return Mono.just(ResponseEntity.ok(new Profile(securityUser.getDomainUser())));
	}

	@PutMapping
	public Mono<ResponseEntity<Profile>> updateProfile(@RequestBody ProfileUpdateRequestDTO updateRequest,
	                                                   @AuthenticatedUser SecurityUser securityUser) {
		return userService.updateProfile(securityUser.getDomainUser(), updateRequest)
				.map(savedProfile -> ResponseEntity.ok(new Profile(savedProfile)));
	}

	@DeleteMapping
	public Mono<ResponseEntity<Void>> deleteProfile(@AuthenticatedUser SecurityUser securityUser) {
		return userService.deleteUser(securityUser.getDomainUser())
				.thenReturn(ResponseEntity.noContent().build());
	}

	@GetMapping("/friends") /* TODO: Get friends */
	public Mono<ResponseEntity<List<PublicProfile>>> getFriends(@AuthenticatedUser SecurityUser securityUser) {
		return userService.getFriends(securityUser.getDomainUser())
				.collectList()
				.map(ResponseEntity::ok);
	}

	@PostMapping("/friends") /* TODO: add friend */
	public Mono<ResponseEntity<User>> addFriend(@RequestBody String friendId, @AuthenticatedUser SecurityUser securityUser) {
		return userService.addFriend(securityUser.getDomainUser(), friendId)
				.map(ResponseEntity::ok);
	}

	@DeleteMapping("/friends") /* TODO: Delete friend */
	public Mono<ResponseEntity<User>> deleteFriend(@RequestBody String friendId, @AuthenticatedUser SecurityUser securityUser) {
		return userService.removeFriend(securityUser.getDomainUser(), friendId)
				.map(ResponseEntity::ok);
	}

	@GetMapping("/wallet") /* TODO: Get wallets */
	public Mono<ResponseEntity<List<WalletSummary>>> getWallets(@AuthenticatedUser SecurityUser securityUser) {
		return userService.getWallets(securityUser.getDomainUser())
				.collect(Collectors.toList())
				.map(ResponseEntity::ok);
	}

	@PostMapping("/wallet") /* TODO: Add wallet */
	public Mono<ResponseEntity<User>> joinWallet(@RequestBody String walletId, @AuthenticatedUser SecurityUser securityUser) {
		return userService.joinWallet(securityUser.getDomainUser(), walletId)
				.map(ResponseEntity::ok);
	}

	@DeleteMapping("/wallet") /* TODO: Delete wallet */
	public Mono<ResponseEntity<User>> quitWallet(@RequestBody String walletId, @AuthenticatedUser SecurityUser securityUser) {
		return userService.quitWallet(securityUser.getDomainUser(), walletId)
				.map(ResponseEntity::ok);
	}
}
