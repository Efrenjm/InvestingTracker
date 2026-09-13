package org.efrenjm.investingtracker.service.user_management;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.controller.user_management.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.dto.model.organization.OrganizationSummary;
import org.efrenjm.investingtracker.exception.user_management.OrganizationNotFoundException;
import org.efrenjm.investingtracker.exception.user_management.UserNotFoundException;
import org.efrenjm.investingtracker.model.user.User;
import org.efrenjm.investingtracker.dto.model.user.PublicProfile;
import org.efrenjm.investingtracker.repository.OrganizationRepository;
import org.efrenjm.investingtracker.service.model.user.UserService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class UserManagementService implements IUserManagementService {
	private final UserService userService;
	private final OrganizationRepository organizationRepository;

	public Mono<User> updateProfile(User user, ProfileUpdateRequestDTO updateRequest) {
		user.setUsername(updateRequest.getUsername());
		user.setFirstName(updateRequest.getFirstName());
		user.setMiddleName(updateRequest.getMiddleName());
		user.setLastName(updateRequest.getLastName());
		user.setProfilePicture(updateRequest.getProfilePicture());

		return userService.saveUser(user); /* TODO: catch error */
	}

	/* TODO: Delete personal organization */
	public Mono<Void> deleteUser(User user) {
		ObjectId userId = user.getId();
		List<ObjectId> organizations = user.getOrganizations();
		List<ObjectId> friends = user.getFriends();

//		friends.forEach(friend -> friend.getFriends().remove(user));
//
//		organizations.forEach(organization -> {
//			if (organization.getMembers().size() == 1) {
//				organizationRepository.delete(organization).subscribe(); /* TODO: Delete accounts */
//			} else {
//				organization.getMembers()
//						.removeIf(member -> member.getUser().getId().equals(userId));
//			}
//		});

//		userService.fetchOrganizations(userId)
//				.flatMap(organization -> organization.getMembers().stream()
//						.filter(member -> member.getUser().getId().equals(userId))
//						.findFirst()
//						.map(member -> organizationRepository.save(organization))
//						.orElse(Mono.empty()))
//				.subscribe();
		return userService.deleteUser(userId);
	}

	public Flux<PublicProfile> getFriends(User user) {
		return userService.fetchFriends(user.getId());
	}

	public Mono<User> addFriend(User user, ObjectId friendId) {
		return userService.fetchUser(friendId)
				/* TODO: Add logic to invite friends */
				.switchIfEmpty(Mono.error(new UserNotFoundException(friendId.toString())))
				.flatMap(friendProfile -> {
					user.getFriends().add(friendProfile.getId());
					return userService.saveUser(user);
				});
	}

	public Mono<User> removeFriend(User user, ObjectId friendToRemoveId) {
		List<ObjectId> updatedFriendsList = user.getFriends().stream()
				.filter(friendId -> !friendId.equals(friendToRemoveId))
				.toList();

		user.setFriends(updatedFriendsList);

		return userService.saveUser(user);
	}

	public Flux<OrganizationSummary> getOrganizations(User user) {
		return userService.fetchOrganizations(user.getId());
	}

	public Mono<User> joinOrganization(User user, ObjectId organizationId) {
		return organizationRepository.findById(organizationId)
				.switchIfEmpty(Mono.error(new OrganizationNotFoundException(organizationId.toString())))
				.flatMap(organization -> {
					user.getOrganizations().add(organization.getId());
					return userService.saveUser(user);
				});
	}

	public Mono<User> quitOrganization(User user, ObjectId organizationToQuitId) {
		List<ObjectId> updatedOrganizationList = user.getOrganizations().stream()
				.filter(organizationId -> !organizationId.equals(organizationToQuitId))
				.toList();

		user.setOrganizations(updatedOrganizationList);

		return userService.saveUser(user);
	}
}
