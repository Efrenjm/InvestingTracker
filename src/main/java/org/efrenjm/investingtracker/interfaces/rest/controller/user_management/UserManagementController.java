package org.efrenjm.investingtracker.interfaces.rest.controller.user_management;

import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.inbound.UserPort;
import org.efrenjm.investingtracker.interfaces.annotations.AuthUser;
import org.efrenjm.investingtracker.interfaces.rest.controller.user_management.dto.ProfileUpdateRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserManagementController {
	private final UserPort userPort;

	@GetMapping
	public Mono<ResponseEntity<Profile>> fetchProfile(@AuthUser Profile profile) {
		return Mono.just(ResponseEntity.ok(profile));
	}

	@PutMapping
	public Mono<ResponseEntity<Profile>> updateProfile(@RequestBody ProfileUpdateRequestDTO updateRequest,
	                                                   @AuthUser User user) {
		return userPort.updateProfile(user, updateRequest.toCommand())
				.map(savedUser -> ResponseEntity.ok(Profile.from(savedUser)));
	}

	@DeleteMapping
	public Mono<ResponseEntity<Void>> deleteProfile(@AuthUser User user) {
		return userPort.deleteUser(user)
				.thenReturn(ResponseEntity.noContent().<Void>build());
	}

	@GetMapping("/friends")
	public Mono<ResponseEntity<List<PublicProfile>>> getFriends(@AuthUser User user) {
		return userPort.getFriends(user)
				.collectList()
				.map(ResponseEntity::ok);
	}

	@PostMapping("/friends")
	public Mono<ResponseEntity<PublicProfile>> addFriend(@RequestBody String friendId, @AuthUser User user) {
		return userPort.addFriend(user, friendId)
				.map(friend -> ResponseEntity.ok(PublicProfile.from(friend)));
	}

	@DeleteMapping("/friends")
	public Mono<ResponseEntity<Void>> deleteFriend(@RequestBody String friendId, @AuthUser User user) {
		return userPort.removeFriend(user, friendId)
				.thenReturn(ResponseEntity.noContent().<Void>build());
	}

	@GetMapping("/wallet")
	public Mono<ResponseEntity<List<WalletSummary>>> getWallets(@AuthUser User user) {
		return userPort.getWallets(user)
				.collectList()
				.map(ResponseEntity::ok);
	}

	@PostMapping("/wallet")
	public Mono<ResponseEntity<Void>> joinWallet(@RequestBody String walletId, @AuthUser User user) {
		return userPort.joinWallet(user, walletId)
				.thenReturn(ResponseEntity.noContent().<Void>build());
	}

	@DeleteMapping("/wallet")
	public Mono<ResponseEntity<Void>> quitWallet(@RequestBody String walletId, @AuthUser User user) {
		return userPort.quitWallet(user, walletId)
				.thenReturn(ResponseEntity.noContent().<Void>build());
	}
}
