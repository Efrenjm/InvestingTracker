package org.efrenjm.investingtracker.controller.user_management;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.user_management.ProfileResponseDTO;
import org.efrenjm.investingtracker.dto.user_management.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.service.user_management.UserManagementService;
import org.efrenjm.investingtracker.service.utils.AuthenticatedContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/user-management")
public class UserManagementController {
	private final UserManagementService userManagementService;
	private final AuthenticatedContextService authenticatedContextService;

	@GetMapping("/profile")
	public Mono<ResponseEntity<ProfileResponseDTO>> fetchProfile() {
		return authenticatedContextService.getAuthenticatedProfile()
				.map(profile -> ResponseEntity.ok(new ProfileResponseDTO(profile)));
	}

	@PutMapping("/profile")
	public Mono<ResponseEntity<ProfileResponseDTO>> updateProfile(@RequestBody ProfileUpdateRequestDTO updateRequest) {
		return authenticatedContextService.getAuthenticatedProfile()
				.flatMap(currentProfile -> userManagementService.updateProfile(currentProfile, updateRequest))
				.map(savedProfile -> ResponseEntity.ok(new ProfileResponseDTO(savedProfile)));
	}

	@DeleteMapping("/profile")
	public Mono<ResponseEntity<Void>> deleteProfile() {
		return authenticatedContextService.getAuthenticatedUserCredentials()
				.flatMap(userManagementService::deleteUser)
				.thenReturn(ResponseEntity.noContent().build());
	}

	@GetMapping("/friends") /* TODO: Get friends */
	public Mono<ResponseEntity<List<Profile>>> getFriends() {
		return authenticatedContextService.getAuthenticatedProfile()
				.flatMap(userId -> userManagementService.getFriends(userId).collect(Collectors.toList()))
				.map(ResponseEntity::ok);
	}

	@PostMapping("/friends") /* TODO: add friend */
	public Mono<ResponseEntity<Profile>> addFriend(@RequestBody ObjectId friendId) {
		return authenticatedContextService.getAuthenticatedProfile()
				.flatMap(userId -> userManagementService.addFriend(userId, friendId))
				.map(ResponseEntity::ok);
	}

	@DeleteMapping("/friends") /* TODO: Delete friend */
	public Mono<ResponseEntity<Profile>> deleteFriend(@RequestBody ObjectId friendId) {
		return authenticatedContextService.getAuthenticatedProfile()
				.flatMap(userId -> userManagementService.removeFriend(userId, friendId))
				.map(ResponseEntity::ok);
	}

	@GetMapping("/organizations") /* TODO: Get organizations */
	public Mono<ResponseEntity<List<Organization>>> getOrganizations() {
		return authenticatedContextService.getAuthenticatedProfile()
				.flatMap(userId -> userManagementService.getOrganizations(userId).collect(Collectors.toList()))
				.map(ResponseEntity::ok);
	}

	@PostMapping("/organizations") /* TODO: Add organization */
	public Mono<ResponseEntity<Profile>> joinOrganization(@RequestBody ObjectId organizationId) {
		return authenticatedContextService.getAuthenticatedProfile()
				.flatMap(userId -> userManagementService.joinOrganization(userId, organizationId))
				.map(ResponseEntity::ok);
	}

	@DeleteMapping("/organizations") /* TODO: Delete organization */
	public Mono<ResponseEntity<Profile>> quitOrganization(@RequestBody ObjectId organizationId) {
		return authenticatedContextService.getAuthenticatedProfile()
				.flatMap(userId -> userManagementService.quitOrganization(userId, organizationId))
				.map(ResponseEntity::ok);
	}
}
