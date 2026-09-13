package org.efrenjm.investingtracker.controller.user_management;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.config.AuthenticatedUser;
import org.efrenjm.investingtracker.dto.controller.user_management.ProfileResponseDTO;
import org.efrenjm.investingtracker.dto.controller.user_management.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.dto.model.organization.OrganizationSummary;
import org.efrenjm.investingtracker.model.user.User;
import org.efrenjm.investingtracker.dto.model.user.PublicProfile;
import org.efrenjm.investingtracker.service.user_management.UserManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserManagementController {
	private final UserManagementService userManagementService;

	@GetMapping
	public Mono<ResponseEntity<ProfileResponseDTO>> fetchProfile(@AuthenticatedUser User user) {
		return Mono.just(ResponseEntity.ok(new ProfileResponseDTO(user)));
	}

	@PutMapping
	public Mono<ResponseEntity<ProfileResponseDTO>> updateProfile(@RequestBody ProfileUpdateRequestDTO updateRequest,
	                                                              @AuthenticatedUser User user) {
		return userManagementService.updateProfile(user, updateRequest)
				.map(savedProfile -> ResponseEntity.ok(new ProfileResponseDTO(savedProfile)));
	}

	@DeleteMapping
	public Mono<ResponseEntity<Void>> deleteProfile(@AuthenticatedUser User user) {
		return userManagementService.deleteUser(user)
				.thenReturn(ResponseEntity.noContent().build());
	}

	@GetMapping("/friends") /* TODO: Get friends */
	public Mono<ResponseEntity<List<PublicProfile>>> getFriends(@AuthenticatedUser User user) {
		return userManagementService.getFriends(user)
				.collectList()
				.map(ResponseEntity::ok);
	}

	@PostMapping("/friends") /* TODO: add friend */
	public Mono<ResponseEntity<User>> addFriend(@RequestBody ObjectId friendId, @AuthenticatedUser User user) {
		return userManagementService.addFriend(user, friendId)
				.map(ResponseEntity::ok);
	}

	@DeleteMapping("/friends") /* TODO: Delete friend */
	public Mono<ResponseEntity<User>> deleteFriend(@RequestBody ObjectId friendId, @AuthenticatedUser User user) {
		return userManagementService.removeFriend(user, friendId)
				.map(ResponseEntity::ok);
	}

	@GetMapping("/organizations") /* TODO: Get organizations */
	public Mono<ResponseEntity<List<OrganizationSummary>>> getOrganizations(@AuthenticatedUser User user) {
		return userManagementService.getOrganizations(user)
				.collect(Collectors.toList())
				.map(ResponseEntity::ok);
	}

	@PostMapping("/organizations") /* TODO: Add organization */
	public Mono<ResponseEntity<User>> joinOrganization(@RequestBody ObjectId organizationId, @AuthenticatedUser User user) {
		return userManagementService.joinOrganization(user, organizationId)
				.map(ResponseEntity::ok);
	}

	@DeleteMapping("/organizations") /* TODO: Delete organization */
	public Mono<ResponseEntity<User>> quitOrganization(@RequestBody ObjectId organizationId, @AuthenticatedUser User user) {
		return userManagementService.quitOrganization(user, organizationId)
				.map(ResponseEntity::ok);
	}
}
