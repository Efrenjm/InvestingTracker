//package org.efrenjm.investingtracker.interfaces.rest.user_management;
//
//import lombok.AllArgsConstructor;
//import org.efrenjm.investingtracker.domain.model.user.User;
//import org.efrenjm.investingtracker.infrastructure.persistence.redis.UserSession;
//import org.efrenjm.investingtracker.interfaces.annotations.AuthUser;
//import org.efrenjm.investingtracker.application.dto.controller.user_management.Profile;
//import org.efrenjm.investingtracker.interfaces.rest.controller.user_management.dto.ProfileUpdateRequestDTO;
//import org.efrenjm.investingtracker.domain.dto.WalletSummary;
//import org.efrenjm.investingtracker.domain.dto.PublicProfile;
//import org.efrenjm.investingtracker.application.service.user_service.UserService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@AllArgsConstructor
//@RequestMapping("/user")
//public class UserManagementController {
//	private final UserService userService;
//
//	@GetMapping
//	public Mono<ResponseEntity<Profile>> fetchProfile(@AuthUser UserSession userSession) {
//		return Mono.just(ResponseEntity.ok(new Profile(userSession.getDomainUser())));
//	}
//
//	@PutMapping
//	public Mono<ResponseEntity<Profile>> updateProfile(@RequestBody ProfileUpdateRequestDTO updateRequest,
//	                                                   @AuthUser UserSession userSession) {
//		return userService.updateProfile(userSession.getDomainUser(), updateRequest)
//				.map(savedProfile -> ResponseEntity.ok(new Profile(savedProfile)));
//	}
//
//	@DeleteMapping
//	public Mono<ResponseEntity<Void>> deleteProfile(@AuthUser UserSession userSession) {
//		return userService.deleteUser(userSession.getDomainUser())
//				.thenReturn(ResponseEntity.noContent().build());
//	}
//
//	@GetMapping("/friends") /* TODO: Get friends */
//	public Mono<ResponseEntity<List<PublicProfile>>> getFriends(@AuthUser UserSession userSession) {
//		return userService.getFriends(userSession.getDomainUser())
//				.collectList()
//				.map(ResponseEntity::ok);
//	}
//
//	@PostMapping("/friends") /* TODO: add friend */
//	public Mono<ResponseEntity<User>> addFriend(@RequestBody String friendId, @AuthUser UserSession userSession) {
//		return userService.addFriend(userSession.getDomainUser(), friendId)
//				.map(ResponseEntity::ok);
//	}
//
//	@DeleteMapping("/friends") /* TODO: Delete friend */
//	public Mono<ResponseEntity<User>> deleteFriend(@RequestBody String friendId, @AuthUser UserSession userSession) {
//		return userService.removeFriend(userSession.getDomainUser(), friendId)
//				.map(ResponseEntity::ok);
//	}
//
//	@GetMapping("/wallet") /* TODO: Get wallets */
//	public Mono<ResponseEntity<List<WalletSummary>>> getWallets(@AuthUser UserSession userSession) {
//		return userService.getWallets(userSession.getDomainUser())
//				.collect(Collectors.toList())
//				.map(ResponseEntity::ok);
//	}
//
//	@PostMapping("/wallet") /* TODO: Add wallet */
//	public Mono<ResponseEntity<User>> joinWallet(@RequestBody String walletId, @AuthUser UserSession userSession) {
//		return userService.joinWallet(userSession.getDomainUser(), walletId)
//				.map(ResponseEntity::ok);
//	}
//
//	@DeleteMapping("/wallet") /* TODO: Delete wallet */
//	public Mono<ResponseEntity<User>> quitWallet(@RequestBody String walletId, @AuthUser UserSession userSession) {
//		return userService.quitWallet(userSession.getDomainUser(), walletId)
//				.map(ResponseEntity::ok);
//	}
//}
