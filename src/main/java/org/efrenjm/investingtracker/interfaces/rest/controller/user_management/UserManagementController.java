package org.efrenjm.investingtracker.interfaces.rest.controller.user_management;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.ports.inbound.UserPort;
import org.efrenjm.investingtracker.infrastructure.logging.AppLogger;
import org.efrenjm.investingtracker.interfaces.annotations.AuthUser;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.AuthResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.user_management.dto.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.user_management.dto.UserWebDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserManagementController {
	private final UserPort userPort;

	@GetMapping
	public Mono<ResponseEntity<AuthResponseDTO>> fetchProfile(@AuthUser Profile profile) {
		AppLogger.info(log, "USER-001", "fetchProfile", "Fetching profile for user");
		if (profile == null) {
			AppLogger.warn(log, "USER-002", "fetchProfile", "Profile is null for authenticated request");
			return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
		}
		AppLogger.success(log, "USER-003", "fetchProfile", "Successfully fetched profile for userId: " + profile.id());
		return Mono.just(ResponseEntity.ok(new AuthResponseDTO(UserWebDTO.from(profile))));
	}

	@PutMapping
	public Mono<ResponseEntity<UserWebDTO>> updateProfile(@RequestBody ProfileUpdateRequestDTO updateRequest,
	                                                   @AuthUser UserIdentity user) {
		return userPort.updateProfile(user, updateRequest.toCommand())
				.map(savedUser -> ResponseEntity.ok(UserWebDTO.from(Profile.from(savedUser))));
	}

	@DeleteMapping
	public Mono<ResponseEntity<Void>> deleteProfile(@AuthUser UserIdentity user) {
		return userPort.deleteUser(user)
				.thenReturn(ResponseEntity.noContent().build());
	}

	@GetMapping("/friends")
	public Mono<ResponseEntity<List<PublicProfile>>> getFriends(@AuthUser UserIdentity user) {
		return userPort.getFriends(user)
				.collectList()
				.map(ResponseEntity::ok);
	}

	@PostMapping("/friends")
	public Mono<ResponseEntity<PublicProfile>> addFriend(@RequestBody String friendId, @AuthUser UserIdentity user) {
		return userPort.addFriend(user, friendId)
				.map(friend -> ResponseEntity.ok(PublicProfile.from(friend)));
	}

	@DeleteMapping("/friends")
	public Mono<ResponseEntity<Void>> deleteFriend(@RequestBody String friendId, @AuthUser UserIdentity user) {
		return userPort.removeFriend(user, friendId)
				.thenReturn(ResponseEntity.noContent().build());
	}

	@GetMapping("/wallet")
	public Mono<ResponseEntity<List<WalletSummary>>> getWallets(@AuthUser UserIdentity user) {
		return userPort.getWallets(user)
				.collectList()
				.map(ResponseEntity::ok);
	}

	@PostMapping("/wallet")
	public Mono<ResponseEntity<Void>> joinWallet(@RequestBody String walletId, @AuthUser UserIdentity user) {
		return userPort.joinWallet(user, walletId)
				.thenReturn(ResponseEntity.noContent().build());
	}

	@DeleteMapping("/wallet")
	public Mono<ResponseEntity<Void>> quitWallet(@RequestBody String walletId, @AuthUser UserIdentity user) {
		return userPort.quitWallet(user, walletId)
				.thenReturn(ResponseEntity.noContent().build());
	}
}
